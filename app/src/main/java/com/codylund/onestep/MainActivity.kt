package com.codylund.onestep

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Explode
import android.transition.Slide
import android.view.Window
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), OnDeletePathCallback, MainView {

    private val TAG = "MainActivity"

    private lateinit var pathFinder: OneStepRepoImpl

    private var mPaths: List<Path> = ArrayList()

    // Path view display stuff
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: PathAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val mCompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        pathFinder = OneStepRepoImpl(this)

        pathFinder.getPaths().subscribe(this::onGetPaths)

        // Path list setup
        viewManager = LinearLayoutManager(this)
        viewAdapter = PathAdapter(this)
        recyclerView = this.paths.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        addPath.setOnClickListener({
            startActivity(Intent(this, NewPathActivity::class.java))
        })

    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(0, 0);
    }

    override fun delete(path: Path) {
        pathFinder.delete(path)
    }

    fun onGetPaths(paths: List<PathImpl>) {
        println("Got new paths: $paths")
        viewAdapter.submitList(paths)
    }

    override fun showPathView(path: Path) {
        var intent = Intent(this, PathActivity::class.java)
        intent.putExtra(PathActivity.KEY_PATH_ID, path.getIdentifier() as Long)
        startActivity(intent)
    }
}
