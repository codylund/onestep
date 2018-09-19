package com.codylund.onestep

import android.arch.persistence.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStepStatus(status: StepStatus) : Int {
        return status.ordinal
    }

    @TypeConverter
    fun toStepStatus(int: Int) : StepStatus {
        return StepStatus.values()[int]
    }

    @TypeConverter
    fun fromPathStatus(status: PathStatus) : Int {
        return status.ordinal
    }

    @TypeConverter
    fun toPathStatus(int: Int) : PathStatus {
        return PathStatus.values()[int]
    }
}