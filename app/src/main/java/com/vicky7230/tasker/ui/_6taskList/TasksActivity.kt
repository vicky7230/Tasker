package com.vicky7230.tasker.ui._6taskList

import android.annotation.SuppressLint
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.ui._4home.RightSwipeListener
import com.vicky7230.tasker.ui._4home.SwipeHelper
import com.vicky7230.tasker.ui._4home.UnderlayButton
import com.vicky7230.tasker.ui._4home.UnderlayButtonClickListener
import com.vicky7230.tasker.ui._5newTask.NewTaskActivity
import com.vicky7230.tasker.utils.AppConstants
import com.vicky7230.tasker.widget.ElasticDragDismissFrameLayout
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_tasks.*
import kotlinx.android.synthetic.main.bottom_sheet_delete_list.*
import kotlinx.android.synthetic.main.bottom_sheet_rename_list.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class TasksActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tasksForListAdapter: TasksForListAdapter

    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var listName: String
    private lateinit var chromeFader: ElasticDragDismissFrameLayout.SystemChromeFader
    private lateinit var listRenameDialog: BottomSheetDialog
    private lateinit var listDeleteDialog: BottomSheetDialog

    companion object {

        const val EXTRAS_LIST_ID = "list_id"
        const val EXTRAS_LIST_COLOR = "list_color"
        const val EXTRAS_LIST_NAME = "list_name"

        fun getStartIntent(
            context: Context,
            listId: Long,
            listColor: String,
            listName: String
        ): Intent {
            val intent = Intent(context, TasksActivity::class.java)
            intent.putExtra(EXTRAS_LIST_ID, listId)
            intent.putExtra(EXTRAS_LIST_COLOR, listColor)
            intent.putExtra(EXTRAS_LIST_NAME, listName)
            return intent
        }
    }

    override fun getViewModel(): TasksViewModel {
        tasksViewModel = ViewModelProvider(this, viewModelFactory)[TasksViewModel::class.java]
        return tasksViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        tasks.layoutManager = LinearLayoutManager(this)
        tasks.adapter = tasksForListAdapter

        val rightSwipeListener = object : RightSwipeListener {
            override fun onRightSwiped(viewHolder: RecyclerView.ViewHolder) {
                if (viewHolder is TasksForListAdapter.TaskViewHolder) {
                    val task = tasksForListAdapter.getData()[viewHolder.adapterPosition]
                    tasksViewModel.setTaskFinished(task)
                    tasksForListAdapter.notifyItemChanged(viewHolder.adapterPosition)
                }
            }
        }

        val swipeHelper = object : SwipeHelper(
            this,
            tasks,
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

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        edit_list_name.setOnClickListener {
            //showRenameListDialog()
        }

        delete_list.setOnClickListener {
            showConfirmDeleteDialog()
        }

        tasksViewModel.listRenamed.observe(this, Observer {
            when (it) {
                is Resource.Loading -> showLoading()
                is Resource.Error -> {
                    hideLoading()
                    showToast(it.exception.localizedMessage)
                }
                is Resource.Success -> {
                    if (this::listRenameDialog.isInitialized) {
                        listRenameDialog.dismiss()
                    }
                    list_name.text = it.data
                    hideLoading()
                }
            }
        })

        tasksViewModel.taskFinished.observe(this, Observer { taskLongId: Long ->
            //updateTask(taskLongId)
        })

        tasksViewModel.taskDeleted.observe(this, Observer { taskLongId: Long ->
            //updateTask(taskLongId)
        })

        tasksViewModel.listDeleted.observe(this, Observer { listDeleted: Boolean ->
            onBackPressed()
            //updateTask(taskLongId)
        })

        chromeFader = object : ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            override fun onDragDismissed() {
                Timber.d("onDragDismissed")
                list_name.visibility = View.GONE
                edit_list_name.visibility = View.GONE
                delete_list.visibility = View.GONE
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
            && intent.getLongExtra(EXTRAS_LIST_ID, -1L) != -1L
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
                delete_list.setColorFilter(colorBlack)
            }

            val listId = intent.getLongExtra(EXTRAS_LIST_ID, -1)
            if (listId != -1L)
                tasksViewModel.getTasks(listId)

        }
    }

    /*private fun showRenameListDialog() {
        val view: View = layoutInflater.inflate(R.layout.bottom_sheet_rename_list, null)
        listRenameDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        listRenameDialog.setContentView(view)
        listRenameDialog.rename_list_button.setOnClickListener {
            if (listRenameDialog.new_list_name!!.text!!.isEmpty()) {
                showToast("Please enter list name.")
                return@setOnClickListener
            }

            if (intent != null && intent.getStringExtra(EXTRAS_LIST_SLACK) != null) {
                val listSlack = intent.getStringExtra(EXTRAS_LIST_SLACK)
                tasksViewModel.renameTaskList(
                    listRenameDialog.new_list_name.text.toString(),
                    listSlack!!
                )
            }
        }

        listRenameDialog.show()
    }*/

    private fun showConfirmDeleteDialog() {
        val view: View = layoutInflater.inflate(R.layout.bottom_sheet_delete_list, null)
        listDeleteDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        listDeleteDialog.setContentView(view)

        listDeleteDialog.delete_list_button_no.setOnClickListener {
            listDeleteDialog.dismiss()
        }

        listDeleteDialog.delete_list_button_yes.setOnClickListener {

            if (intent != null && intent.getLongExtra(EXTRAS_LIST_ID, -1L) != -1L) {
                val listIdLong = intent.getLongExtra(EXTRAS_LIST_ID, -1L)
                tasksViewModel.deleteTaskList(listIdLong)
            }
        }

        listDeleteDialog.show()
    }

    private fun editButton(): UnderlayButton {
        return UnderlayButton(
            this@TasksActivity,
            "Edit",
            30,
            R.drawable.ic_edit,
            ContextCompat.getColor(this@TasksActivity, R.color.colorBlue),
            object : UnderlayButtonClickListener {
                override fun onClick(position: Int) {
                    val task = tasksForListAdapter.getData()[position]
                    startActivity(NewTaskActivity.getStartIntent(this@TasksActivity, task.id))
                }
            }
        )
    }

    private fun deleteButton(): UnderlayButton {
        return UnderlayButton(
            this@TasksActivity,
            "Delete",
            30,
            R.drawable.ic_trash,
            ContextCompat.getColor(this@TasksActivity, R.color.colorRed),
            object : UnderlayButtonClickListener {
                override fun onClick(position: Int) {

                    val task = tasksForListAdapter.getData()[position]
                    tasksForListAdapter.removeItem(position)

                    val snackBar: Snackbar = Snackbar.make(
                        tasks,
                        "Task was deleted.",
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setAction("UNDO") {
                        tasksForListAdapter.restoreItem(task, position)
                        tasks.scrollToPosition(position)
                    }

                    snackBar.addCallback(object :
                        BaseTransientBottomBar.BaseCallback<Snackbar?>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event == DISMISS_EVENT_TIMEOUT) {
                                tasksViewModel.deleteTasK(task)
                            }
                        }
                    })
                    snackBar.setActionTextColor(Color.YELLOW)
                    snackBar.show()
                }
            }
        )
    }

    /*private fun updateTask(taskLongId: Long) {
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
    }*/

    override fun onBackPressed() {
        list_name.visibility = View.GONE
        edit_list_name.visibility = View.GONE
        super.onBackPressed()
    }

    override fun onStop() {
        //Fixes shared element transition
        //https://stackoverflow.com/questions/60876188/android-clears-activity-to-activity-shared-element-transition-exit-animation-aft
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q && !isFinishing) {
            Instrumentation().callActivityOnSaveInstanceState(this, Bundle())
        }
        super.onStop()
    }
}
