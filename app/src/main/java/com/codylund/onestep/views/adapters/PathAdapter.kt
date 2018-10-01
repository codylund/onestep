package com.codylund.onestep.views.adapters

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.codylund.onestep.*
import com.codylund.onestep.R.layout.item_path
import com.codylund.onestep.models.Path
import com.codylund.onestep.views.MainView
import java.util.logging.Logger

class PathAdapter(var mainView: MainView) : ListAdapter<Path, PathAdapter.PathViewHolder>(DIFF_CALLBACK) {

    val LOGGER = Logger.getLogger(PathAdapter::javaClass.name)

    companion object {
        val DIFF_CALLBACK = Differ<Path>()
    }

    class PathViewHolder(val mainView: MainView, itemView: View) : RecyclerView.ViewHolder(itemView) {

        var nameView = itemView.findViewById<TextView>(R.id.name)
        var descriptionView = itemView.findViewById<TextView>(R.id.description)
        var deleteButton = itemView.findViewById<Button>(R.id.delete)
        lateinit var pathData: Path

        fun bindData(path: Path) {
            pathData = path
            nameView.text = pathData.getPathName()
            descriptionView.text = pathData.getPathDescription()
            deleteButton.setOnClickListener {
                mainView.delete(pathData)
            }
            itemView.setOnClickListener {
                mainView.showPathView(pathData)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, id: Int): PathViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(item_path, parent, false)
        return PathViewHolder(mainView, viewHolder)
    }

    override fun onBindViewHolder(holder: PathViewHolder, position: Int) {
        var path = getItem(position)
        holder.bindData(path)
    }
}