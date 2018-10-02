package com.codylund.onestep.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.codylund.onestep.R
import com.codylund.onestep.models.StepStatus

class StepStatusView(context: Context, attrs: AttributeSet?) : ImageView(context, attrs) {

    var mStatus: StepStatus = StepStatus.NEW

    fun setStatus(status: StepStatus) {
        mStatus = status
        setImageResource(getResourceId(status))
    }

    fun toggleStatus() {
        when(mStatus) {
            StepStatus.NEW -> setStatus(StepStatus.DONE)
            StepStatus.DONE -> setStatus(StepStatus.NEW)
        }
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

