package com.codylund.onestep

import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface PathImplDao {
    @Query("SELECT * from pathData")
    fun getAll(): Flowable<List<PathImpl>>

    @Query("SELECT * from pathData WHERE mPathId=:id")
    fun get(id: Long): Flowable<PathImpl>

    @Insert
    fun insert(pathImpl: PathImpl): Long

    @Delete
    fun delete(pathImpl: PathImpl)

    @Update
    fun update(pathImpl: PathImpl)
}