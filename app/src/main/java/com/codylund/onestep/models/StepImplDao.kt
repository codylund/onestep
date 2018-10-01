package com.codylund.onestep.models

import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface StepImplDao {
    @Query("SELECT * from stepData")
    fun getAll(): Flowable<List<StepImpl>>

    @Query("SELECT * from stepData where pathId = :pathId")
    fun getStepsForPath(pathId: Long): Flowable<List<StepImpl>>

    @Query("SELECT * from stepData where pathId = :pathId AND nextStep = null")
    fun getLastStep(pathId: Long): StepImpl

    @Query("SELECT * from stepData where mStepId = :id limit 1")
    fun get(id: Long): Flowable<StepImpl>

    @Insert
    fun insert(stepImpl: StepImpl): Long

    @Delete
    fun delete(stepImpl: StepImpl)

    @Update
    fun update(stepImpl: StepImpl)
}