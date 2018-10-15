package com.codylund.onestep.views.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
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

    private var pathId: Long = INVALID_PATH_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_step)
        pathId = intent.getLongExtra(KEY_PATH_ID, INVALID_PATH_ID)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.activity_new_step_title)

        //cancel.setOnClickListener { finish() }
    }

    private fun validateParameters(stepWhat: String?, stepWhen: String?, stepWhere: String?,
                           stepWhy: String?, stepHow: String?) : Boolean {
        return (stepWhat != null) || (stepWhen != null) || (stepWhere != null)
                || (stepWhy != null) || (stepHow != null)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_new_step_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()

        return if (id == R.id.confirm) {
            createStep()
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun createStep() {
        // Capture new step values
        var stepWhat = whatView.text.toString()
        var stepWhen = whenView.text.toString()
        var stepWhere = whereView.text.toString()
        var stepWhy = whyView.text.toString()
        var stepHow = howView.text.toString()

        if (validateParameters(stepWhat, stepWhen, stepWhere, stepWhy, stepHow)) {
            // Add the new step to the database
            PathViewModelImpl(this).makeStep(pathId, "", stepWhat, stepWhen, stepWhere, stepWhy, stepHow)
                .subscribe(object : DisposableCompletableObserver() {
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
}