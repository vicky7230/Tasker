package com.vicky7230.tasker.ui._5newTask

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioAttributes.Builder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.receiver.ReminderBroadcastReceiver
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.utils.AnimUtilskt
import com.vicky7230.tasker.utils.ViewUtils
import com.vicky7230.tasker.worker.CreateTaskWorker
import com.vicky7230.tasker.worker.UpdateTaskWorker
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_new_task.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import org.apache.commons.lang3.RandomStringUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class NewTaskActivity : BaseActivity(), TaskListsAdapter2.Callback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var taskListsAdapter2: TaskListsAdapter2

    private lateinit var newTaskViewModel: NewTaskViewModel
    private lateinit var selectedTaskList2: TaskList2
    private var timeViewContainerHeight = 0
    private var calendarViewContainerHeight = 0
    private var taskListViewContainerHeight = 0
    private lateinit var calendarInstance: Calendar
    private lateinit var task: Task

    companion object {

        const val EXTRAS_TASK_LONG_ID = "taskLongId"
        const val EXTRAS_OPERATION = "operation"
        const val EXTRAS_OPERATION_CREATE = "taskCreate"
        const val EXTRAS_OPERATION_UPDATE = "taskUpdate"
        const val EXTRAS_TASK = "task"

        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, NewTaskActivity::class.java)
            intent.putExtra(EXTRAS_OPERATION, EXTRAS_OPERATION_CREATE)
            return intent
        }

        fun getStartIntent(context: Context, taskLongId: Long): Intent {
            val intent = Intent(context, NewTaskActivity::class.java)
            intent.putExtra(EXTRAS_TASK_LONG_ID, taskLongId)
            intent.putExtra(EXTRAS_OPERATION, EXTRAS_OPERATION_UPDATE)
            return intent
        }
    }

    override fun getViewModel(): NewTaskViewModel {
        newTaskViewModel = ViewModelProvider(this, viewModelFactory)[NewTaskViewModel::class.java]
        return newTaskViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        init()
    }

    private fun init() {

        setKeyboardListener()

        getTimeAndCalendarContainerHeight()

        task_lists_2.layoutManager = LinearLayoutManager(this)
        task_lists_2.adapter = taskListsAdapter2
        taskListsAdapter2.setCallback(this)

        newTaskViewModel.taskList.observe(this, Observer { taskList: List<TaskList> ->

            initializeTaskListView(taskList)

            taskListViewContainerHeight = ViewUtils.dpToPx(348F)
        })

        setTaskListListeners()

        cancel_button.setOnClickListener {
            finish()
        }

        done_button.setOnClickListener {

            if (TextUtils.isEmpty(task_edit_text.text)) {
                showError("Task is empty.")
                return@setOnClickListener
            }

            if (intent != null && intent.getStringExtra(EXTRAS_OPERATION) != null)
                if (intent.getStringExtra(EXTRAS_OPERATION) == EXTRAS_OPERATION_CREATE) {
                    newTaskViewModel.insertTaskInDB(
                        Task(
                            0,
                            RandomStringUtils.randomAlphanumeric(10),
                            (-1).toString(),
                            task_edit_text.text.toString(),
                            calendarInstance.time.time,
                            selectedTaskList2.listSlack
                        )
                    )
                } else if (intent.getStringExtra(EXTRAS_OPERATION) == EXTRAS_OPERATION_UPDATE) {
                    task.task = task_edit_text.text.toString()
                    task.dateTime = calendarInstance.time.time
                    task.listSlack = selectedTaskList2.listSlack
                    newTaskViewModel.updateTaskInDB(task)
                }

        }

        newTaskViewModel.taskInsertedInDB.observe(this, Observer { taskLongId: Long ->
            createReminder()
            createTaskOnServer(taskLongId)
            finish()
        })

        newTaskViewModel.taskUpdatedInDB.observe(this, Observer { taskLongId: Long ->
            updateReminder()
            updateTaskOnServer(taskLongId)
            finish()
        })

        initializeDateAndTimeViews(Date())

        if (intent != null &&
            intent.getLongExtra(EXTRAS_TASK_LONG_ID, -1L) != -1L
        ) {
            newTaskViewModel.task.observe(this, Observer { taskFromDb: Task ->
                task = taskFromDb
                //initializeDateAndTimeViews(Date(task.dateTime))
                val date = Date(task.dateTime)
                calendarInstance.time = date
                val dateFormatter = SimpleDateFormat("d LLL yyyy", Locale.ENGLISH)
                val formattedDate = dateFormatter.format(calendarInstance.time)
                task_date.text = formattedDate

                val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                val formattedTime = timeFormatter.format(calendarInstance.time)
                task_time.text = formattedTime

                calendar_view.minDate = calendarInstance.time.time
                calendar_view.date = calendarInstance.time.time

                task_edit_text.setText(task.task)
            })
            newTaskViewModel.getData(intent.getLongExtra(EXTRAS_TASK_LONG_ID, -1L))
        } else {
            newTaskViewModel.getData(-1L)
        }
    }

    private fun createReminder() {
        val intent = Intent(this, ReminderBroadcastReceiver::class.java)
        intent.putExtra(EXTRAS_TASK, task_edit_text.text.toString())
        val pendingIntent =
            PendingIntent.getBroadcast(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendarInstance.time.time,
            pendingIntent
        )
    }

    private fun updateReminder() {
        val intent = Intent(this, ReminderBroadcastReceiver::class.java)
        intent.putExtra(EXTRAS_TASK, task_edit_text.text.toString())
        val pendingIntent =
            PendingIntent.getBroadcast(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendarInstance.time.time,
            pendingIntent
        )

    }

    private fun setTaskListListeners() {
        which_task_list.setOnClickListener { view: View ->
            if (!view.isSelected) {
                UIUtil.hideKeyboard(this)
                view.isSelected = true
                calendar_view_container.visibility = View.GONE
                time_view_container.visibility = View.GONE
                task_list_view_container.visibility = View.VISIBLE
                time_button.isSelected = false
                calendar_button.isSelected = false
                AnimUtilskt.slideView(task_list_view_container, 0, taskListViewContainerHeight)
            }
        }

        task_list_cancel_button.setOnClickListener {
            which_task_list.isSelected = false
            AnimUtilskt.slideView(task_list_view_container, taskListViewContainerHeight, 0)
        }

        task_list_done_button.setOnClickListener {
            which_task_list.isSelected = false
            AnimUtilskt.slideView(task_list_view_container, taskListViewContainerHeight, 0)
        }
    }

    private fun updateTaskOnServer(taskLongId: Long) {
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

    private fun initializeTaskListView(taskList: List<TaskList>) {
        val taskList2 = arrayListOf<TaskList2>()
        taskList.forEach {
            taskList2.add(TaskList2(it.listSlack, it.name, it.color))
        }
        if (this::task.isInitialized) {
            val item = taskList2.filter {
                task.listSlack == it.listSlack
            }
            item[0].selected = true
            selectedTaskList2 = item[0]
            which_task_list.text = item[0].name
            curved_dot.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item[0].color))
        } else {
            taskList2[0].selected = true
            selectedTaskList2 = taskList2[0]
            which_task_list.text = taskList2[0].name
            curved_dot.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(taskList2[0].color))
        }
        taskListsAdapter2.updateItems(taskList2)
    }

    private fun initializeDateAndTimeViews(date: Date) {
        calendarInstance = Calendar.getInstance()
        calendarInstance.time = date
        val dateFormatter = SimpleDateFormat("d LLL yyyy", Locale.ENGLISH)
        val formattedDate = dateFormatter.format(calendarInstance.time)
        task_date.text = formattedDate

        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val formattedTime = timeFormatter.format(calendarInstance.time)
        task_time.text = formattedTime

        calendar_view.minDate = calendarInstance.time.time
        calendar_view.date = calendarInstance.time.time

        calendar_view.setOnDateChangeListener { view, year, month, dayOfMonth ->
            calendarInstance.set(year, month, dayOfMonth)
            val selectedDate = dateFormatter.format(calendarInstance.time)
            task_date.text = selectedDate
        }

        calendar_button.setOnClickListener { view: View ->
            if (!view.isSelected) {
                UIUtil.hideKeyboard(this)
                view.isSelected = true
                calendar_view_container.visibility = View.VISIBLE
                time_view_container.visibility = View.GONE
                task_list_view_container.visibility = View.GONE
                time_button.isSelected = false
                which_task_list.isSelected = false
                AnimUtilskt.slideView(calendar_view_container, 0, calendarViewContainerHeight)
            }
        }

        date_cancel_button.setOnClickListener {
            calendar_button.isSelected = false
            AnimUtilskt.slideView(calendar_view_container, calendarViewContainerHeight, 0)
        }

        date_done_button.setOnClickListener {
            calendar_button.isSelected = false
            AnimUtilskt.slideView(calendar_view_container, calendarViewContainerHeight, 0)
        }

        time_view.setOnTimeChangedListener { view, hourOfDay, minute ->
            calendarInstance.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendarInstance.set(Calendar.MINUTE, minute)

            task_time.text = timeFormatter.format(calendarInstance.time)
        }

        time_button.setOnClickListener { view: View ->
            if (!view.isSelected) {
                UIUtil.hideKeyboard(this)
                view.isSelected = true
                time_view_container.visibility = View.VISIBLE
                calendar_view_container.visibility = View.GONE
                task_list_view_container.visibility = View.GONE
                calendar_button.isSelected = false
                which_task_list.isSelected = false
                AnimUtilskt.slideView(time_view_container, 0, timeViewContainerHeight)
            }
        }

        time_cancel_button.setOnClickListener {
            time_button.isSelected = false
            AnimUtilskt.slideView(time_view_container, timeViewContainerHeight, 0)
        }

        time_done_button.setOnClickListener {
            time_button.isSelected = false
            AnimUtilskt.slideView(time_view_container, timeViewContainerHeight, 0)
        }
    }

    private fun getTimeAndCalendarContainerHeight() {
        time_view_container.viewTreeObserver.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // gets called after layout has been done but before display
                    // so we can get the height then hide the view
                    timeViewContainerHeight = time_view_container.height // Ahaha!  Gotcha
                    time_view_container.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    time_view_container.visibility = View.GONE
                }
            })

        calendar_view_container.viewTreeObserver.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // gets called after layout has been done but before display
                    // so we can get the height then hide the view
                    calendarViewContainerHeight = calendar_view_container.height // Ahaha!  Gotcha
                    calendar_view_container.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    calendar_view_container.visibility = View.GONE
                }
            })
    }

    private fun setKeyboardListener() {
        KeyboardVisibilityEvent.setEventListener(
            this,
            this,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    if (isOpen) {
                        calendar_view_container.visibility = View.GONE
                        time_view_container.visibility = View.GONE
                        task_list_view_container.visibility = View.GONE
                        calendar_button.isSelected = false
                        time_button.isSelected = false
                        which_task_list.isSelected = false
                        lifecycleScope.launch {
                            delay(100)
                            task_edit_text.requestFocus()
                        }
                    }
                }
            })
    }

    private fun createTaskOnServer(taskLongId: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val taskToCreate = workDataOf(CreateTaskWorker.TASK_LONG_ID to taskLongId)
        val createTaskWorkerRequest = OneTimeWorkRequestBuilder<CreateTaskWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setInputData(taskToCreate)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueue(createTaskWorkerRequest)
    }

    override fun onTaskListClick(taskList2: TaskList2) {
        selectedTaskList2 = taskList2
        which_task_list.text = selectedTaskList2.name
        curved_dot.backgroundTintList = ColorStateList.valueOf(Color.parseColor(taskList2.color))
    }
}
