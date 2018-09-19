package com.codylund.onestep

import android.content.Intent
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.codylund.onestep.PathAdapter.PathViewHolder.Companion.DIFF_CALLBACK
import com.codylund.onestep.R.layout.item_path

class PathAdapter(var mainView: MainView) : ListAdapter<Path, PathAdapter.PathViewHolder>(DIFF_CALLBACK) {

    class PathViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameView = itemView.findViewById<TextView>(R.id.name)
        var descriptionView = itemView.findViewById<TextView>(R.id.description)
        var deleteButton = itemView.findViewById<Button>(R.id.delete)

        companion object {
            val DIFF_CALLBACK = object: DiffUtil.ItemCallback<Path>() {
                override fun areItemsTheSame(first: Path, second: Path): Boolean {
                    return first.getIdentifier() == second.getIdentifier()
                }

                override fun areContentsTheSame(first: Path, second: Path): Boolean {
                    return (first.getFirstStepId() != second.getFirstStepId())
                        || (first.getPathDescription() != second.getPathDescription())
                        || (first.getPathName() != second.getPathName())
                        || (first.getStatus() != second.getStatus())
                }

            }
        }

        fun bindData(path: Path) {
            nameView.text = path.getPathName()
            descriptionView.text = path.getPathDescription()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, id: Int): PathViewHolder {
        return PathViewHolder(LayoutInflater.from(parent.context).inflate(item_path, parent, false))
    }


    override fun onBindViewHolder(holder: PathViewHolder, position: Int) {
        var path = getItem(position)
        holder.bindData(getItem(position))
        holder.deleteButton.setOnClickListener({
            mainView.delete(path)
        })
        holder.itemView.setOnClickListener({
            mainView.showPathView(path)
        })
    }
}