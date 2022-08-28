package com.ponkratov.beerapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy
import androidx.navigation.fragment.navArgs
import coil.load
import com.ponkratov.beerapp.databinding.FragmentBeerInfoBinding

class BeerInfoFragment : Fragment() {
    private var _binding: FragmentBeerInfoBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val args by navArgs<BeerInfoFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentBeerInfoBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {
            image.load(args.imageUrl)
            textViewName.text = args.name
            textViewTagline.text = args.tagline
            textViewBrewersTips.text = args.brewersTips
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}