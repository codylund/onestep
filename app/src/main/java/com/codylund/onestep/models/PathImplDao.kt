package com.codylund.onestep.models

import android.arch.persistence.room.*
import com.codylund.onestep.models.PathImpl
import io.reactivex.Flowable

@Dao
interface PathImplDao {
    @Query("SELECT * from pathData")
    fun getAll(): Flowable<List<PathImpl>>

    @Query("SELECT * from pathData WHERE mPathId=:id")
    fun get(id: Long): Flowable<PathImpl>

    @Query("SELECT * from pathData WHERE mPathId=:id")
    fun getSync(id: Long): PathImpl

    @Insert
    fun insert(pathImpl: PathImpl): Long

    @Delete
    fun delete(pathImpl: PathImpl)

    @Update
    fun update(pathImpl: PathImpl)
}