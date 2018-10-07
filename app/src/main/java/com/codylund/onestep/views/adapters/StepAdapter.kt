package com.codylund.onestep.views.adapters

import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import com.codylund.onestep.*
import com.codylund.onestep.R.layout.item_step
import com.codylund.onestep.models.Step
import com.codylund.onestep.models.StepStatus
import com.codylund.onestep.utils.StepUtils
import com.codylund.onestep.utils.StringUtils
import com.codylund.onestep.viewmodels.StepViewModel
import com.codylund.onestep.views.Animations
import com.codylund.onestep.views.ItemDragHelperCallback
import com.codylund.onestep.views.ItemDragSwapStrategy
import com.codylund.onestep.views.StepStatusView
import com.codylund.onestep.views.activities.DiffCallbackBuilder
import java.time.Duration
import java.util.logging.Logger

class StepAdapter(val mStepViewModel: StepViewModel) : RecyclerView.Adapter<StepAdapter.StepViewHolder>(){

    val LOGGER = Logger.getLogger(StepAdapter::class.java.name)
    private val mSteps = mutableListOf<Step>()

    // ItemTouchHelper is awesome because it greatly simplifies drag and drop animations. However,
    // it is not clear how it manages the underlying ViewHolder objects as items are moved
    // around. This means that, after a while, it is not easy to tell which ViewHolder contains
    // which Step. We maintain a cache so we can easily retrieve the latest Step by its identifier
    // instead of by its location in the adapter list.
    private val mStepCache = StepAdapterCache()

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mItemTouchHelper: ItemTouchHelper
    private val mDiffCallbackBuilder = DiffCallbackBuilder(Differ<Step>())

    /**
     * The step list has been updated.
     */
    fun submitList(steps: List<Step>) {
        // Let the cache know about it
        mStepCache.onSubmitList(steps)

        // Find the differences between the new list and the cached list
        val result = DiffUtil.calculateDiff(mDiffCallbackBuilder.build(mStepCache.toList(), steps))

        // Dispatch the changes to both the cache and this adapter
        result.apply {
            LOGGER.info("Dispatching step list updates to the cache.")
            dispatchUpdatesTo(mStepCache) // Updates the data with the changes
            LOGGER.info("Dispatching step list updates to the adapter.")
            dispatchUpdatesTo(this@StepAdapter) // Update the views with the changes
        }
    }

    /**
     * How many steps are in the adapter?
     */
    override fun getItemCount(): Int {
        return mStepCache.getSize()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        // Grab a reference to the recycler view for later use
        mRecyclerView = recyclerView

        // Listen for the RecyclerView to finish populating for the first time
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener {
            object: ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // Connect the dots; this is the only time we should do this without user action
                    connectTheDots()

                    // Unregister this listener so we don't get into an infinite loop.
                    recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)                }
            }
        }

        // Setup touch helper for drag and drop support
        mItemTouchHelper = ItemTouchHelper(ItemDragHelperCallback(object: ItemDragSwapStrategy<StepViewHolder> {

            private var mStartingPosition: Int = -1

            /**
             * Step updates to propagate to the cache and DB. Use a map with step identifiers for keys
             * so we can't queue multiple updates for the same step.
             */
            private val mUpdateList = mutableMapOf<Long, Step>()

            /**
             * Call when a drag-and-dropped action is started.
             */
            override fun start(position: Int) {
                LOGGER.info("Starting drag from position $position")
                mStartingPosition = position

                // Clear update list for current drag and drop
                mUpdateList.clear()

                // Disconnect the dots
                connectTheDots(reset = true)

                // Join the steps surrounding the original
                getLastAndNextStep(position) { lastStep, nextStep ->
                    StepUtils.connectSteps(lastStep, nextStep)?.let{
                        mUpdateList[it.getIdentifier()] = it
                    }
                }
            }

            /**
             * Call when the drag-and-drop action is finished.
             */
            override fun complete(position: Int) {
                LOGGER.info("Stopping drag in position $position")

                // Did the item actually move?
                if (mStartingPosition != position) {
                    mStepCache.moveItem(mStartingPosition, position)

                    // Update pointers of moved item and surrounding steps
                    val step = mStepCache.getItemAt(position)
                    getLastAndNextStep(position) { lastStep, nextStep ->
                        // Last to current
                        StepUtils.connectSteps(lastStep, step)?.let{
                            mUpdateList[it.getIdentifier()] = it
                        }
                        // Current to next
                        StepUtils.connectSteps(step, nextStep)?.let{
                            mUpdateList[it.getIdentifier()] = it
                        }
                    }

                    // Update the cache
                    mStepCache.set(mUpdateList.values)

                    // Update the DB
                    mStepViewModel.updateSteps(mUpdateList.values.toList())

                    LOGGER.info("Updated step list: ${StepUtils.stepListToString(mStepCache.toList())}")
                }

                // The item was dropped- reconnect the dots
                connectTheDots()
            }

            /**
             * Return the steps surrounding the provided position via the callback.
             */
            private fun getLastAndNextStep(position: Int, callback: (Step?, Step?) -> Unit) {
                var lastStep = if (position > 0) mStepCache.getItemAt(position - 1) else null
                var nextStep = if (position < itemCount - 1) mStepCache.getItemAt(position + 1) else null
                println("lastStep=${lastStep?.getIdentifier()}, nextStep=${nextStep?.getIdentifier()}")
                callback(lastStep, nextStep)
            }
        }))

        mItemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, id: Int): StepViewHolder {
        return StepViewHolder(LayoutInflater.from(parent.context).inflate(item_step, parent, false))
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        LOGGER.info("Binding step at position $position")
        var step = mStepCache.getItemAt(position)

        holder.bindStepData(step)
        holder.setOnStatusClickListener {
            LOGGER.info("Updated status of step with id=${step.getIdentifier()}")
            mStepCache.getItemWithIdentifier(step.getIdentifier())?.let {
                StepUtils.toggleStatus(it)
                mStepViewModel.updateStepStatus(it)
            }
            connectTheDots()
        }
    }

    override fun onViewAttachedToWindow(holder: StepViewHolder) {
        super.onViewAttachedToWindow(holder)
        connectTheDots()
    }

    /**
     * Connect the dots according the the corresponding path statuses.
     * TODO: make this more efficient. It causes significant UI lag.
     */
    private fun connectTheDots(reset: Boolean = false) {

        var i = findFirstVisibleItemPosition()
        var last = findLastVisibleItemPosition()

        LOGGER.info("Connecting the dots from $i to $last")
        while(connectableFrom(i) && i <= last) {
            // Get the adjacent steps
            var fromStep = mStepCache.getItemAt(i)
            var toStep = mStepCache.getItemAt(i+1)

            LOGGER.info("Trying to connect steps ${fromStep.getIdentifier()} (${fromStep.getStatus()}) and ${toStep.getIdentifier()} (${toStep.getStatus()})")

            var fromStepView = mRecyclerView?.findViewHolderForLayoutPosition(i) as StepViewHolder
            var toStepView = mRecyclerView?.findViewHolderForLayoutPosition(i + 1) as StepViewHolder

            // Are both steps completed?
            var done = false
            if (!reset && fromStep.getStatus() == StepStatus.DONE && toStep.getStatus() == StepStatus.DONE) {
                done = true
            }

            // Connect them (or not)
            fromStepView.drawToNext(done)
            toStepView.drawToLast(done)

            // On to the next set
            i += 1
        }
    }

    private fun findFirstVisibleItemPosition()
            = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

    private fun findLastVisibleItemPosition()
            = (mRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

    /**
     * Are the adjacent pair of steps starting at the provided index connectable?
     */
    private fun connectableFrom(index: Int): Boolean {
        return mRecyclerView?.findViewHolderForLayoutPosition(index) != null
            && mRecyclerView?.findViewHolderForLayoutPosition(index + 1) != null
    }

    /**
     * ViewHolder for displaying step data in a RecyclerView.
     */
    class StepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val LOGGER = Logger.getLogger(StepViewHolder::class.java.name)

        // Step data views
        private var whatView = itemView.findViewById<TextView>(R.id.whatView)
        private var whenView = itemView.findViewById<TextView>(R.id.whenView)
        private var whereView = itemView.findViewById<TextView>(R.id.whereView)
        private var whyView = itemView.findViewById<TextView>(R.id.whyView)

        // Status views
        private var statusView = itemView.findViewById<StepStatusView>(R.id.status)
        private var topLineView = itemView.findViewById<ImageView>(R.id.topLine)
        private var bottomLineView = itemView.findViewById<ImageView>(R.id.bottomLine)

        fun bindStepData(step: Step) {
            // Display the text data
            displayData(whatView, step.getWhat())
            displayData(whenView, step.getWhen())
            displayData(whereView, step.getWhere())
            displayData(whyView, step.getWhy())

            // Update the status view
            statusView.setStatus(step.getStatus())
        }

        fun setOnStatusClickListener(action: () -> Unit) {
            statusView.setOnClickListener {
                statusView.toggleStatus()
                action()
            }
        }

        private fun displayData(view: TextView, data: String?) {
            if (!StringUtils.isEmpty(data)) {
                view.visibility = VISIBLE
                view.text = data
            } else {
                view.visibility = GONE
            }
        }

        fun drawToLast(shouldDraw: Boolean, customDelay: Long = 500, customDuration: Long = 400)
                = animateLine(topLineView, shouldDraw, customDelay, customDuration)

        fun drawToNext(shouldDraw: Boolean, customDelay: Long = 500, customDuration: Long = 400)
                = animateLine(bottomLineView, shouldDraw, customDelay, customDuration)

        private fun animateLine(imageView: ImageView, shouldDraw: Boolean, delay: Long, duration: Long) {
            if (shouldDraw) {
                Animations.fadeWithDelay(imageView, delay, duration, 1.0f)
            } else {
                Animations.fadeWithDelay(imageView, 0, 50, 0.0f)
            }
        }
    }
}