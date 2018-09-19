package com.codylund.onestep

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = arrayOf(StepImpl::class), version = 1)
@TypeConverters(Converters::class)
abstract class StepDataBase : RoomDatabase() {

    abstract fun stepDataDao(): StepImplDao

    companion object {

        private var INSTANCE: StepDataBase? = null

        fun getInstance(context: Context): StepDataBase? {
            if (INSTANCE == null) {
                synchronized(StepDataBase::class) {
                    INSTANCE = Room.databaseBuilder(context,
                            StepDataBase::class.java, "steps.db").build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}