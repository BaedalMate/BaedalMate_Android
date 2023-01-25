package com.mate.baedalmate.presentation.adapter.post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.mate.baedalmate.R
import com.mate.baedalmate.databinding.ItemSpinnerPostCategorySortListBinding

class PostCategoryListSortSpinnerAdapter(
    context: Context,
    @LayoutRes private val resId: Int,
    private val values: MutableList<String>
) : ArrayAdapter<String>(context, resId, values) {
    override fun getCount(): Int = values.size

    override fun getItem(position: Int): String? = values[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemSpinnerPostCategorySortListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val model = values[position]
        try {
            with(binding) {
                imgSpinnerArrow.visibility = View.VISIBLE
                with(tvSpinnerSort) {
                    text = model
                    setTextColor(ContextCompat.getColor(context, R.color.black_000000))
                    setTextAppearance(R.style.style_semi_title_kor)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemSpinnerPostCategorySortListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val model = values[position]
        try {
            binding.tvSpinnerSort.text = model
            if(position == 0) {
//                binding.tvSpinnerDormitory.setTextAppearance(R.style.style_semi_title_kor)
                binding.imgSpinnerArrow.visibility = View.VISIBLE
            }
            if(position == values.lastIndex) {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }
}