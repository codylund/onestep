package com.codylund.onestep.views.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.Toast
import com.codylund.onestep.viewmodels.PathViewModelImpl
import com.codylund.onestep.models.Path
import com.codylund.onestep.R
import com.codylund.onestep.models.ObserverAdapter
import com.codylund.onestep.models.Step
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

    // Path view display stuff
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: StepAdapter

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

                viewAdapter = StepAdapter()

                // Display live path list in recycler view with list adapter
                recyclerView = this.steps.apply {
                    // Prevent resizing after adapter updates
                    setHasFixedSize(true)

                    // Display paths linearly
                    layoutManager = LinearLayoutManager(this@PathActivity)

                    adapter = viewAdapter
                }


                // Fetch steps for the path
                pathFinder.getSteps(pathId).subscribe(object: ObserverAdapter<List<Step>> {
                    override fun onSuccess(result: List<Step>) {
                        viewAdapter.submitList(result)
                    }

                    override fun onFailure(throwable: Throwable) {
                        LOGGER.severe("Failed to update step list: " + throwable.localizedMessage)
                    }

                })
            }
        }

        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
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
            val pathId = intent.getLongExtra(KEY_PATH_ID, INVALID_PATH_ID)
            var intent = Intent(this, NewStepActivity::class.java)
            intent.putExtra(NewStepActivity.KEY_PATH_ID, pathId)
            startActivity(intent)
        }
    }
}