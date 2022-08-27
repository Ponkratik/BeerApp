package com.ponkratov.beerapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ponkratov.beerapp.R
import com.ponkratov.beerapp.databinding.FragmentBeerListBinding
import com.ponkratov.beerapp.extensions.addPaginationListener
import com.ponkratov.beerapp.extensions.addVerticalSpace
import com.ponkratov.beerapp.model.Beer
import com.ponkratov.beerapp.model.PagingData
import com.ponkratov.beerapp.service.BeerService
import com.ponkratov.beerapp.view.adapter.BeerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class BeerListFragment : Fragment() {

    private var _binding: FragmentBeerListBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val adapter by lazy {
        BeerAdapter(
            context = requireContext(),
            onBeerClicked = {
                findNavController().navigate(
                    BeerListFragmentDirections.toBeerInfo(
                        it.name,
                        it.tagline,
                        it.imageUrl,
                        it.brewersTips
                    )
                )
            }
        )
    }

    private var currentPage = 1
    private var currentBeers = mutableListOf<Beer>()

    private var currentRequest: Call<List<Beer>>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentBeerListBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            beerRecyclerView.adapter = adapter
            beerRecyclerView.addVerticalSpace(R.dimen.list_vertical_space)
            beerRecyclerView.addPaginationListener(
                beerRecyclerView.layoutManager as LinearLayoutManager,
                ITEMS_TO_LOAD
            ) {
                executeRequest {

                }
            }

            layoutSwiperefresh.setOnRefreshListener {
                currentPage = 1
                currentRequest?.cancel()
                currentRequest = null

                executeRequest {
                    layoutSwiperefresh.isRefreshing = false
                }
            }
        }

        executeRequest()
    }

    private fun handleException(e: Throwable) {
        Toast.makeText(requireContext(), e.message ?: "Something went wrong", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun executeRequest(
        onRequestFinished: () -> Unit = {}
    ) {
        val finishRequest = {
            onRequestFinished()
            currentRequest = null
        }

        if (currentRequest != null) return

        currentRequest = BeerService.beerApi
            .getBeers(currentPage, ITEMS_PER_PAGE)
            .apply {
                enqueue(object : Callback<List<Beer>> {
                    override fun onResponse(
                        call: Call<List<Beer>>,
                        response: Response<List<Beer>>
                    ) {
                        if (response.isSuccessful) {
                            val newBeers = adapter.currentList
                                .dropLastWhile { it == PagingData.Loading }
                                .plus(response.body()?.map { PagingData.Item(it) }.orEmpty())
                                .plus(PagingData.Loading)
                            adapter.submitList(newBeers)
                            currentPage++
                        } else {
                            handleException(HttpException(response))
                        }

                        finishRequest()
                    }

                    override fun onFailure(call: Call<List<Beer>>, t: Throwable) {
                        if (!call.isCanceled) {
                            handleException(t)
                        }

                        finishRequest()
                    }

                })
            }
    }

    companion object {
        private const val ITEMS_PER_PAGE = 25
        private const val ITEMS_TO_LOAD = 15
    }
}