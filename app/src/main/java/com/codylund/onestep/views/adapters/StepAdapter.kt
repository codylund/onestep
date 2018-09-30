package com.codylund.onestep.views.adapters

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import com.codylund.onestep.*
import com.codylund.onestep.R.layout.item_step
import com.codylund.onestep.logging.LogMessages
import com.codylund.onestep.models.Step
import com.codylund.onestep.utils.StringUtils
import com.codylund.onestep.views.MainView
import com.codylund.onestep.views.adapters.StepAdapter.StepViewHolder.Companion.DIFF_CALLBACK
import java.util.logging.Logger

class StepAdapter(var mainView: MainView) : ListAdapter<Step, StepAdapter.StepViewHolder>(DIFF_CALLBACK) {

    val LOGGER = Logger.getLogger(StepAdapter::class.java.name)

    class StepViewHolder(val mainView: MainView, itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val LOGGER = Logger.getLogger(StepViewHolder::class.java.name)

        var whatView = itemView.findViewById<TextView>(R.id.whatView)
        var whenView = itemView.findViewById<TextView>(R.id.whenView)
        var whereView = itemView.findViewById<TextView>(R.id.whereView)
        var whyView = itemView.findViewById<TextView>(R.id.whyView)

        lateinit var stepData: Step

        companion object {
            val DIFF_CALLBACK = object: DiffUtil.ItemCallback<Step>() {
                override fun areItemsTheSame(first: Step, second: Step): Boolean {
                    return first.getIdentifier() == second.getIdentifier()
                }

                override fun areContentsTheSame(first: Step, second: Step): Boolean {
                    return first.equals(second)
                }
            }
        }

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
            if (StringUtils.isEmpty(data)) {
                view.visibility = VISIBLE
                view.text = data
            } else {
                view.visibility = GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, id: Int): StepViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(item_step, parent, false)
        return StepViewHolder(mainView, viewHolder)
    }


    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        var path = getItem(position)
        holder.bindData(path)
    }
}