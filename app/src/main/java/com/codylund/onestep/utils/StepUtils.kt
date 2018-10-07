package com.codylund.onestep.utils

import com.codylund.onestep.models.Step
import com.codylund.onestep.models.StepStatus
import java.lang.StringBuilder

object StepUtils {
    fun toggleStatus(step: Step) : StepStatus {
        var status = when(step.getStatus()) {
            StepStatus.NEW -> {
                StepStatus.DONE
            }
            StepStatus.DONE -> {
                StepStatus.NEW
            }
            else -> {
                StepStatus.DONE
            }
        }
        step.setStatus(status)
        return status
    }

    fun orderSteps(steps: List<Step>): List<Step> {
        val orderedSteps = mutableListOf<Step>()
        val map = mutableMapOf<Long, Step>()

        for (step in steps) {
            val nextStepIdentifier = step.getNextStepIdentifier()
            if (nextStepIdentifier != null) {
                map[nextStepIdentifier] = step
            } else {
                orderedSteps.add(step)
            }
        }

        for (item in map) {
            map[orderedSteps[0].getIdentifier()]?.let {
                orderedSteps.add(0, it)
            }
        }

        println("Ordered steps to display")
        for (step in orderedSteps) {
            println("id=${step.getIdentifier()}")
        }

        return orderedSteps
    }

    fun stepListToString(list: List<Step>): String {
        val stringBuilder = StringBuilder()
        for(step in list) {
            stringBuilder.append(step.getIdentifier())
            stringBuilder.append(" -> ")
        }
        stringBuilder.append("DONE")
        return stringBuilder.toString()
    }

    fun connectSteps(fromStep: Step?, toStep: Step?): Step? {
        return fromStep?.also {
            it.setNextStep(toStep)
        }
    }
}