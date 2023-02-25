package com.vicky7230.tasker.ui.newTask

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vicky7230.tasker.R
import com.vicky7230.tasker.utils.AppConstants
import com.vicky7230.tasker.utils.gone
import com.vicky7230.tasker.utils.show

// TaskListsAdapter2 name is wrong please change
class TaskListsAdapter2(private val taskLists2: MutableList<TaskList2>) :
    RecyclerView.Adapter<TaskListsAdapter2.TaskListViewHolder2>() {

    interface Callback {
        fun onTaskListClick(taskList2: TaskList2)
    }

    private lateinit var callback: Callback

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun updateItems(taskLists2: List<TaskList2>) {
        this.taskLists2.clear()
        this.taskLists2.addAll(taskLists2)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder2 {
        val taskListViewHolder2 = TaskListViewHolder2(
            LayoutInflater.from(parent.context).inflate(
                R.layout.task_lists_item_view_2,
                parent,
                false
            )
        )

        taskListViewHolder2.itemView.findViewById<CardView>(R.id.task_list_card)
            .setOnClickListener {
                val position = taskListViewHolder2.adapterPosition
                taskLists2.forEach {
                    it.selected = false
                }
                taskLists2[position].selected = true
                notifyDataSetChanged()
                callback.onTaskListClick(taskLists2[position])
            }

        return taskListViewHolder2
    }

    override fun getItemCount(): Int {
        return taskLists2.size
    }

    override fun onBindViewHolder(holder: TaskListViewHolder2, position: Int) {
        holder.onBind(taskLists2[position])
    }

    class TaskListViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(taskList2: TaskList2) {
            if (taskList2.name == AppConstants.LIST_FAMILY) {
                val colorBlack = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorBlack
                )
                itemView.findViewById<AppCompatTextView>(R.id.name).setTextColor(colorBlack)
            } else {
                val colorWhite = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorWhite
                )
                itemView.findViewById<AppCompatTextView>(R.id.name).setTextColor(colorWhite)
            }
            itemView.findViewById<AppCompatTextView>(R.id.name).text = taskList2.name
            itemView.findViewById<CardView>(R.id.task_list_card)
                .setCardBackgroundColor(Color.parseColor(taskList2.color))
            if (taskList2.selected)
                itemView.findViewById<AppCompatImageView>(R.id.selected_indicator).show()
            else
                itemView.findViewById<AppCompatImageView>(R.id.selected_indicator).gone()
        }
    }
}
