package com.vicky7230.tasker.ui._4home

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskListAndCount
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.databinding.ActivityHomeBinding
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.ui._5newTask.NewTaskActivity
import com.vicky7230.tasker.ui._6taskList.TasksActivity
import com.vicky7230.tasker.ui._7finishedDeleted.FinishedDeletedTasksActivity
import com.vicky7230.tasker.utils.AnimUtilskt
import dagger.android.AndroidInjection
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class HomeActivity : BaseActivity(), TaskListsAdapter.Callback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var taskListsAdapter: TaskListsAdapter

    @Inject
    lateinit var todaysTaskAdapter: TodaysTaskAdapter

    private lateinit var binding: ActivityHomeBinding

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var listPopupWindow: ListPopupWindow
    private var options = arrayListOf("Task", "List")
    private var taskListColor: String = "-1"
    private lateinit var colorsDialog: BottomSheetDialog

    companion object {
        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    override fun getViewModel(): HomeViewModel {
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        return homeViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        taskListsAdapter.setCallback(this)

        init()
    }

    @SuppressLint("SetTextI18n")
    private fun init() {

        createPopup()

        binding.addButton.setOnClickListener {
            AnimUtilskt.rotateFab(
                binding.addButton,
                45.0F,
                ContextCompat.getColor(this, R.color.colorBlue),
                ContextCompat.getColor(this, R.color.colorWhite)
            )
            listPopupWindow.show()
        }

        binding.moreButton.setOnClickListener {
            val view: View = layoutInflater.inflate(R.layout.bottom_sheet, null)
            val dialog = BottomSheetDialog(this, R.style.BottomSheetDialog) // Style here
            dialog.setContentView(view)
            //dialog.account_email.text = "You($userEmail)"

            view.findViewById<AppCompatTextView>(R.id.finished_tasks).setOnClickListener {
                startActivity(
                    FinishedDeletedTasksActivity.getStartIntent(
                        this,
                        FinishedDeletedTasksActivity.FLAG_FINISHED
                    )
                )
            }

            view.findViewById<AppCompatTextView>(R.id.deleted_tasks).setOnClickListener {
                startActivity(
                    FinishedDeletedTasksActivity.getStartIntent(
                        this,
                        FinishedDeletedTasksActivity.FLAG_DELETED
                    )
                )
            }

            view.findViewById<AppCompatTextView>(R.id.privacy_policy).setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://tasker-1.flycricket.io/privacy.html")
                startActivity(i)
            }

            view.findViewById<AppCompatTextView>(R.id.rate).setOnClickListener {
                val appPackageName = packageName
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appPackageName")
                        )
                    )
                } catch (e: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                    )
                }
            }

            dialog.show()
        }

        setUpTaskListsRecyclerView()

        setUpTodaysTasksRecyclerView()

        homeViewModel.taskListAndCount.observe(this, Observer {
            when (it) {
                is Resource.Loading -> showLoading()
                is Resource.Error -> {
                    hideLoading()
                    showError(it.exception.localizedMessage)
                }
                is Resource.Success -> {
                    hideLoading()
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
                    //Timber.e(it.toString())
                    todaysTaskAdapter.updateItems(it.data)
                }
            }
        })

        homeViewModel.getData(getTodaysDateStart(), getTodaysDateEnd())
    }

    private fun setUpTaskListsRecyclerView() {
        binding.taskLists.layoutManager = LinearLayoutManager(this)
        binding.taskLists.isNestedScrollingEnabled = false
        binding.taskLists.adapter = taskListsAdapter
    }

    private fun setUpTodaysTasksRecyclerView() {

        binding.todaysTasks.layoutManager = LinearLayoutManager(this)
        binding.todaysTasks.isNestedScrollingEnabled = false
        binding.todaysTasks.adapter = todaysTaskAdapter

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
            binding.todaysTasks,
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
                    val task = todaysTaskAdapter.getData()[position]
                    startActivity(NewTaskActivity.getStartIntent(this@HomeActivity, task.id))
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

                    val task = todaysTaskAdapter.getData()[position]
                    todaysTaskAdapter.removeItem(position)

                    val snackBar: Snackbar = Snackbar.make(
                        binding.todaysTasks,
                        "Task was deleted.",
                        Snackbar.LENGTH_LONG
                    )
                    snackBar.setAction("UNDO") {
                        todaysTaskAdapter.restoreItem(task, position)
                        binding.todaysTasks.scrollToPosition(position)
                    }

                    snackBar.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event == DISMISS_EVENT_TIMEOUT) {
                                homeViewModel.deleteTasK(task)
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

    /**
     * Create Popup Window from fab click
     */
    private fun createPopup() {

        listPopupWindow = ListPopupWindow(this)
        listPopupWindow.setAdapter(PopupAdapter(options))
        listPopupWindow.anchorView = binding.addButton
        listPopupWindow.setDropDownGravity(Gravity.END)
        listPopupWindow.width = resources.getDimension(R.dimen.popup_width).toInt()
        listPopupWindow.height = ListPopupWindow.WRAP_CONTENT
        listPopupWindow.verticalOffset =
            resources.getDimension(R.dimen.popup_vertical_offset).toInt()
        listPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.curved_bg))
        listPopupWindow.isModal = true
        listPopupWindow.setOnItemClickListener { parent, view, position, id ->
            if (position == 0) {
                startActivity(NewTaskActivity.getStartIntent(this@HomeActivity))
            } else {
                showCreateListDialog()
            }
            listPopupWindow.dismiss()
        }
        listPopupWindow.setOnDismissListener {
            AnimUtilskt.rotateFab(
                binding.addButton,
                0.0F,
                ContextCompat.getColor(this, R.color.colorWhite),
                ContextCompat.getColor(this, R.color.colorBlue)
            )
        }
    }

    private fun showCreateListDialog() {

        taskListColor = "-1"

        val view: View = layoutInflater.inflate(R.layout.bottom_sheet_colors, null)
        colorsDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        colorsDialog.setContentView(view)
        colorsDialog.setOnShowListener {
            val bottomSheet: FrameLayout =
                colorsDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        colorsDialog.findViewById<MaterialButton>(R.id.create_list_button)?.setOnClickListener {

            if (colorsDialog.findViewById<AppCompatEditText>(R.id.new_list_name)!!.text!!.isEmpty()) {
                showToast("Please enter list name.")
                return@setOnClickListener
            }

            if (taskListColor.equals("-1", true)) {
                showToast("Please select a color.")
                return@setOnClickListener
            }

            homeViewModel.createNewList(
                taskListColor,
                colorsDialog.findViewById<AppCompatEditText>(R.id.new_list_name)!!.text!!.toString()
            )
            colorsDialog.dismiss()
        }
        val listener = View.OnClickListener() {
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_1)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_2)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_3)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_4)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_5)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_6)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_7)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_8)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_9)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_10)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_11)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_12)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_13)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_14)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_15)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_16)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_17)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_18)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_19)?.setImageDrawable(null)
            colorsDialog.findViewById<AppCompatImageView>(R.id.color_20)?.setImageDrawable(null)
            (it as AppCompatImageView).setImageResource(R.drawable.ic_done_white)
            taskListColor = it.tag as String
            Timber.e(taskListColor)
        }

        colorsDialog.findViewById<AppCompatImageView>(R.id.color_1)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_2)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_3)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_4)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_5)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_6)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_7)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_8)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_9)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_10)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_11)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_12)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_13)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_14)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_15)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_16)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_17)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_18)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_19)?.setOnClickListener(listener)
        colorsDialog.findViewById<AppCompatImageView>(R.id.color_20)?.setOnClickListener(listener)
        colorsDialog.show()
    }

    /**
     * On task list click
     */
    override fun onListClick(
        taskListAndCount: TaskListAndCount,
        taskListCard: CardView,
        listName: AppCompatTextView
    ) {

        binding.addButton.hide()

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            taskListCard,
            "cardAnimation"
        )

        startActivity(
            TasksActivity.getStartIntent(
                this@HomeActivity,
                taskListAndCount.id,
                taskListAndCount.color,
                taskListAndCount.name
            ),
            options.toBundle()
        )
    }

    override fun onResume() {
        binding.addButton.show()
        super.onResume()
    }
}
