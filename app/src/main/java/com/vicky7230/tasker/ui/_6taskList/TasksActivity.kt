package com.vicky7230.tasker.ui._6taskList

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._0base.BaseActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_tasks.*
import javax.inject.Inject

class TasksActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var tasksForListAdapter: TasksForListAdapter

    lateinit var tasksViewModel: TasksViewModel

    companion object {

        const val EXTRAS_LIST_SLACK = "list_slack"
        const val EXTRAS_LIST_COLOR = "list_color"
        const val EXTRAS_LIST_NAME = "list_name"

        fun getStartIntent(
            context: Context,
            listSlack: String,
            listColor: String,
            listName: String
        ): Intent {
            val intent = Intent(context, TasksActivity::class.java)
            intent.putExtra(EXTRAS_LIST_SLACK, listSlack)
            intent.putExtra(EXTRAS_LIST_COLOR, listColor)
            intent.putExtra(EXTRAS_LIST_NAME, listName)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        tasksViewModel = ViewModelProvider(this, viewModelFactory)[TasksViewModel::class.java]

        tasksViewModel.tasks.observe(this, Observer {
            tasksForListAdapter.updateItems(it)
        })

        if (intent != null
            && intent.getStringExtra(EXTRAS_LIST_COLOR) != null
            && intent.getStringExtra(EXTRAS_LIST_NAME) != null
            && intent.getStringExtra(EXTRAS_LIST_SLACK) != null
        ) {
            val listColor = intent.getStringExtra(EXTRAS_LIST_COLOR)
            task_list_card.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(listColor))

            val listName = intent.getStringExtra(EXTRAS_LIST_NAME)
            list_name.text = listName

            val listSlack = intent.getStringExtra(EXTRAS_LIST_SLACK)
            if (listSlack != null)
                tasksViewModel.getTasks(listSlack)
        }
    }

    override fun onBackPressed() {
        list_name.visibility = View.GONE
        edit_list_name.visibility = View.GONE
        super.onBackPressed()
    }
}
