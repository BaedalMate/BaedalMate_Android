package com.mate.baedalmate.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2

object ViewPagerUtil {
    fun previewNextItem(viewPager: ViewPager2, currentVisibleItemMargin: Int, nextItemMargin: Int) {
        viewPager.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State,
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.right = currentVisibleItemMargin
                outRect.left = currentVisibleItemMargin
            }
        })
        val pageTranslationX = nextItemMargin + currentVisibleItemMargin

        viewPager.offscreenPageLimit = 1
        var transform = CompositePageTransformer()
        transform.addTransformer(MarginPageTransformer(15.dp))
        transform.addTransformer(ViewPager2.PageTransformer{ view: View, fl: Float ->
            var v = 1-Math.abs(fl)
            view.scaleY = 0.8f + v * 0.2f
        })
        transform.addTransformer { page, position ->
            page.translationX = -pageTranslationX * (position)
        }

        viewPager.setPageTransformer(transform)
    }
}