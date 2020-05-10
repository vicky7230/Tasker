package com.vicky7230.tasker.ui._7finishedDeleted

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskAndTaskList
import kotlinx.android.synthetic.main.deleted_finished_tasks_item_view.view.*
import java.text.SimpleDateFormat
import java.util.*


class DeletedFinishedTasksAdapter(private val deletedFinishedTasks: MutableList<TaskAndTaskList>) :
    RecyclerView.Adapter<DeletedFinishedTasksAdapter.TaskViewHolder>() {

    val outputDateFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    fun updateItems(tasks: List<TaskAndTaskList>) {
        this.deletedFinishedTasks.clear()
        this.deletedFinishedTasks.addAll(tasks)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val taskViewHolder = TaskViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.deleted_finished_tasks_item_view, parent, false)
        )

        return taskViewHolder
    }

    override fun getItemCount(): Int {
        return deletedFinishedTasks.size
    }

    fun removeItem(position: Int) {
        deletedFinishedTasks.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: TaskAndTaskList, position: Int) {
        deletedFinishedTasks.add(position, item)
        notifyItemInserted(position)
    }

    fun getData(): MutableList<TaskAndTaskList> {
        return deletedFinishedTasks
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.onBind(deletedFinishedTasks[position])
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun onBind(task: TaskAndTaskList) {
            if (task.finished == 1)
                itemView.task_icon.setImageResource(R.drawable.ic_marked)
            else
                itemView.task_icon.setImageResource(R.drawable.ic_trash_blue)
            itemView.task_text.text = task.task
            itemView.task_time.text = outputDateFormat.format(Date(task.dateTime))
            itemView.task_curved_dot.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(task.listColor))
        }
    }
}