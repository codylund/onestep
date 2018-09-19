package com.codylund.onestep

interface Path {
    fun getIdentifier(): Any
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