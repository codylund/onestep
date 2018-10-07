package com.codylund.onestep.views

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import java.util.logging.Logger

class ItemDragHelperCallback<T: RecyclerView.ViewHolder>(val strategy: ItemDragSwapStrategy<T>) : ItemTouchHelper.Callback() {

    private val LOGGER = Logger.getLogger(ItemDragHelperCallback::class.java.name)

    private var mLastActionState: Int = ItemTouchHelper.ACTION_STATE_IDLE
    private var mDraggingViewHolder: RecyclerView.ViewHolder? = null

    override fun isLongPressDragEnabled() = true
    override fun isItemViewSwipeEnabled() = false

    override fun getMovementFlags(p0: RecyclerView, p1: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(ItemTouchHelper.UP + ItemTouchHelper.DOWN, 0)
    }

    override fun onMove(p0: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        p0.adapter!!.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        LOGGER.info("State: $actionState")
        when (actionState) {
            ItemTouchHelper.ACTION_STATE_DRAG -> {
                viewHolder?.let {
                    mDraggingViewHolder = it
                    strategy.start(it.adapterPosition)
                }
            }
            ItemTouchHelper.ACTION_STATE_IDLE -> {
                mDraggingViewHolder?.let{
                    strategy.complete(it.adapterPosition)
                    mDraggingViewHolder = null
                }
            }
            else -> {
                // whatever
            }
        }
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
        // Do nothing; we don't support swipe
    }
}