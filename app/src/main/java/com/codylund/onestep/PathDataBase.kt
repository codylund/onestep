package com.codylund.onestep

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = arrayOf(PathImpl::class), version = 1)
@TypeConverters(Converters::class)
abstract class PathDataBase : RoomDatabase() {

    abstract fun pathDataDao(): PathImplDao

    companion object {

        private var INSTANCE: PathDataBase? = null

        fun getInstance(context: Context): PathDataBase {
            if (INSTANCE == null) {
                synchronized(StepDataBase::class) {
                    INSTANCE = Room.databaseBuilder(context,
                            PathDataBase::class.java, "paths.db").build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}