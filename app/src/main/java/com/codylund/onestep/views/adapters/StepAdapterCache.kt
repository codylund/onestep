package com.codylund.onestep.views.adapters

import android.support.v7.util.ListUpdateCallback
import com.codylund.onestep.models.Step
import java.util.logging.Logger

class StepAdapterCache : ListUpdateCallback {

    private val LOGGER = Logger.getLogger(StepAdapterCache::class.java.name)

    private val mStepCache = mutableMapOf<Long, Step>()
    private val mStepOrder = mutableListOf<Long>()
    private lateinit var mNewSteps: List<Step>

    /**
     * Get the step with the given identifier.
     */
    operator fun get(identifier: Long) = mStepCache[identifier]

    /**
     * Cache the step.
     */
    fun set(step: Step) = set(listOf(step))

    /**
     * Cache the steps.
     */
    fun set(steps: Collection<Step>) {
        for(step in steps)
            mStepCache[step.getIdentifier()] = step
    }

    /**
     * Get the number of steps.
     */
    fun getSize() = mStepOrder.size

    /**
     * Get the item at the specified position in the step order.
     */
    fun getItemAt(index: Int) = mStepCache[mStepOrder[index]]!!

    /**
     * Move an item from one position to another in the step order.
     */
    fun moveItem(initialIndex: Int, finalIndex: Int) {
        mStepOrder.add(finalIndex, mStepOrder.removeAt(initialIndex))
    }

    /**
     * Returns step with the provided identifier.
     */
    fun getItemWithIdentifier(identifier: Long) = mStepCache[identifier]!!

    /**
     * A new list has been received.
     */
    fun onSubmitList(newSteps: List<Step>) {
        mNewSteps = newSteps
    }

    /**
     * Updates cached items that have changed in the new list.
     */
    override fun onChanged(position: Int, count: Int, payload: Any?) {
        LOGGER.info("Step(s) changed: $count at position $position.")
        for (i in position until (position + count)) {
            mNewSteps[i].let {
                LOGGER.info("Changing step with id=${it.getIdentifier()}")
                mStepOrder[i] = it.getIdentifier()
                mStepCache[it.getIdentifier()] = it
            }
        }
    }

    /**
     * Moves cached item that has moved in the new list.
     */
    override fun onMoved(fromPostion: Int, toPosition: Int) {
        LOGGER.info("Step moved: position $fromPostion to position $toPosition.")
        mStepOrder.add(toPosition, mStepOrder.removeAt(fromPostion))
    }

    /**
     * Inserts new items into the cache from the new list.
     */
    override fun onInserted(position: Int, count: Int) {
        LOGGER.info("Step(s) inserted: $count at position $position.")
        for (i in position until (position + count)) {
            mNewSteps[i].let {
                LOGGER.info("Inserting step with id=${it.getIdentifier()}")
                mStepOrder.add(i, it.getIdentifier())
                mStepCache[it.getIdentifier()] = it
            }
        }
    }

    /**
     * Removes items from the cache that are not present in the new list.
     */
    override fun onRemoved(position: Int, count: Int) {
        LOGGER.info("Step(s) removed: $count steps at position $position.")
        for (i in position until (position + count)) {
            var identifier = mStepOrder.removeAt(position)
            mStepCache.remove(identifier)
        }
    }

    /**
     * Converts the cache to its representative ordered list of steps.
     */
    fun toList(): List<Step> {
        val currentList = mutableListOf<Step>()
        for (i in 0 until mStepOrder.size)
            mStepCache[mStepOrder[i]]?.let{ currentList.add(it) }
        return currentList
    }
}