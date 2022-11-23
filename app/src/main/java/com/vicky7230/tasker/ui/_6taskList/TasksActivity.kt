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
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.databinding.ActivityTasksBinding
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.ui._4home.RightSwipeListener
import com.vicky7230.tasker.ui._4home.SwipeHelper
import com.vicky7230.tasker.ui._4home.UnderlayButton
import com.vicky7230.tasker.ui._4home.UnderlayButtonClickListener
import com.vicky7230.tasker.ui._5newTask.NewTaskActivity
import com.vicky7230.tasker.utils.AppConstants
import com.vicky7230.tasker.widget.ElasticDragDismissFrameLayout
import dagger.android.AndroidInjection
import timber.log.Timber
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

    private lateinit var binding: ActivityTasksBinding

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

        binding = ActivityTasksBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(R.layout.activity_tasks)

        binding.tasks.layoutManager = LinearLayoutManager(this)
        binding.tasks.adapter = tasksForListAdapter

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
            binding.tasks,
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

        binding.editListName.setOnClickListener {
            showRenameListDialog()
        }

        binding.deleteList.setOnClickListener {
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
                    binding.listName.text = it.data
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
        })

        chromeFader = object : ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            override fun onDragDismissed() {
                Timber.d("onDragDismissed")
                binding.listName.visibility = View.GONE
                binding.editListName.visibility = View.GONE
                binding.deleteList.visibility = View.GONE
                supportFinishAfterTransition()
            }
        }

        binding.draggableLayout.addListener(chromeFader)

        tasksViewModel.tasks.observe(this, Observer {
            binding.taskCount.text = "${it.size} task"
            tasksForListAdapter.updateItems(it, listName)
        })

        if (intent != null
            && intent.getStringExtra(EXTRAS_LIST_COLOR) != null
            && intent.getStringExtra(EXTRAS_LIST_NAME) != null
            && intent.getLongExtra(EXTRAS_LIST_ID, -1L) != -1L
        ) {
            val listColor = intent.getStringExtra(EXTRAS_LIST_COLOR)
            binding.tasks.setBackgroundColor(Color.parseColor(listColor))
            binding.taskListCard.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(listColor))

            listName = intent.getStringExtra(EXTRAS_LIST_NAME)!!
            binding.listName.text = listName

            if (listName == AppConstants.LIST_FAMILY) {
                val colorBlack = ContextCompat.getColor(
                    this,
                    R.color.colorBlack
                )
                val colorDarkGray = ContextCompat.getColor(
                    this,
                    R.color.colorDarkGray
                )
                binding.listName.setTextColor(colorBlack)
                binding.taskCount.setTextColor(colorDarkGray)
                binding.editListName.setColorFilter(colorBlack)
                binding.deleteList.setColorFilter(colorBlack)
            }

            val listId = intent.getLongExtra(EXTRAS_LIST_ID, -1)
            if (listId != -1L)
                tasksViewModel.getTasks(listId)

        }
    }

    private fun showRenameListDialog() {
        val view: View = layoutInflater.inflate(R.layout.bottom_sheet_rename_list, null)
        listRenameDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        listRenameDialog.setContentView(view)
        view.findViewById<MaterialButton>(R.id.rename_list_button).setOnClickListener {
            if (view.findViewById<AppCompatEditText>(R.id.new_list_name)!!.text!!.isEmpty()) {
                showToast("Please enter list name.")
                return@setOnClickListener
            }

            if (intent != null && intent.getLongExtra(EXTRAS_LIST_ID, -1) != -1L) {
                val listId = intent.getLongExtra(EXTRAS_LIST_ID, -1)
                tasksViewModel.updateTaskList(
                    listId,
                    view.findViewById<AppCompatEditText>(R.id.new_list_name).text.toString()
                )
            }
        }

        listRenameDialog.show()
    }

    private fun showConfirmDeleteDialog() {
        val view: View = layoutInflater.inflate(R.layout.bottom_sheet_delete_list, null)
        listDeleteDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        listDeleteDialog.setContentView(view)

        view.findViewById<MaterialButton>(R.id.delete_list_button_no).setOnClickListener {
            listDeleteDialog.dismiss()
        }

        view.findViewById<MaterialButton>(R.id.delete_list_button_yes).setOnClickListener {

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
                        binding.tasks,
                        "Task was deleted.",
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setAction("UNDO") {
                        tasksForListAdapter.restoreItem(task, position)
                        binding.tasks.scrollToPosition(position)
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

    override fun onBackPressed() {
        binding.listName.visibility = View.GONE
        binding.editListName.visibility = View.GONE
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
