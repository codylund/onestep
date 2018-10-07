package com.codylund.onestep.models

import com.codylund.onestep.views.adapters.Differ

interface Step : Differ.Diffable {
    fun getIdentifier(): Long
    fun getNextStepIdentifier(): Long?

    fun getWho(): String?
    fun getWhat(): String?
    fun getWhen(): String?
    fun getWhere(): String?
    fun getWhy(): String?
    fun getHow(): String?
    fun getStatus(): StepStatus

    fun setWho(how: String)
    fun setWhat(what: String)
    fun setWhen(wen: String)
    fun setWhere(where: String)
    fun setWhy(why: String)
    fun setHow(how: String)
    fun setStatus(status: StepStatus)
    fun setNextStep(step: Step?)
}