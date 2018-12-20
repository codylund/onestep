package com.codylund.onestep.views.adapters

import android.support.v7.util.ListUpdateCallback
import com.codylund.onestep.models.Step
import com.codylund.onestep.models.StepStatus
import com.codylund.onestep.utils.StepUtils
import com.codylund.onestep.viewmodels.StepViewModel
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

class StepAdapterCache(val mStepViewModel: StepViewModel) : ListUpdateCallback {

    private val LOGGER = Logger.getLogger(StepAdapterCache::class.java.name)

    // step data mapped by step identifier
    private val mStepCache = mutableMapOf<Long, Step>()

    // order of the steps
    private val mStepOrder = mutableListOf<Long>()

    // should the lines for the current step be drawn?
    private var mLineCache = ConcurrentHashMap<Long, LineCacheItem>()
    data class LineCacheItem(var nextState: Boolean, var lastState: Boolean)

    // new list of steps
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
        updateLines()
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
        updateLines()
    }

    /**
     * Moves cached item that has moved in the new list.
     */
    override fun onMoved(fromPostion: Int, toPosition: Int) {
        LOGGER.info("Step moved: position $fromPostion to position $toPosition.")

        // Reorder the steps
        val id = mStepOrder.removeAt(fromPostion)
        mStepOrder.add(toPosition, id)

        // Update the
        var updateList = mutableListOf<Int>()
                .apply {
                    // the step behind the moved step
                    add(toPosition-1)
                    // the moved step
                    add(toPosition)
                    // the step behind the moved step's original position
                    add(fromPostion - if (toPosition > fromPostion) 1 else 0)
                }.filter {
                    it >= 0
                }.map {
                    var fromId = mStepOrder[it]
                    var toId = if (it + 1 < mStepOrder.size) mStepOrder[it + 1] else null

                    mStepCache[fromId]!!.apply {
                        setNextStep(if (toId == null) null else mStepCache[toId])
                    }
                }

        updateLines()

        if (updateList.isNotEmpty())
            mStepViewModel.updateSteps(updateList)
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
        updateLines()
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
        updateLines(position, position + count - 1)
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


    fun getLineCacheItem(identifier: Long) = mLineCache[identifier]

    private fun updateLines(startIndex: Int = 0, endIndex: Int = getSize() - 1) {
        for (i in startIndex until endIndex) {
            LOGGER.info("Caching lines from $i to $i+1")
            // Get the adjacent steps
            var fromStep = getItemAt(i)
            var toStep = getItemAt(i+1)

            // Are both steps completed?
            var done = false
            if (fromStep.getStatus() == StepStatus.DONE && toStep.getStatus() == StepStatus.DONE) {
                done = true
            }

            updateLineCache(fromStep.getIdentifier(), toStep.getIdentifier(), done)
        }
    }

    private fun updateLineCache(from: Long, to: Long, done: Boolean) {
        // Insert cache items if they don't exist
        if (mLineCache[from] != null) {
            mLineCache[from]?.nextState = done
        } else {
            mLineCache[from] = LineCacheItem(done, false)
        }

        if (to < getSize() && mLineCache[to] != null) {
            mLineCache[to]?.lastState = done
        } else {
            mLineCache[to] = LineCacheItem(false, done)
        }
    }
}