package com.vicky7230.tasker.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vicky7230.tasker.Config.TIME_PATTERN
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskAndTaskList
import java.text.SimpleDateFormat
import java.util.*


class TodayTaskAdapter(private val todayTasks: MutableList<TaskAndTaskList>) :
    RecyclerView.Adapter<TodayTaskAdapter.TaskViewHolder>() {

    val outputDateFormat = SimpleDateFormat(TIME_PATTERN, Locale.ENGLISH)

    fun updateItems(tasks: List<TaskAndTaskList>) {
        this.todayTasks.clear()
        this.todayTasks.addAll(tasks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder =
        TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.tasks_item_view, parent, false)
        )

    override fun getItemCount(): Int {
        return todayTasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.onBind(todayTasks[position])
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(task: TaskAndTaskList) {
            if (task.finished == 1) {
                itemView.findViewById<AppCompatImageView>(R.id.task_ring)
                    .setImageResource(R.drawable.ic_marked)
                itemView.findViewById<AppCompatTextView>(R.id.task_text)
                    .setTextColor(ContextCompat.getColor(itemView.context,R.color.colorDarkGray))
            } else {
                itemView.findViewById<AppCompatImageView>(R.id.task_ring)
                    .setImageResource(R.drawable.ic_ring)
                itemView.findViewById<AppCompatTextView>(R.id.task_text)
                    .setTextColor(ContextCompat.getColor(itemView.context,R.color.colorBlack))
            }

            itemView.findViewById<AppCompatTextView>(R.id.task_text).text = task.task
            itemView.findViewById<AppCompatTextView>(R.id.task_time).text =
                outputDateFormat.format(Date(task.dateTime))
            itemView.findViewById<View>(R.id.task_curved_dot).backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(task.listColor))
        }
    }
}