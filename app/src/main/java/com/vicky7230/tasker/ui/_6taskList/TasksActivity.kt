package com.vicky7230.tasker.ui._6taskList

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.utils.AppConstants
import com.vicky7230.tasker.widget.ElasticDragDismissLinearLayout
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_tasks.*
import timber.log.Timber
import javax.inject.Inject


class TasksActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tasksForListAdapter: TasksForListAdapter

    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var listName: String
    private lateinit var chromeFader: ElasticDragDismissLinearLayout.SystemChromeFader

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

        tasks.layoutManager = LinearLayoutManager(this)
        tasks.adapter = tasksForListAdapter

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        chromeFader = object : ElasticDragDismissLinearLayout.SystemChromeFader(this) {
            override fun onDragDismissed() {
                Timber.d("onDragDismissed")
                supportFinishAfterTransition()
            }
        }

        draggable_layout.addListener(chromeFader)

        tasksViewModel.tasks.observe(this, Observer {
            task_count.text = "${it.size} task"
            tasksForListAdapter.updateItems(it, listName)
        })

        if (intent != null
            && intent.getStringExtra(EXTRAS_LIST_COLOR) != null
            && intent.getStringExtra(EXTRAS_LIST_NAME) != null
            && intent.getStringExtra(EXTRAS_LIST_SLACK) != null
        ) {
            val listColor = intent.getStringExtra(EXTRAS_LIST_COLOR)
            tasks.setBackgroundColor(Color.parseColor(listColor))
            task_list_card.backgroundTintList = ColorStateList.valueOf(Color.parseColor(listColor))

            listName = intent.getStringExtra(EXTRAS_LIST_NAME)!!
            list_name.text = listName

            if (listName == AppConstants.LIST_FAMILY) {
                val colorBlack = ContextCompat.getColor(
                    this,
                    R.color.colorBlack
                )
                val colorDarkGray = ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
                list_name.setTextColor(colorBlack)
                task_count.setTextColor(colorDarkGray)
                edit_list_name.setColorFilter(colorBlack)
            }

            val listSlack = intent.getStringExtra(EXTRAS_LIST_SLACK)
            if (listSlack != null)
                tasksViewModel.getTasks(listSlack)
        }
    }

    /*override fun onBackPressed() {
        list_name.visibility = View.GONE
        edit_list_name.visibility = View.GONE
        super.onBackPressed()
    }*/
}
