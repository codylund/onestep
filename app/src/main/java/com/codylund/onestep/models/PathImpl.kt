package com.codylund.onestep.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.codylund.onestep.views.adapters.Differ

@Entity(tableName = "pathData")
class PathImpl(
        @ColumnInfo(name = "name") var mName: String,
        @ColumnInfo(name = "description") var mDescription: String?
) : Path {

    @PrimaryKey(autoGenerate = true) var mPathId: Long = 0
    @ColumnInfo(name = "firstStepId") var mFirstStepId: Long = 0
    @ColumnInfo(name = "status") var mStatus: PathStatus = PathStatus.NEW

    override fun getIdentifier(): Long {
        return mPathId
    }

    @Ignore
    override fun getPathName(): String {
        return mName
    }

    override fun getPathDescription(): String? {
        return mDescription
    }

    @Ignore
    override fun getFirstStepId(): Long {
        return mFirstStepId
    }

    @Ignore
    override fun getStatus(): PathStatus {
        return mStatus
    }

    @Ignore
    override fun setPathName(name: String) {
        mName = name
    }

    @Ignore
    override fun setPathDescription(description: String) {
        mDescription = description
    }

    @Ignore
    override fun setFirstStepId(id: Long) {
        mFirstStepId = id
    }

    @Ignore
    override fun setStatus(status: PathStatus) {
        mStatus = status
    }

    override fun delete() {

    }

    override fun sameAs(other: Differ.Diffable): Boolean {
        if (other !is PathImpl)
            return false
        return getIdentifier() == other.getIdentifier()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PathImpl)
            return false
        return (getFirstStepId() != other.getFirstStepId())
                || (getPathDescription() != other.getPathDescription())
                || (getPathName() != other.getPathName())
                || (getStatus() != other.getStatus())
    }
}