package com.codylund.onestep

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.transition.Explode
import android.view.View
import android.view.Window
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_path.*
import java.util.logging.Logger

class PathActivity : AppCompatActivity() {

    private val LOGGER = Logger.getLogger(PathActivity::class.java.name)

    companion object {
        val KEY_PATH_ID = "PathActivity.PATH_ID"
    }

    private val INVALID_PATH_ID: Long = -1

    private lateinit var pathFinder: OneStepRepoImpl

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
                pathFinder = OneStepRepoImpl(this)
                pathFinder.getPath(pathId).subscribe(this::onGetPathSuccess, this::onGetPathError)
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

        }
    }

    fun onGetPathSuccess(path: Path) {
        println(path.toString())
        toolbar.title = path.getPathName()
    }

    fun onGetPathError(throwable: Throwable) {
        Toast.makeText(applicationContext, "Uh-oh! Sorry, I couldn't find that path...", Toast.LENGTH_LONG).show()
        throwable.printStackTrace()
        finish()
    }
}