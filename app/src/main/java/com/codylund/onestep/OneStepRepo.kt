package com.codylund.onestep

interface OneStepRepo {
    fun getFirstSteps(): List<Step>
    fun makeStep(who: String, what: String, wen: String, where: String, why: String, how: String)
    fun takeStep(from: Step, to: Step)
    fun breakStep(from: Step, propogate: Boolean)
}