package com.vicky7230.tasker.ui._6taskList

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.ui._4home.PopupAdapter
import com.vicky7230.tasker.ui._5newTask.NewTaskActivity
import com.vicky7230.tasker.utils.AnimUtilskt
import com.vicky7230.tasker.utils.AppConstants
import com.vicky7230.tasker.widget.ElasticDragDismissLinearLayout
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_tasks.*
import timber.log.Timber
import javax.inject.Inject


class TasksActivity : BaseActivity(), AdapterView.OnItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tasksForListAdapter: TasksForListAdapter

    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var listName: String
    private lateinit var chromeFader: ElasticDragDismissLinearLayout.SystemChromeFader
    private lateinit var listPopupWindow: ListPopupWindow
    private var options = arrayListOf("Task", "List")

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

        createPopup()

        add_button_2.setOnClickListener {
            AnimUtilskt.rotateFab(
                add_button_2,
                45.0F,
                ContextCompat.getColor(this, R.color.colorBlue),
                ContextCompat.getColor(this, R.color.colorWhite)
            )
            listPopupWindow.show()
        }

        chromeFader = object : ElasticDragDismissLinearLayout.SystemChromeFader(this) {
            override fun onDragDismissed() {
                Timber.d("onDragDismissed")
                list_name.visibility = View.GONE
                edit_list_name.visibility = View.GONE
                add_button_2.visibility = View.GONE
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

    private fun createPopup() {

        listPopupWindow = ListPopupWindow(this)
        listPopupWindow.setAdapter(PopupAdapter(options))
        listPopupWindow.anchorView = add_button_2
        listPopupWindow.setDropDownGravity(Gravity.END)
        listPopupWindow.width = resources.getDimension(R.dimen.popup_width).toInt()
        listPopupWindow.height = ListPopupWindow.WRAP_CONTENT
        listPopupWindow.verticalOffset =
            resources.getDimension(R.dimen.popup_vertical_offset).toInt()
        listPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.curved_bg))
        listPopupWindow.isModal = true
        listPopupWindow.setOnItemClickListener(this)
        listPopupWindow.setOnDismissListener {
            AnimUtilskt.rotateFab(
                add_button_2,
                0.0F,
                ContextCompat.getColor(this, R.color.colorWhite),
                ContextCompat.getColor(this, R.color.colorBlue)
            )
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 0) {
            startActivity(NewTaskActivity.getStartIntent(this))
        }

        listPopupWindow.dismiss()
    }

    override fun onBackPressed() {
        list_name.visibility = View.GONE
        edit_list_name.visibility = View.GONE
        add_button_2.visibility = View.GONE
        super.onBackPressed()
    }
}
