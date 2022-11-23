package com.vicky7230.tasker.ui._5newTask

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.db.entities.TaskList
import com.vicky7230.tasker.databinding.ActivityNewTaskBinding
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.utils.AnimUtilskt
import com.vicky7230.tasker.utils.ViewUtils
import dagger.android.AndroidInjection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class NewTaskActivity : BaseActivity(), TaskListsAdapter2.Callback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var taskListsAdapter2: TaskListsAdapter2

    private lateinit var binding: ActivityNewTaskBinding

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

        binding = ActivityNewTaskBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init() {

        setKeyboardListener()

        getTimeAndCalendarContainerHeight()

        binding.taskLists2.layoutManager = LinearLayoutManager(this)
        binding.taskLists2.adapter = taskListsAdapter2
        taskListsAdapter2.setCallback(this)

        newTaskViewModel.taskList.observe(this, Observer { taskList: List<TaskList> ->

            initializeTaskListView(taskList)

            taskListViewContainerHeight = ViewUtils.dpToPx(348F)
        })

        setTaskListListeners()

        binding.cancelButton.setOnClickListener {
            finish()
        }

        binding.doneButton.setOnClickListener {

            if (TextUtils.isEmpty(binding.taskEditText.text)) {
                showError("Task is empty.")
                return@setOnClickListener
            }

            if (intent != null && intent.getStringExtra(EXTRAS_OPERATION) != null)
                if (intent.getStringExtra(EXTRAS_OPERATION) == EXTRAS_OPERATION_CREATE) {
                    newTaskViewModel.insertTaskInDB(
                        Task(
                            0,
                            binding.taskEditText.text.toString(),
                            calendarInstance.time.time,
                            selectedTaskList2.id
                        )
                    )
                } else if (intent.getStringExtra(EXTRAS_OPERATION) == EXTRAS_OPERATION_UPDATE) {
                    task.task = binding.taskEditText.text.toString()
                    task.dateTime = calendarInstance.time.time
                    newTaskViewModel.updateTaskInDB(task)
                }

        }

        newTaskViewModel.taskInsertedInDB.observe(this, Observer { taskLongId: Long ->
            //createReminder()
            finish()
        })

        newTaskViewModel.taskUpdatedInDB.observe(this, Observer { taskLongId: Long ->
            //updateReminder()
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
                binding.taskDate.text = formattedDate

                val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                val formattedTime = timeFormatter.format(calendarInstance.time)
                binding.taskTime.text = formattedTime

                binding.calendarView.minDate = calendarInstance.time.time
                binding.calendarView.date = calendarInstance.time.time

                binding.taskEditText.setText(task.task)
            })
            newTaskViewModel.getData(intent.getLongExtra(EXTRAS_TASK_LONG_ID, -1L))
        } else {
            newTaskViewModel.getData(-1L)
        }
    }

    private fun setTaskListListeners() {
        binding.whichTaskList.setOnClickListener { view: View ->
            if (!view.isSelected) {
                UIUtil.hideKeyboard(this)
                view.isSelected = true
                binding.calendarViewContainer.visibility = View.GONE
                binding.timeViewContainer.visibility = View.GONE
                binding.taskListViewContainer.visibility = View.VISIBLE
                binding.timeButton.isSelected = false
                binding.calendarButton.isSelected = false
                AnimUtilskt.slideView(binding.taskListViewContainer, 0, taskListViewContainerHeight)
            }
        }

        binding.taskListCancelButton.setOnClickListener {
           binding.whichTaskList.isSelected = false
            AnimUtilskt.slideView(binding.taskListViewContainer, taskListViewContainerHeight, 0)
        }

        binding.taskListDoneButton.setOnClickListener {
            binding.whichTaskList.isSelected = false
            AnimUtilskt.slideView(binding.taskListViewContainer, taskListViewContainerHeight, 0)
        }
    }

    private fun initializeTaskListView(taskList: List<TaskList>) {
        val taskList2 = arrayListOf<TaskList2>()
        taskList.forEach {
            taskList2.add(TaskList2(it.id, it.name, it.color))
        }
        if (this::task.isInitialized) {
            val item = taskList2.filter {
                task.listId == it.id
            }
            item[0].selected = true
            selectedTaskList2 = item[0]
            binding.whichTaskList.text = item[0].name
            binding.curvedDot.backgroundTintList = ColorStateList.valueOf(Color.parseColor(item[0].color))
        } else {
            taskList2[0].selected = true
            selectedTaskList2 = taskList2[0]
            binding.whichTaskList.text = taskList2[0].name
            binding.curvedDot.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(taskList2[0].color))
        }
        taskListsAdapter2.updateItems(taskList2)
    }

    private fun initializeDateAndTimeViews(date: Date) {
        calendarInstance = Calendar.getInstance()
        calendarInstance.time = date
        val dateFormatter = SimpleDateFormat("d LLL yyyy", Locale.ENGLISH)
        val formattedDate = dateFormatter.format(calendarInstance.time)
        binding.taskDate.text = formattedDate

        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val formattedTime = timeFormatter.format(calendarInstance.time)
        binding.taskTime.text = formattedTime

        binding.calendarView.minDate = calendarInstance.time.time
        binding.calendarView.date = calendarInstance.time.time

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            calendarInstance.set(year, month, dayOfMonth)
            val selectedDate = dateFormatter.format(calendarInstance.time)
            binding.taskDate.text = selectedDate
        }

        binding.calendarButton.setOnClickListener { view: View ->
            if (!view.isSelected) {
                UIUtil.hideKeyboard(this)
                view.isSelected = true
                binding.calendarViewContainer.visibility = View.VISIBLE
                binding.timeViewContainer.visibility = View.GONE
                binding.taskListViewContainer.visibility = View.GONE
                binding.timeButton.isSelected = false
                binding.whichTaskList.isSelected = false
                AnimUtilskt.slideView(binding.calendarViewContainer, 0, calendarViewContainerHeight)
            }
        }

        binding.dateCancelButton.setOnClickListener {
            binding.calendarButton.isSelected = false
            AnimUtilskt.slideView(binding.calendarViewContainer, calendarViewContainerHeight, 0)
        }

        binding.dateDoneButton.setOnClickListener {
            binding.calendarButton.isSelected = false
            AnimUtilskt.slideView(binding.calendarViewContainer, calendarViewContainerHeight, 0)
        }

        binding.timeView.setOnTimeChangedListener { view, hourOfDay, minute ->
            calendarInstance.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendarInstance.set(Calendar.MINUTE, minute)

            binding.taskTime.text = timeFormatter.format(calendarInstance.time)
        }

        binding.timeButton.setOnClickListener { view: View ->
            if (!view.isSelected) {
                UIUtil.hideKeyboard(this)
                view.isSelected = true
                binding.timeViewContainer.visibility = View.VISIBLE
                binding.calendarViewContainer.visibility = View.GONE
                binding.taskListViewContainer.visibility = View.GONE
                binding.calendarButton.isSelected = false
                binding.whichTaskList.isSelected = false
                AnimUtilskt.slideView(binding.timeViewContainer, 0, timeViewContainerHeight)
            }
        }

        binding.timeCancelButton.setOnClickListener {
            binding.timeButton.isSelected = false
            AnimUtilskt.slideView(binding.timeViewContainer, timeViewContainerHeight, 0)
        }

        binding.timeDoneButton.setOnClickListener {
            binding.timeButton.isSelected = false
            AnimUtilskt.slideView(binding.timeViewContainer, timeViewContainerHeight, 0)
        }
    }

    private fun getTimeAndCalendarContainerHeight() {
        binding.timeViewContainer.viewTreeObserver.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // gets called after layout has been done but before display
                    // so we can get the height then hide the view
                    timeViewContainerHeight = binding.timeViewContainer.height // Ahaha!  Gotcha
                    binding.timeViewContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    binding.timeViewContainer.visibility = View.GONE
                }
            })

        binding.calendarViewContainer.viewTreeObserver.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    // gets called after layout has been done but before display
                    // so we can get the height then hide the view
                    calendarViewContainerHeight = binding.calendarViewContainer.height // Ahaha!  Gotcha
                    binding.calendarViewContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    binding.calendarViewContainer.visibility = View.GONE
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
                        binding.calendarViewContainer.visibility = View.GONE
                        binding.timeViewContainer.visibility = View.GONE
                        binding.taskListViewContainer.visibility = View.GONE
                        binding.calendarButton.isSelected = false
                        binding.timeButton.isSelected = false
                        binding.whichTaskList.isSelected = false
                        lifecycleScope.launch {
                            delay(100)
                            binding.taskEditText.requestFocus()
                        }
                    }
                }
            })
    }

    override fun onTaskListClick(taskList2: TaskList2) {
        selectedTaskList2 = taskList2
        binding.whichTaskList.text = selectedTaskList2.name
        binding.curvedDot.backgroundTintList = ColorStateList.valueOf(Color.parseColor(taskList2.color))
    }
}
