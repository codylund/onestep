package com.codylund.onestep.viewmodels

import android.content.Context
import com.codylund.onestep.models.*
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class PathViewModelImpl(context: Context) : PathViewModel {

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

    fun makeStep(who: String, what: String, wen: String, where: String, why: String, how: String)
            : Completable {

        var step = StepImpl(who, what, wen, where, why, how)

        return Completable.create {
            val id = mStepDataBase.insert(step)
            it.onComplete()
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun takeStep(from: Step, to: Step) {
        checkType(from, to)
        val fromImpl = from as StepImpl
        val toImpl = to as StepImpl
        fromImpl.mNextStepId = toImpl.mStepId
        toImpl.mLastStepId = fromImpl.mStepId
        mStepDataBase?.update(from)
        mStepDataBase?.update(to)
    }

    private fun checkType(vararg steps: Step) {
        for (step in steps)
            if (step !is StepImpl)
                throw IllegalArgumentException(step.toString())
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