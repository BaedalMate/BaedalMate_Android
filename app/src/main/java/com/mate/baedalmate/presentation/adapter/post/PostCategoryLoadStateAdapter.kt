package com.mate.baedalmate.presentation.adapter.post

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class PostCategoryLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<PostCategoryLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: PostCategoryLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PostCategoryLoadStateViewHolder {
        return PostCategoryLoadStateViewHolder.create(parent, retry)
    }
}