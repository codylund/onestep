package com.codylund.onestep.views.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.codylund.onestep.R
import com.codylund.onestep.viewmodels.PathViewModelImpl
import io.reactivex.observers.DisposableCompletableObserver
import kotlinx.android.synthetic.main.activity_new_step.*

class NewStepActivity : AppCompatActivity() {

    companion object {
        val KEY_PATH_ID = NewStepActivity::class.java.name + ".KEY_PATH_ID"
    }

    private val INVALID_PATH_ID: Long = -1

    private lateinit var pathFinder: PathViewModelImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_step)
        val pathId = intent.getLongExtra(KEY_PATH_ID, INVALID_PATH_ID)

        create.setOnClickListener {
            // Capture new step values
            var stepWhat = whatView.text.toString()
            var stepWhen = whenView.text.toString()
            var stepWhere = whereView.text.toString()
            var stepWhy = whyView.text.toString()
            var stepHow = howView.text.toString()

            if (validateParameters(stepWhat, stepWhen, stepWhere, stepWhy, stepHow)) {
                // Add the new step to the database
                pathFinder = PathViewModelImpl(this)
                pathFinder.makeStep(pathId, "", stepWhat, stepWhen, stepWhere, stepWhy, stepHow)
                        .subscribeWith(object : DisposableCompletableObserver() {
                            override fun onComplete() {
                                finish()
                                dispose()
                            }

                            override fun onError(e: Throwable) {
                                Toast.makeText(applicationContext, "Uh-oh! The step couldn't be added.", Toast.LENGTH_LONG).show()
                                e.printStackTrace()
                                dispose()
                            }
                        })
            }
        }

        cancel.setOnClickListener { finish() }
    }

    private fun validateParameters(stepWhat: String?, stepWhen: String?, stepWhere: String?,
                           stepWhy: String?, stepHow: String?) : Boolean {
        return (stepWhat != null) || (stepWhen != null) || (stepWhere != null)
                || (stepWhy != null) || (stepHow != null)
    }

}