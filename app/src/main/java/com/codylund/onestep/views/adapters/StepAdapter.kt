package com.codylund.onestep.views.adapters

import android.support.v7.recyclerview.extensions.ListAdapter
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
import java.util.logging.Logger

class StepAdapter : ListAdapter<Step, StepAdapter.StepViewHolder>(DIFF_CALLBACK) {

    val LOGGER = Logger.getLogger(StepAdapter::class.java.name)

    companion object {
        val DIFF_CALLBACK = Differ<Step>()
    }

    class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val LOGGER = Logger.getLogger(StepViewHolder::class.java.name)

        var whatView = itemView.findViewById<TextView>(R.id.whatView)
        var whenView = itemView.findViewById<TextView>(R.id.whenView)
        var whereView = itemView.findViewById<TextView>(R.id.whereView)
        var whyView = itemView.findViewById<TextView>(R.id.whyView)

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
        }

        private fun displayData(data: String?, view: TextView) {
            if (!StringUtils.isEmpty(data)) {
                view.visibility = VISIBLE
                view.text = data
            } else {
                view.visibility = GONE
            }
        }

        fun drawFromLast(shouldDraw: Boolean) {
            topLineView.visibility = if (shouldDraw) VISIBLE else INVISIBLE
        }

        fun drawToNext(shouldDraw: Boolean) {
            bottomLineView.visibility = if (shouldDraw) VISIBLE else INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, id: Int): StepViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(item_step, parent, false)
        return StepViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        // Get the step to bind
        var step = getItem(position)
        holder.bindData(step)

        if (position > 0) {
            var lastItem = getItem(position - 1)
            holder.drawFromLast(lastItem.getStatus() == StepStatus.DONE)
        }

        if (position < itemCount - 1) {
            var nextItem = getItem(position + 1)
            holder.drawToNext(nextItem.getStatus() == StepStatus.DONE)
        }
    }
}