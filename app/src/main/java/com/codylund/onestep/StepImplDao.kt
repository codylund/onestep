package com.codylund.onestep

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
interface StepImplDao {
    @Query("SELECT * from stepData")
    fun getAll(): Flowable<List<StepImpl>>

    @Query("SELECT * from stepData where mStepId = :id limit 1")
    fun get(id: Int): Flowable<StepImpl>

    @Insert
    fun insert(stepImpl: StepImpl)

    @Delete
    fun delete(stepImpl: StepImpl)

    @Update
    fun update(stepImpl: StepImpl)
}