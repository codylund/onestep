package com.codylund.onestep

import android.support.v7.widget.RecyclerView

interface ItemDragSwapStrategy<T: RecyclerView.ViewHolder> {
    fun swap(firstItem: T, secondItem: T)
    fun complete()
}