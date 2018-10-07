package com.codylund.onestep.views.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.codylund.onestep.*
import com.codylund.onestep.models.ObservableAdapter
import com.codylund.onestep.models.ObserverAdapter
import com.codylund.onestep.models.Path
import com.codylund.onestep.viewmodels.PathViewModelImpl
import com.codylund.onestep.views.MainView
import com.codylund.onestep.views.adapters.PathAdapter
import io.reactivex.CompletableObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableCompletableObserver
import kotlinx.android.synthetic.main.activity_main.*
import java.util.logging.Logger

class MainActivity : AppCompatActivity(), MainView, ObserverAdapter<List<Path>> {

    private val LOGGER = Logger.getLogger(MainActivity::javaClass.name)

    private val mCompositeDisposable = CompositeDisposable()

    private lateinit var pathFinder: PathViewModelImpl
    private lateinit var pathListObservable: ObservableAdapter<List<Path>>

    // Path view display stuff
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PathAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pathFinder = PathViewModelImpl(this)

        // Subscribe to path list
        pathFinder.getPaths().subscribe(this)

        // Display live path list in recycler view with list adapter
        viewAdapter = PathAdapter(this)
        recyclerView = this.paths.apply {
            // Prevent resizing after adapter updates
            setHasFixedSize(true)

            // Display paths linearly
            layoutManager = LinearLayoutManager(this@MainActivity)

            adapter = viewAdapter
        }

        addPath.setOnClickListener {
            startActivity(Intent(this, NewPathActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(0, 0);
    }

    /**
     * Delete the provided path.
     */
    override fun delete(path: Path) {
        pathFinder.delete(path)
        .subscribe(object: CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                mCompositeDisposable.add(d)
            }

            override fun onComplete() {
                println("Deleted step.")
            }

            override fun onError(e: Throwable) {
                println("Failed to delete path: ${e.localizedMessage}")
                Toast.makeText(applicationContext, "Failed to delete path.", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onSubscribe(observable: ObservableAdapter<List<Path>>) {
        pathListObservable = observable
    }

    /**
     * Callback with fetching latest path list succeeds.
     */
    override fun onSuccess(paths: List<Path>) {
        println("Got new paths: $paths")
        viewAdapter.submitList(paths)
    }

    /**
     * Callback when fetching latest path list fails.
     */
    override fun onFailure(throwable: Throwable) {
        LOGGER.severe("Failed to update path list: " + throwable.localizedMessage)
    }

    /**
     * Show the path view for the given path.
     */
    override fun showPathView(path: Path) {
        var intent = Intent(this, PathActivity::class.java)
        intent.putExtra(PathActivity.KEY_PATH_ID, path.getIdentifier())
        startActivity(intent)
    }

    override fun finish() {
        super.finish()
        pathListObservable?.unsubscribe()
        mCompositeDisposable?.dispose()
    }
}
