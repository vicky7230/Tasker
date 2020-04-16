package com.vicky7230.tasker.ui._4home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskAndTaskList
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskListAndCount
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.ui._5newTask.NewTaskActivity
import com.vicky7230.tasker.ui._6taskList.TasksActivity
import com.vicky7230.tasker.utils.AnimUtilskt
import com.vicky7230.tasker.worker.UpdateTaskWorker
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeActivity : BaseActivity(), AdapterView.OnItemClickListener, TaskListsAdapter.Callback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var taskListsAdapter: TaskListsAdapter

    @Inject
    lateinit var todaysTaskAdapter: TodaysTaskAdapter

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var listPopupWindow: ListPopupWindow
    private var options = arrayListOf("Task", "List")

    companion object {
        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        taskListsAdapter.setCallback(this)
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        init()
    }

    private fun init() {

        createPopup()

        add_button.setOnClickListener {
            AnimUtilskt.rotateFab(
                add_button,
                45.0F,
                ContextCompat.getColor(this, R.color.colorBlue),
                ContextCompat.getColor(this, R.color.colorWhite)
            )
            listPopupWindow.show()
        }

        setUpTaskListsRecyclerView()

        setUpTodaysTasksRecyclerView()

        homeViewModel.taskFinished.observe(this, Observer { taskLongId: Long ->
            updateTask(taskLongId)
        })

        homeViewModel.taskListAndCount.observe(this, Observer {
            when (it) {
                is Resource.Loading -> showLoading()
                is Resource.Error -> {
                    hideLoading()
                    showError(it.exception.localizedMessage)
                }
                is Resource.Success -> {
                    hideLoading()
                    //Timber.d(it.toString())
                    taskListsAdapter.updateItems(it.data)
                }
            }
        })

        homeViewModel.taskAndTaskList.observe(this, Observer {
            when (it) {
                is Resource.Loading -> showLoading()
                is Resource.Error -> {
                    hideLoading()
                    showError(it.exception.localizedMessage)
                }
                is Resource.Success -> {
                    hideLoading()
                    Timber.e(it.toString())
                    todaysTaskAdapter.updateItems(it.data)
                }
            }
        })

        homeViewModel.getData(getTodaysDateStart(), getTodaysDateEnd())
    }

    private fun setUpTaskListsRecyclerView() {
        task_lists.layoutManager = LinearLayoutManager(this)
        task_lists.isNestedScrollingEnabled = false
        task_lists.adapter = taskListsAdapter
    }

    private fun setUpTodaysTasksRecyclerView() {

        todays_tasks.layoutManager = LinearLayoutManager(this)
        todays_tasks.isNestedScrollingEnabled = false
        todays_tasks.adapter = todaysTaskAdapter

        val rightSwipeListener = object : RightSwipeListener {
            override fun onRightSwiped(viewHolder: RecyclerView.ViewHolder) {
                if (viewHolder is TodaysTaskAdapter.TaskViewHolder) {
                    val task = todaysTaskAdapter.getData()[viewHolder.adapterPosition]
                    homeViewModel.setTaskFinished(task)
                    todaysTaskAdapter.notifyItemChanged(viewHolder.adapterPosition)
                }
            }
        }

        val swipeHelper = object : SwipeHelper(
            this,
            todays_tasks,
            resources.getDimension(R.dimen.underlay_button_width).toInt(),
            rightSwipeListener
        ) {
            override fun instantiateMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<UnderlayButton>
            ) {
                buffer.add(deleteButton())
                buffer.add(editButton())
            }
        }
    }

    private fun editButton(): UnderlayButton {
        return UnderlayButton(
            this@HomeActivity,
            "Edit",
            30,
            R.drawable.ic_edit,
            ContextCompat.getColor(this@HomeActivity, R.color.colorBlue),
            object : UnderlayButtonClickListener {
                override fun onClick(position: Int) {
                    //TODO
                }
            }
        )
    }

    private fun deleteButton(): UnderlayButton {
        return UnderlayButton(
            this@HomeActivity,
            "Delete",
            30,
            R.drawable.ic_trash,
            ContextCompat.getColor(this@HomeActivity, R.color.colorRed),
            object : UnderlayButtonClickListener {
                override fun onClick(position: Int) {

                    val item: TaskAndTaskList = todaysTaskAdapter.getData()[position]
                    todaysTaskAdapter.removeItem(position)

                    val snackBar: Snackbar = Snackbar.make(
                        todays_tasks,
                        "Task was deleted.",
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setAction("UNDO") {
                        todaysTaskAdapter.restoreItem(item, position)
                        todays_tasks.scrollToPosition(position)
                    }

                    snackBar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event == DISMISS_EVENT_TIMEOUT) {
                                homeViewModel.deleteTasK(item)
                            }
                        }
                    })
                    snackBar.setActionTextColor(Color.YELLOW)
                    snackBar.show()
                }
            }
        )
    }

    private fun getTodaysDateStart(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        Timber.d(calendar.time.time.toString())
        return calendar.time.time
    }

    private fun getTodaysDateEnd(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        Timber.d(calendar.time.time.toString())
        return calendar.time.time
    }


    private fun createPopup() {

        listPopupWindow = ListPopupWindow(this)
        listPopupWindow.setAdapter(PopupAdapter(options))
        listPopupWindow.anchorView = add_button
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
                add_button,
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

    override fun onListClick(
        taskListAndCount: TaskListAndCount,
        taskListCard: CardView,
        listName: AppCompatTextView
    ) {

        add_button.hide()

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            taskListCard,
            "cardAnimation"
        )

        startActivity(
            TasksActivity.getStartIntent(
                this@HomeActivity,
                taskListAndCount.listSlack,
                taskListAndCount.color,
                taskListAndCount.name
            ),
            options.toBundle()
        )
    }

    private fun updateTask(taskLongId: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val taskToUpdate = workDataOf(UpdateTaskWorker.TASK_LONG_ID to taskLongId)
        val updateTaskWorkerRequest = OneTimeWorkRequestBuilder<UpdateTaskWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setInputData(taskToUpdate)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueue(updateTaskWorkerRequest)
    }

    override fun onResume() {
        add_button.show()
        super.onResume()
    }
}
