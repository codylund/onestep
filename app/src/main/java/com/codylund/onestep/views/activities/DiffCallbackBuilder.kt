package com.codylund.onestep.views.activities

import android.support.v7.util.DiffUtil
import com.codylund.onestep.views.adapters.Differ

class DiffCallbackBuilder<T : Differ.Diffable>(val itemDiffer: Differ<T>) {

    fun build(oldList: List<T>, newList: List<T>) : DiffUtil.Callback {
        return object: DiffUtil.Callback() {
            override fun areItemsTheSame(p0: Int, p1: Int): Boolean {
                return itemDiffer.areItemsTheSame(oldList[p0], newList[p1])
            }

            override fun getOldListSize(): Int {
                return oldList.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areContentsTheSame(p0: Int, p1: Int): Boolean {
                return itemDiffer.areContentsTheSame(oldList[p0], newList[p1])
            }
        }
    }
}