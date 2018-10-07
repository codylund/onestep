package com.codylund.onestep.viewmodels

import android.content.Context
import android.util.Log
import com.codylund.onestep.models.*
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.logging.Logger

class PathViewModelImpl(val context: Context) : PathViewModel {

    private val LOGGER = Logger.getLogger(PathViewModelImpl::class.java.name)

    val mStepDataBase = StepDataBase.getInstance(context)!!.stepDataDao()
    val mPathDataBase = PathDataBase.getInstance(context)!!.pathDataDao()

    fun getPaths(): ObservableAdapter<List<Path>> {
        val observablePathList = mPathDataBase.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        return FlowableAdapter(observablePathList as Flowable<List<Path>>)
    }

    fun getPath(id: Long): ObservableAdapter<Path> {
        val observablePath = mPathDataBase.get(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        return FlowableAdapter(observablePath as Flowable<Path>)
    }

    fun getSteps(pathId: Long): ObservableAdapter<List<Step>> {
        val observableStepList = mStepDataBase.getStepsForPath(pathId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        return FlowableAdapter(observableStepList as Flowable<List<Step>>)
    }

    fun makePath(name: String, description: String): Single<Long> {
        return Single.create { it: SingleEmitter<Long> ->
            var path = PathImpl(name, description)

            val id = mPathDataBase.insert(path)
            it.onSuccess(id)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun makeStep(pathId: Long, who: String, what: String, wen: String, where: String, why: String, how: String)
            : Completable {

        return Completable.create {
            // Get the current last step in the path
            val lastStep = mStepDataBase.getLastStep(pathId)

            var step = StepImpl(who, what, wen, where, why, how)
            step.mPathId = pathId
            step.mNextStepId = null

            // Update the new step with the last step's id
            val id = mStepDataBase.insert(step)
            LOGGER.info("Added step with id=$id to path with id=$pathId")

            // Update the previous step
            lastStep?.let {
                LOGGER.info("Step with id=${it.mStepId} leads to step with id=$id ")
                it.mNextStepId = id
                mStepDataBase.update(it)
            }

            it.onComplete()
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun delete(path: Path) : Completable {
        return Completable.create {
            mPathDataBase.delete(path as PathImpl)
            it.onComplete()
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }
}