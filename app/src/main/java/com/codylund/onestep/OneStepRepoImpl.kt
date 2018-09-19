package com.codylund.onestep

import android.content.ClipDescription
import android.content.Context
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class OneStepRepoImpl(context: Context) : OneStepRepo, OnDeletePathCallback {

    val mStepDataBase = StepDataBase.getInstance(context)!!.stepDataDao()
    val mPathDataBase = PathDataBase.getInstance(context)!!.pathDataDao()
    val mFirstSteps: MutableList<Step> = mutableListOf()
    
    override fun getFirstSteps(): List<Step> {
        return mFirstSteps
    }

    fun getPaths(): Flowable<List<PathImpl>> {
        return mPathDataBase.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getPath(id: Long): Flowable<PathImpl> {
        return mPathDataBase.get(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun makePath(name: String, description: String) : Single<Long> {
        return Single.create({it: SingleEmitter<Long> ->
            var path = PathImpl(name, description)
            val id = mPathDataBase.insert(path)
            it.onSuccess(id)
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun makeStep(who: String, what: String, wen: String, where: String, why: String, how: String) {

        var step = StepImpl(who, what, wen, where, why, how)

        Completable.create({
            val id = mStepDataBase.insert(step)
            it.onComplete()
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((object : DisposableCompletableObserver() {
            override fun onComplete() {
                println("Added " + step.toString() + " to step database")
                dispose()
            }

            override fun onError(e: Throwable) {
                println("Failed to add " + step.toString() + " to step database")
                dispose()
            }
        }))
    }

    override fun takeStep(from: Step, to: Step) {
        checkType(from, to)
        val fromImpl = from as StepImpl
        val toImpl = to as StepImpl
        fromImpl.mNextStepId = toImpl.mStepId
        toImpl.mLastStepId = fromImpl.mStepId
        mStepDataBase?.update(from)
        mStepDataBase?.update(to)
    }

    override fun breakStep(from: Step, propagate: Boolean) {
       /* val to = from.getNextStep() ?: return
        checkType(from, to)

        // Get data objects for current and next steps
        val fromImpl = from as StepImpl
        val toImpl = to as StepImpl

        // Will the break propagate?
        if (propagate) {
            fromImpl.mNextStepId = null
            fromImpl.setNextStep(null)
            recursiveDelete(toImpl)
        } else {
            fromImpl.mNextStepId = toImpl.mNextStepId
            fromImpl.setNextStep(toImpl.getNextStep())
        }

        // Update the items
        mStepDataBase?.update(from)
        mStepDataBase?.delete(to)*/
    }

    private fun recursiveDelete(step: StepImpl?) {
        /*var next = step
        while (next != null) {
            mStepDataBase?.delete(next)
            next = next.getNextStep() as StepImpl
        }*/
    }

    private fun checkType(vararg steps: Step) {
        for (step in steps)
            if (step !is StepImpl)
                throw IllegalArgumentException(step.toString())
    }

    override fun delete(path: Path) {
        if (path !is PathImpl) return
        Completable.create({
            mPathDataBase.delete(path)
            it.onComplete()
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(object: DisposableCompletableObserver() {
            override fun onComplete() {
                println("Deleted step.")
            }

            override fun onError(e: Throwable) {
                println("Failed to delete path: ${e.localizedMessage}")
            }
        })
    }
}