package com.codylund.onestep.views

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.codylund.onestep.ItemDragSwapStrategy

class ItemDragHelperCallback<T: RecyclerView.ViewHolder>(val strategy: ItemDragSwapStrategy<T>) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = true
    override fun isItemViewSwipeEnabled() = false

    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(ItemTouchHelper.UP + ItemTouchHelper.DOWN, 0)
    }

    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        p0.adapter!!.notifyItemMoved(p1.adapterPosition, p2.adapterPosition)
        strategy.swap(p1 as T, p2 as T)
        return true
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
        // Do nothing; we don't support swipe
    }
}