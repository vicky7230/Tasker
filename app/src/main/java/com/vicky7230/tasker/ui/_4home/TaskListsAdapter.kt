package com.vicky7230.tasker.ui._4home

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskListAndCount
import kotlinx.android.synthetic.main.task_lists_item_view.view.*

class TaskListsAdapter(private val taskLists: MutableList<TaskListAndCount>) :
    RecyclerView.Adapter<TaskListsAdapter.TaskListViewHolder>() {

    interface Callback {
        fun onListClick(
            taskListAndCount: TaskListAndCount,
            taskListCard: CardView,
            listName: AppCompatTextView
        )
    }

    private lateinit var callback: Callback;

    fun setCallback(callback: Callback) {
        this.callback = callback;
    }

    fun updateItems(taskLists: List<TaskListAndCount>) {
        this.taskLists.clear()
        this.taskLists.addAll(taskLists)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val taskListViewHolder = TaskListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.task_lists_item_view,
                parent,
                false
            )
        )

        taskListViewHolder.itemView.setOnClickListener {
            val position = taskListViewHolder.adapterPosition
            callback.onListClick(
                taskLists[position],
                taskListViewHolder.itemView.task_list_card,
                taskListViewHolder.itemView.list_name
            )
        }

        return taskListViewHolder
    }

    override fun getItemCount(): Int {
        return taskLists.size
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        holder.onBind(taskLists[position])
    }

    class TaskListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun onBind(taskList: TaskListAndCount) {
            itemView.list_name.text = taskList.name
            itemView.task_count.text = "${taskList.taskCount} Tasks"
            itemView.task_list_card.setCardBackgroundColor(Color.parseColor(taskList.color))
        }
    }
}
