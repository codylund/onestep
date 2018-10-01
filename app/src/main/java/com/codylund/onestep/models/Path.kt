package com.codylund.onestep.models

import com.codylund.onestep.views.adapters.Differ

interface Path : Differ.Diffable {
    fun getIdentifier(): Long
    fun getPathName(): String
    fun getPathDescription(): String?
    fun getFirstStepId(): Long
    fun getStatus(): PathStatus
    fun setPathName(name: String)
    fun setPathDescription(description: String)
    fun setFirstStepId(id: Long)
    fun setStatus(status: PathStatus)
    fun delete()
}