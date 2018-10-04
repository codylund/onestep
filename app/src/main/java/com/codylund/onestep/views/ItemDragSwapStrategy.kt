package com.codylund.onestep.views

import android.support.v7.widget.RecyclerView

interface ItemDragSwapStrategy<T: RecyclerView.ViewHolder> {
    fun start()
    fun complete()
}