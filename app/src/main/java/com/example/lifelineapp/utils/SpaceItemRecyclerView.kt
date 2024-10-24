package com.example.lifelineapp.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * This class adds the space between each item in recycler view
 */

class SpaceItemRecyclerView(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = space

        // Optionally, you can also add spacing to other sides
        // outRect.top = space
        // outRect.left = space
        // outRect.right = space
    }
}
