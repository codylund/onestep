package com.codylund.onestep.viewmodels

import com.codylund.onestep.models.Step

interface StepViewModel {
    fun updateSteps(steps: List<Step>)
    fun updateStep(step: Step)
    fun updateStepStatus(step: Step)
}