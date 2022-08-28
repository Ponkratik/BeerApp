package com.ponkratov.beerapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ponkratov.beerapp.databinding.ItemBeerBinding
import com.ponkratov.beerapp.databinding.ItemErrorBinding
import com.ponkratov.beerapp.databinding.ItemLoadingBinding
import com.ponkratov.beerapp.model.Beer
import com.ponkratov.beerapp.model.PagingData

class BeerAdapter(
    context: Context,
    private val onBeerClicked: (Beer) -> Unit
) : ListAdapter<PagingData<Beer>, RecyclerView.ViewHolder>(DIFF_UTIL) {
    private val layoutInflater = LayoutInflater.from(context)

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is PagingData.Item -> TYPE_ITEM
            PagingData.Loading -> TYPE_LOADING
            PagingData.Error -> TYPE_ERROR
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TYPE_ITEM -> {
                BeerViewHolder(
                    binding = ItemBeerBinding.inflate(layoutInflater, parent, false),
                    onBeerClicked = onBeerClicked
                )
            }
            TYPE_LOADING -> {
                LoadingViewHolder(
                    binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                )
            }
            /*TYPE_ERROR -> {
                ErrorViewHolder(
                    binding = ItemErrorBinding.inflate(layoutInflater, parent, false),
                    onRefreshClicked = onActionButtonClicked
                )
            }*/
            else -> error("Unsupported viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)) {
            PagingData.Error -> {
                //no operation
            }
            is PagingData.Item -> {
                checkNotNull(holder as BeerViewHolder) { "Incorrect viewHolder $item" }
                holder.bind(item.data)
            }
            PagingData.Loading -> {
                //no operation
            }
        }
    }

    companion object {

        private const val TYPE_ITEM = 0
        private const val TYPE_LOADING = 1
        private const val TYPE_ERROR = 2

        private val DIFF_UTIL = object : DiffUtil.ItemCallback<PagingData<Beer>>() {
            override fun areItemsTheSame(
                oldItem: PagingData<Beer>,
                newItem: PagingData<Beer>
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PagingData<Beer>,
                newItem: PagingData<Beer>
            ): Boolean {
                val oldBeer = oldItem as? PagingData.Item
                val newBeer = newItem as? PagingData.Item
                return oldBeer == newBeer
            }
        }
    }
}

class BeerViewHolder(
    private val binding: ItemBeerBinding,
    private val onBeerClicked: (Beer) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Beer) {
        with(binding) {
            beerImage.load(item.imageUrl)
            beerName.text = item.name

            root.setOnClickListener { onBeerClicked(item) }
        }
    }
}

class LoadingViewHolder(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {

}

class ErrorViewHolder(
    binding: ItemErrorBinding,
    onRefreshClicked: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.button.setOnClickListener {
            onRefreshClicked()
        }
    }
}