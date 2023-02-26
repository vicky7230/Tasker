package com.vicky7230.tasker.ui.home

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.vicky7230.tasker.Config.GOOGLE_PLAY_STORE_LINK
import com.vicky7230.tasker.Config.HOME_SCENE_TRANSITION_ANIMATION_SHARED_ELEMENT_NAME
import com.vicky7230.tasker.Config.MARKET_LINT
import com.vicky7230.tasker.Config.PRIVACY_POLICY_LINK
import com.vicky7230.tasker.R
import com.vicky7230.tasker.data.db.joinReturnTypes.TaskListAndCount
import com.vicky7230.tasker.data.network.Resource
import com.vicky7230.tasker.databinding.ActivityHomeBinding
import com.vicky7230.tasker.ui.base.BaseActivity
import com.vicky7230.tasker.ui.newTask.NewTaskActivity
import com.vicky7230.tasker.ui.taskList.TasksActivity
import com.vicky7230.tasker.ui.finishedDeleted.FinishedDeletedTasksActivity
import com.vicky7230.tasker.utils.AnimUtilskt
import com.vicky7230.tasker.utils.MessageManager
import com.vicky7230.tasker.utils.openLink
import dagger.android.AndroidInjection
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class HomeActivity : BaseActivity<ActivityHomeBinding>(), TaskListsAdapter.Callback {

    @Inject
    lateinit var taskListsAdapter: TaskListsAdapter
    @Inject
    lateinit var todayTaskAdapter: TodayTaskAdapter
    private lateinit var listPopupWindow: ListPopupWindow
    private lateinit var colorsDialog: BottomSheetDialog
    private lateinit var messageManager: MessageManager
    private lateinit var colorIdList: List<Int>
    private var options: ArrayList<String> = arrayListOf()
    private var taskListColor: String = String()

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
    }

    override fun onBindingCreated() {
        AndroidInjection.inject(this)
        super.onBindingCreated()
        taskListsAdapter.setCallback(this)
        init()
    }

    private fun init() {
        options.add( getString(R.string.task))
        options.add(this.getString(R.string.list))
        taskListColor = this.getString(R.string.not_initialized_yet)
        messageManager = MessageManager(activity = this)
        colorIdList = listOf(
            R.id.color_1,
            R.id.color_2,
            R.id.color_3,
            R.id.color_4,
            R.id.color_5,
            R.id.color_6,
            R.id.color_7,
            R.id.color_8,
            R.id.color_9,
            R.id.color_10,
            R.id.color_11,
            R.id.color_12,
            R.id.color_13,
            R.id.color_14,
            R.id.color_15,
            R.id.color_16,
            R.id.color_17,
            R.id.color_18,
            R.id.color_19,
            R.id.color_20,
        )
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
            val dialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
            dialog.setContentView(view)
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
                openLink(link = PRIVACY_POLICY_LINK)
            }
            view.findViewById<AppCompatTextView>(R.id.rate).setOnClickListener {
                val appPackageName = packageName
                try {
                    openLink(link = MARKET_LINT.format(appPackageName))
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    openLink(link = GOOGLE_PLAY_STORE_LINK.format(appPackageName))
                }
            }
            dialog.show()
        }
        setUpTaskListsRecyclerView()
        setUpTodayTasksRecyclerView()
        homeViewModel.getData(getTodayDateStart(), getTodayDateEnd())
    }

    private fun setUpTaskListsRecyclerView() {
        binding.taskLists.layoutManager = LinearLayoutManager(this)
        binding.taskLists.isNestedScrollingEnabled = false
        binding.taskLists.adapter = taskListsAdapter
    }

    private fun setUpTodayTasksRecyclerView() {
        binding.todayTasks.layoutManager = LinearLayoutManager(this)
        binding.todayTasks.isNestedScrollingEnabled = false
        binding.todayTasks.adapter = todayTaskAdapter
    }

    private fun getTodayDateStart(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        Timber.d(calendar.time.time.toString())
        return calendar.time.time
    }

    private fun getTodayDateEnd(): Long {
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
        listPopupWindow.setOnItemClickListener { _, _, position, _ ->
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
        taskListColor = this.getString(R.string.not_initialized_yet)
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
                messageManager.showToast(this.getString(R.string.enter_last_name))
                return@setOnClickListener
            }

            if (taskListColor.equals(this.getString(R.string.not_initialized_yet), true)) {
                messageManager.showToast(this.getString(R.string.select_a_color))
                return@setOnClickListener
            }

            homeViewModel.createNewList(
                taskListColor,
                colorsDialog.findViewById<AppCompatEditText>(R.id.new_list_name)!!.text!!.toString()
            )
            colorsDialog.dismiss()
        }
        val listener = View.OnClickListener {
            colorIdList.forEach{ viewId ->
                colorsDialog.findViewById<AppCompatImageView>(viewId)?.setOnClickListener(null)
            }
            (it as AppCompatImageView).setImageResource(R.drawable.ic_done_white)
            taskListColor = it.tag as String
            Timber.e(taskListColor)
        }
        colorIdList.forEach{ viewId ->
            colorsDialog.findViewById<AppCompatImageView>(viewId)?.setOnClickListener(listener)
        }
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
            HOME_SCENE_TRANSITION_ANIMATION_SHARED_ELEMENT_NAME
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

    override fun registerObservers() = with(homeViewModel) {
        taskAndTaskList {
            when (it) {
                is Resource.Loading -> messageManager.showLoading()
                is Resource.Error -> {
                    messageManager.hideLoading()
                    messageManager.showError(it.exception.localizedMessage)
                }
                is Resource.Success -> {
                    messageManager.hideLoading()
                    todayTaskAdapter.updateItems(it.data)
                }
            }
        }
        taskListAndCount {
            when (it) {
                is Resource.Loading -> messageManager.showLoading()
                is Resource.Error -> {
                    messageManager.hideLoading()
                    messageManager.showError(it.exception.localizedMessage)
                }
                is Resource.Success -> {
                    messageManager.hideLoading()
                    taskListsAdapter.updateItems(it.data)
                }
            }
        }
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }
}
