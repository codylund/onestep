package com.codylund.onestep.viewmodels

import android.content.Context
import com.codylund.onestep.models.Step
import com.codylund.onestep.models.StepDataBase
import com.codylund.onestep.models.StepImpl
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import java.util.logging.Logger

class StepViewModelImpl(context: Context) : StepViewModel {

    private val LOGGER = Logger.getLogger(StepViewModelImpl::class.java.name)

    val mStepDataBase = StepDataBase.getInstance(context)!!.stepDataDao()

    override fun updateStep(step: Step) {
        val listOfOne = listOf(step)
        updateSteps(listOfOne)
    }

    override fun updateSteps(steps: List<Step>) {
        val stepImpls = steps.filterIsInstance<StepImpl>()
        Completable.create {
            mStepDataBase.updateSteps(stepImpls)
            it.onComplete()
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object: DisposableCompletableObserver() {
            override fun onComplete() {
                LOGGER.info("Successfully updated steps.")
                dispose()
            }

            override fun onError(e: Throwable) {
                LOGGER.severe("Failed to update steps.")
                dispose()
            }
        })
    }

    override fun updateStepStatus(step: Step) {
        Completable.create {
            mStepDataBase.updateStepStatus(step.getIdentifier(), step.getStatus())
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object: DisposableCompletableObserver() {
            override fun onComplete() {
                LOGGER.info("Successfully updated status for step with id=${step.getIdentifier()}")
            }
            override fun onError(e: Throwable) {
                LOGGER.info("Failed to update step with id=${step.getIdentifier()}")
            }
        })
    }

}