package com.vicky7230.tasker.ui._4home

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskAndTaskList
import kotlinx.android.synthetic.main.tasks_item_view.view.*
import java.text.SimpleDateFormat
import java.util.*


class TodaysTaskAdapter(private val todaysTasks: MutableList<TaskAndTaskList>) :
    RecyclerView.Adapter<TodaysTaskAdapter.TaskViewHolder>() {

    val outputDateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    fun updateItems(tasks: List<TaskAndTaskList>) {
        this.todaysTasks.clear()
        this.todaysTasks.addAll(tasks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val taskViewHolder = TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.tasks_item_view, parent, false)
        )

        return taskViewHolder
    }

    override fun getItemCount(): Int {
        return todaysTasks.size
    }

    fun removeItem(position: Int) {
        todaysTasks.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: TaskAndTaskList, position: Int) {
        todaysTasks.add(position, item)
        notifyItemInserted(position)
    }

    fun getData(): MutableList<TaskAndTaskList> {
        return todaysTasks
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.onBind(todaysTasks[position])
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun onBind(task: TaskAndTaskList) {
            if (task.finished == 1) {
                itemView.task_ring.setImageResource(R.drawable.ic_marked)
                itemView.task_text.setTextColor(itemView.context.resources.getColor(R.color.colorDarkGray))
            } else {
                itemView.task_ring.setImageResource(R.drawable.ic_ring)
                itemView.task_text.setTextColor(itemView.context.resources.getColor(R.color.colorBlack))
            }

            itemView . task_text . text = task . task
                    itemView.task_time.text = outputDateFormat.format(Date(task.dateTime))
            itemView.task_curved_dot.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(task.listColor))
        }
    }
}