package com.codylund.onestep.models

import android.arch.persistence.room.TypeConverter
import com.codylund.onestep.models.PathStatus
import com.codylund.onestep.models.StepStatus

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