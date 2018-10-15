package com.codylund.onestep.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.codylund.onestep.views.adapters.Differ

@Entity(tableName = "stepData")
data class StepImpl (
        @ColumnInfo(name = "who") var mWho: String?,
        @ColumnInfo(name = "what") var mWhat: String?,
        @ColumnInfo(name = "when") var mWhen: String?,
        @ColumnInfo(name = "where") var mWhere: String?,
        @ColumnInfo(name = "why") var mWhy: String?,
        @ColumnInfo(name = "how") var mHow: String?
) : Step {

    @PrimaryKey(autoGenerate = true) var mStepId: Long = 0
    @ColumnInfo(name = "pathId") var mPathId: Long? = null
    @ColumnInfo(name = "nextStep") var mNextStepId: Long? = null
    @ColumnInfo(name = "status") var mStatus: StepStatus = StepStatus.NEW

    @Ignore
    override fun setWho(who: String) {
        mWho = who
    }

    @Ignore
    override fun setWhat(what: String) {
        mWhat = what
    }

    @Ignore
    override fun setWhen(wen: String) {
        mWhen = wen
    }

    @Ignore
    override fun setWhere(where: String) {
        mWhere = where
    }

    @Ignore
    override fun setWhy(why: String) {
        mWhy = why
    }

    @Ignore
    override fun setHow(how: String) {
        mHow = how
    }

    @Ignore
    override fun setStatus(status: StepStatus) {
        mStatus = status
    }

    override fun setNextStep(step: Step?) {
        mNextStepId = step?.getIdentifier()
    }

    @Ignore
    override fun getIdentifier(): Long {
        return mStepId
    }

    override fun getNextStepIdentifier(): Long? {
        return mNextStepId
    }

    @Ignore
    override fun getWho(): String? {
        return mWho
    }

    @Ignore
    override fun getWhat(): String? {
        return mWhat
    }

    @Ignore
    override fun getWhen(): String? {
        return mWhen
    }

    @Ignore
    override fun getWhere(): String? {
        return mWhere
    }

    @Ignore
    override fun getWhy(): String? {
        return mWhy
    }

    @Ignore
    override fun getHow(): String? {
        return mHow
    }

    @Ignore
    override fun getStatus(): StepStatus {
        return mStatus
    }

    override fun equals(other: Any?) : Boolean {
        // Check the type
        if (other !is StepImpl)
            return false

        // Check all the members
        return mStepId == other.mStepId
                && mPathId == other.mPathId
                && mNextStepId == other.mNextStepId
                && mStatus == other.mStatus
                && mWho == other.mWho
                && mWhat == other.mWhat
                && mWhy == other.mWhy
                && mWhen == other.mWhen
                && mWhere == other.mWhere
                && mHow == other.mHow
    }

    override fun sameAs(other: Differ.Diffable): Boolean {
        if (other !is StepImpl)
            return false
        return getIdentifier() == other.getIdentifier()
    }
}