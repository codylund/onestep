package com.codylund.onestep.views.adapters

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.codylund.onestep.*
import com.codylund.onestep.R.layout.item_step
import com.codylund.onestep.models.Step
import com.codylund.onestep.models.StepStatus
import com.codylund.onestep.utils.StringUtils
import com.codylund.onestep.views.Animations
import com.codylund.onestep.views.ItemDragHelperCallback
import com.codylund.onestep.views.ItemDragSwapStrategy
import com.codylund.onestep.views.StepStatusView
import java.util.logging.Logger

class StepAdapter : ListAdapter<Step, StepAdapter.StepViewHolder>(DIFF_CALLBACK) {

    val LOGGER = Logger.getLogger(StepAdapter::class.java.name)

    companion object {
        val DIFF_CALLBACK = Differ<Step>()
    }

    lateinit var mRecyclerView: RecyclerView
    lateinit var mItemTouchHelper: ItemTouchHelper

    class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Step data views
        var whatView = itemView.findViewById<TextView>(R.id.whatView)
        var whenView = itemView.findViewById<TextView>(R.id.whenView)
        var whereView = itemView.findViewById<TextView>(R.id.whereView)
        var whyView = itemView.findViewById<TextView>(R.id.whyView)

        // Status views
        var statusView = itemView.findViewById<StepStatusView>(R.id.status)
        var topLineView = itemView.findViewById<ImageView>(R.id.topLine)
        var bottomLineView = itemView.findViewById<ImageView>(R.id.bottomLine)

        fun bindStepData(step: Step) {
            // Display the text data
            displayData(whatView, step.getWhat())
            displayData(whenView, step.getWhen())
            displayData(whereView, step.getWhere())
            displayData(whyView, step.getWhy())

            // Update the status view
            statusView.setStatus(step.getStatus())
        }

        private fun displayData(view: TextView, data: String?) {
            if (!StringUtils.isEmpty(data)) {
                view.visibility = VISIBLE
                view.text = data
            } else {
                view.visibility = GONE
            }
        }

        fun drawToLast(shouldDraw: Boolean) = animateLine(topLineView, shouldDraw)

        fun drawToNext(shouldDraw: Boolean) = animateLine(bottomLineView, shouldDraw)

        private fun animateLine(imageView: ImageView, shouldDraw: Boolean) {
            val alpha: Float
            val delay: Long
            if (shouldDraw) {
                alpha = 1.0f
                delay = 500
            } else {
                alpha = 0.0f
                delay = 0
            }
            Animations.fadeWithDelay(imageView, delay, alpha)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView

        // Setup touch helper for drag and drop support
        mItemTouchHelper = ItemTouchHelper(ItemDragHelperCallback(object: ItemDragSwapStrategy<StepViewHolder> {
            override fun start() = connectTheDots(shouldConnect = false)
            override fun complete() = connectTheDots(shouldConnect = true)
        }))
        mItemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, id: Int): StepViewHolder {
        return StepViewHolder(LayoutInflater.from(parent.context).inflate(item_step, parent, false))
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        var step = getItem(position)
        holder.bindStepData(step)

        holder.statusView.setOnClickListener {
            holder.statusView.toggleStatus()
            Animations.clickStepStatus(it)
            connectTheDots()
        }

        // TODO - probably shouldn't call this so often
        connectTheDots()
    }

    /**
     * Connect the dots according the the corresponding path statuses.
     */
    private fun connectTheDots(shouldConnect: Boolean = true) {
        var i = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        var last = (mRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        while(connectableFrom(i) && i <= last) {
            // Get the adjacent status views
            var fromStep = mRecyclerView?.findViewHolderForAdapterPosition(i) as StepViewHolder
            var toStep = mRecyclerView?.findViewHolderForAdapterPosition(i + 1) as StepViewHolder

            // Connect them if both are completed
            if (shouldConnect && fromStep.statusView.mStatus == StepStatus.DONE && toStep.statusView.mStatus == StepStatus.DONE) {
                fromStep.drawToNext(true)
                toStep.drawToLast(true)
            } else {
                fromStep.drawToNext(false)
                toStep.drawToLast(false)
            }

            // On to the next set
            i += 1
        }
    }

    /**
     * Are the adjacent pair of steps starting at the provided index connectable?
     */
    private fun connectableFrom(index: Int): Boolean {
        return mRecyclerView?.findViewHolderForAdapterPosition(index) != null
            && mRecyclerView?.findViewHolderForAdapterPosition(index + 1) != null
    }

}