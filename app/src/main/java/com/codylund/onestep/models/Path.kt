package com.codylund.onestep.models

interface Path {
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