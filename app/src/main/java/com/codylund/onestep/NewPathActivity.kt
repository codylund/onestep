package com.codylund.onestep

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.activity_new_path.*

class NewPathActivity : AppCompatActivity() {

    private lateinit var pathFinder: OneStepRepoImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_path)

        create.setOnClickListener({
            var name = name.text.toString()
            var description = description.text.toString()
            if ((name != null) && (description != null)) {
                pathFinder = OneStepRepoImpl(this)
                pathFinder.makePath(name, description)
                .subscribeWith(object: DisposableSingleObserver<Long>() {
                    override fun onSuccess(t: Long) {
                        Toast.makeText(applicationContext, "Created new path: ID $t", Toast.LENGTH_LONG).show()
                        finish()
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(applicationContext, "Uh-oh! Failed to create path.", Toast.LENGTH_LONG).show()
                    }
                })
            }
        })

        cancel.setOnClickListener { finish() }
    }


}