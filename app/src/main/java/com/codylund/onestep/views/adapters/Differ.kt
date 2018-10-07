package com.codylund.onestep.views.adapters

import android.support.v7.util.DiffUtil

class Differ<T: Differ.Diffable> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(first: T, second: T): Boolean {
        return first.sameAs(second)
    }

    override fun areContentsTheSame(first: T, second: T): Boolean {
        return first.equals(second)
    }

    interface Diffable {
        fun sameAs(other: Diffable): Boolean
    }

}