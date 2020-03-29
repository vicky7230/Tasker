package com.vicky7230.tasker.ui._6taskList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.db.entities.Task
import kotlinx.android.synthetic.main.tasks_for_list_item_view.view.*


class TasksForListAdapter(private val tasks: MutableList<Task>) :
    RecyclerView.Adapter<TasksForListAdapter.TaskViewHolder>() {

    fun updateItems(tasks: List<Task>) {
        this.tasks.clear()
        this.tasks.addAll(tasks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val taskViewHolder = TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.tasks_for_list_item_view,
                parent,
                false
            )
        )

        return taskViewHolder
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.onBind(tasks[position])
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun onBind(task: Task) {
            itemView.task_text.text = task.task
        }
    }
}