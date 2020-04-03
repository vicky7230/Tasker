package com.vicky7230.tasker.ui._6taskList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.utils.AppConstants
import kotlinx.android.synthetic.main.tasks_for_list_item_view.view.*
import java.text.SimpleDateFormat
import java.util.*


class TasksForListAdapter(
    private val tasks: MutableList<Task>
) : RecyclerView.Adapter<TasksForListAdapter.TaskViewHolder>() {

    lateinit var listName: String
    val outputDateFormat = SimpleDateFormat("d LLL yyyy h:m a", Locale.ENGLISH)

    fun updateItems(tasks: List<Task>, listName: String) {
        this.tasks.clear()
        this.tasks.addAll(tasks)
        this.listName = listName
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

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun onBind(task: Task) {
            if (listName == AppConstants.LIST_FAMILY) {
                val colorBlack = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorBlack
                )
                itemView.task_text.setTextColor(colorBlack)
                itemView.task_time.setTextColor(colorBlack)
                itemView.clock_icon.setColorFilter(colorBlack)
                val colorDarkGray = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorDarkGray
                )
                itemView.task_ring.setColorFilter(colorDarkGray)
                itemView.horizontal_line.setBackgroundColor(colorDarkGray)
            } else {
                val colorWhite = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorWhite
                )
                val colorGray = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorGray
                )
                itemView.task_text.setTextColor(colorWhite)
                itemView.task_ring.setColorFilter(colorGray)
            }
            itemView.task_text.text = task.task
            itemView.task_time.text = outputDateFormat.format(Date(task.dateTime))
        }
    }
}