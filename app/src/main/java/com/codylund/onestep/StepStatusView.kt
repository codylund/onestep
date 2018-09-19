package com.codylund.onestep

import android.content.Context
import android.widget.ImageView

class StepStatusView(context: Context) : ImageView(context) {

    init {
        setStatus(StepStatus.NEW)
    }

    fun setStatus(status: StepStatus) {
        setImageResource(getResourceId(status))
    }

    fun getResourceId(status: StepStatus): Int {
        when (status) {
            StepStatus.NEW -> {
                return R.drawable.drawable_uncompleted_task
            }
            StepStatus.ACTIVE -> {
                return R.drawable.drawable_uncompleted_task
            }
            StepStatus.DONE -> {
                return R.drawable.drawable_completed_task
            }
        }
        return R.drawable.drawable_uncompleted_task
    }
}

