package com.vicky7230.tasker.ui.taskList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.utils.AppConstants
import java.text.SimpleDateFormat
import java.util.*


class TasksForListAdapter(
    private val tasks: MutableList<Task>
) : RecyclerView.Adapter<TasksForListAdapter.TaskViewHolder>() {

    lateinit var listName: String
    val outputDateFormat = SimpleDateFormat("d LLL yyyy hh:mm a", Locale.ENGLISH)

    fun updateItems(tasks: List<Task>, listName: String) {
        this.tasks.clear()
        this.tasks.addAll(tasks)
        this.listName = listName
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.tasks_for_list_item_view,
                parent,
                false
            )
        )

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
                itemView.findViewById<AppCompatTextView>(R.id.task_text).setTextColor(colorBlack)
                itemView.findViewById<AppCompatTextView>(R.id.task_time).setTextColor(colorBlack)
                itemView.findViewById<AppCompatImageView>(R.id.clock_icon)
                    .setColorFilter(colorBlack)
                val colorDarkGray = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorDarkGray
                )
                itemView.findViewById<AppCompatImageView>(R.id.task_ring)
                    .setColorFilter(colorDarkGray)
                itemView.findViewById<View>(R.id.horizontal_line).setBackgroundColor(colorDarkGray)
            } else {
                val colorWhite = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorWhite
                )
                val colorGray = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorGray
                )
                itemView.findViewById<AppCompatTextView>(R.id.task_text).setTextColor(colorWhite)
                itemView.findViewById<AppCompatImageView>(R.id.task_ring).setColorFilter(colorGray)
            }
            itemView.findViewById<AppCompatTextView>(R.id.task_text).text = task.task
            itemView.findViewById<AppCompatTextView>(R.id.task_time).text =
                outputDateFormat.format(Date(task.dateTime))
        }
    }
}