package com.mate.baedalmate.presentation.adapter.post

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.mate.baedalmate.R
import com.mate.baedalmate.common.extension.setOnDebounceClickListener
import com.mate.baedalmate.databinding.ItemPostCategoryListLoadingFooterBinding

class PostCategoryLoadStateViewHolder(
    private val binding: ItemPostCategoryListLoadingFooterBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.btnPostCategoryListLoadingFooterRetry.setOnDebounceClickListener {
            retry.invoke()
        }
    }

    fun bind(loadState: LoadState) {
        with(binding) {
            if (loadState is LoadState.Error) {
                Log.e("LOAD ERROR", "${loadState.error.localizedMessage}")
            }
            lottiePostCategoryListLoadingFooter.isVisible = loadState is LoadState.Loading
            btnPostCategoryListLoadingFooterRetry.isVisible = loadState is LoadState.Error
            tvPostCategoryListLoadingFooterErrorGuide.isVisible = loadState is LoadState.Error
        }
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): PostCategoryLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post_category_list_loading_footer, parent, false)
            val binding = ItemPostCategoryListLoadingFooterBinding.bind(view)
            return PostCategoryLoadStateViewHolder(binding, retry)
        }
    }
}