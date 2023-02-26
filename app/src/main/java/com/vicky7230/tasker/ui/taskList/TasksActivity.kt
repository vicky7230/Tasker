package com.vicky7230.tasker.ui.taskList

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.vicky7230.tasker.R
import com.vicky7230.tasker.databinding.ActivityTasksBinding
import com.vicky7230.tasker.ui.base.BaseActivity
import com.vicky7230.tasker.utils.AppConstants
import com.vicky7230.tasker.utils.MessageManager
import com.vicky7230.tasker.widget.ElasticDragDismissFrameLayout
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject


class TasksActivity : BaseActivity<ActivityTasksBinding>() {

    @Inject
    lateinit var tasksForListAdapter: TasksForListAdapter
    private lateinit var chromeFader: ElasticDragDismissFrameLayout.SystemChromeFader
    private lateinit var listRenameDialog: BottomSheetDialog
    private lateinit var listDeleteDialog: BottomSheetDialog
    private lateinit var messageManager: MessageManager
    private lateinit var listName: String

    private val tasksViewModel: TasksViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[TasksViewModel::class.java]
    }

    override fun onBindingCreated() {
        AndroidInjection.inject(this)
        super.onBindingCreated()
        binding.tasks.adapter = tasksForListAdapter
        messageManager = MessageManager(activity = this)
        init()
    }

    private fun init() {
        binding.editListName.setOnClickListener {
            showRenameListDialog()
        }
        binding.deleteList.setOnClickListener {
            showConfirmDeleteDialog()
        }
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
                messageManager.showToast("Please enter list name.")
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

    private fun backPressed() {
        binding.listName.visibility = View.GONE
        binding.editListName.visibility = View.GONE
        onBackPressedDispatcher.onBackPressed()
    }

    override fun registerObservers() = with(tasksViewModel) {
        listDeleted {
            backPressed()
        }
        listRenamed {
            binding.listName.text = it
            listRenameDialog.dismiss()
        }
        tasks {
            val taskTask =
                if (it.size > 1)
                    this@TasksActivity.getString(R.string._0_task)
                        .format(it.size.toString())
                else
                    this@TasksActivity.getString(R.string._2_tasks)
                        .format(it.size.toString())
            binding.taskCount.text = taskTask
            tasksForListAdapter.updateItems(it, listName)
        }
    }

    override fun onStop() {
        if (!isFinishing) {
            Instrumentation().callActivityOnSaveInstanceState(this, Bundle())
        }
        super.onStop()
    }

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
}
