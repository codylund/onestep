package com.codylund.onestep.views.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.Toast
import com.codylund.onestep.ItemDragSwapStrategy
import com.codylund.onestep.viewmodels.PathViewModelImpl
import com.codylund.onestep.models.Path
import com.codylund.onestep.R
import com.codylund.onestep.models.ObserverAdapter
import com.codylund.onestep.models.Step
import com.codylund.onestep.views.ItemDragHelperCallback
import com.codylund.onestep.views.adapters.StepAdapter
import kotlinx.android.synthetic.main.activity_path.*
import java.util.logging.Logger

class PathActivity : AppCompatActivity() {

    private val LOGGER = Logger.getLogger(PathActivity::class.java.name)

    private lateinit var mItemTouchHelper: ItemTouchHelper

    companion object {
        val KEY_PATH_ID = "PathActivity.PATH_ID"
    }

    private val INVALID_PATH_ID: Long = -1

    private lateinit var pathFinder: PathViewModelImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path)
        val pathId = intent.getLongExtra(KEY_PATH_ID, INVALID_PATH_ID)
        when(pathId) {
            INVALID_PATH_ID -> {
                LOGGER.severe("Error: expected a path id but got $INVALID_PATH_ID")
                finish()
            }
            else -> {
                LOGGER.info("Fetching path with id $pathId")
                pathFinder = PathViewModelImpl(this)

                // Fetch the path data
                pathFinder.getPath(pathId).subscribe(object: ObserverAdapter<Path> {

                    override fun onSuccess(result: Path) {
                        println(result.toString())
                        toolbar.title = result.getPathName()
                    }

                    override fun onFailure(throwable: Throwable) {
                        Toast.makeText(applicationContext, "Uh-oh! Sorry, I couldn't find that path...", Toast.LENGTH_LONG).show()
                        throwable.printStackTrace()
                        finish()
                    }

                })

                // Fetch steps for the path
                pathFinder.getSteps(pathId).subscribe(object: ObserverAdapter<List<Step>> {
                    override fun onSuccess(result: List<Step>) {
                        TODO("not implemented")
                    }

                    override fun onFailure(throwable: Throwable) {
                        TODO("not implemented")
                    }

                })
            }
        }

        steps.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && addPathButton.visibility == View.VISIBLE) {
                    addPathButton.hide()
                } else if (dy < 0 && addPathButton.visibility != View.VISIBLE) {
                    addPathButton.show()
                }
            }
        })

        addPathButton.setOnClickListener {
            startActivity(Intent(this, NewStepActivity::class.java))
        }

        mItemTouchHelper = ItemTouchHelper(ItemDragHelperCallback(object: ItemDragSwapStrategy<StepAdapter.StepViewHolder> {

            override fun swap(firstItem: StepAdapter.StepViewHolder, secondItem: StepAdapter.StepViewHolder) {
                // pathFinder.swapSteps(firstItem.stepData, secondItem.stepData)
            }

            override fun complete() {
                // TODO update the the database with the swap
            }

        }))
        mItemTouchHelper.attachToRecyclerView(steps)
    }
}