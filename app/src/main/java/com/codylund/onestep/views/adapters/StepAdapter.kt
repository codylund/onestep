package com.codylund.onestep.views.adapters

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.codylund.onestep.*
import com.codylund.onestep.R.layout.item_step
import com.codylund.onestep.logging.LogMessages
import com.codylund.onestep.models.Step
import com.codylund.onestep.models.StepStatus
import com.codylund.onestep.utils.StringUtils
import com.codylund.onestep.views.StepStatusView
import java.util.logging.Logger

class StepAdapter() : ListAdapter<Step, StepAdapter.StepViewHolder>(DIFF_CALLBACK) {

    val LOGGER = Logger.getLogger(StepAdapter::class.java.name)

    companion object {
        val DIFF_CALLBACK = Differ<Step>()
    }

    lateinit var mRecyclerView: RecyclerView

    class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val LOGGER = Logger.getLogger(StepViewHolder::class.java.name)

        var whatView = itemView.findViewById<TextView>(R.id.whatView)
        var whenView = itemView.findViewById<TextView>(R.id.whenView)
        var whereView = itemView.findViewById<TextView>(R.id.whereView)
        var whyView = itemView.findViewById<TextView>(R.id.whyView)

        var statusView = itemView.findViewById<StepStatusView>(R.id.status)
        var topLineView = itemView.findViewById<ImageView>(R.id.topLine)
        var bottomLineView = itemView.findViewById<ImageView>(R.id.bottomLine)

        lateinit var stepData: Step

        fun bindData(step: Step) {
            stepData = step
            bindData()
        }

        fun bindData() {
            if (stepData == null) {
                LOGGER.severe(LogMessages.STEP_DATA_NULL)
                return
            }

            displayData(stepData.getWhat(), whatView)
            displayData(stepData.getWhen(), whenView)
            displayData(stepData.getWhere(), whereView)
            displayData(stepData.getWhy(), whyView)

            statusView.setStatus(stepData.getStatus())
        }

        private fun displayData(data: String?, view: TextView) {
            if (!StringUtils.isEmpty(data)) {
                view.visibility = VISIBLE
                view.text = data
            } else {
                view.visibility = GONE
            }
        }

        fun drawToLast(shouldDraw: Boolean) {
            topLineView.visibility = if (shouldDraw) VISIBLE else INVISIBLE
        }

        fun drawToNext(shouldDraw: Boolean) {
            bottomLineView.visibility = if (shouldDraw) VISIBLE else INVISIBLE
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, id: Int): StepViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(item_step, parent, false)
        return StepViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        LOGGER.info("Bound view at position $position")

        // Get the step to bind
        var step = getItem(position)
        holder.bindData(step)

        holder.statusView.setOnClickListener {
            holder.statusView.toggleStatus()
            connectTheDots()
        }

        connectTheDots()
    }

    /**
     * Connect the dots according the the corresponding path statuses.
     */
    private fun connectTheDots() {
        var i = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        while(connectableFrom(i)) {
            // Get the adjacent status views
            var fromStep = mRecyclerView?.findViewHolderForAdapterPosition(i) as StepViewHolder
            var toStep = mRecyclerView?.findViewHolderForAdapterPosition(i + 1) as StepViewHolder

            // Connect them if both are completed
            if (fromStep.statusView.mStatus == StepStatus.DONE && toStep.statusView.mStatus == StepStatus.DONE) {
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