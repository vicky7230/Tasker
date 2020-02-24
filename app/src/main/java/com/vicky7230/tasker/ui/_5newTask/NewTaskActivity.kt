package com.vicky7230.tasker.ui._5newTask

import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.applikeysolutions.cosmocalendar.model.Day
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.utils.AnimUtils
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_new_task.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.text.SimpleDateFormat
import java.util.*


class NewTaskActivity : BaseActivity() {

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, NewTaskActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_task)

        init()
    }

    private fun init() {

        KeyboardVisibilityEvent.setEventListener(
            this,
            this,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    if (isOpen) {
                        calendar_view_container.visibility = View.GONE
                        time_view_container.visibility = View.GONE
                    }
                }
            })

        //KeyboardUtils.showSoftInput(task_edit_text, this)

        val calendarInstance = Calendar.getInstance()
        //val currentMonth = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendarInstance.time)
        val currentDay = Day(calendarInstance)
        //calendarInstance.set(2020, 1, 23)

        calendar_view.selectionType = SelectionType.SINGLE
        calendar_view.calendarOrientation = LinearLayoutManager.HORIZONTAL
        calendar_view.currentDayIconRes = R.drawable.calendar_dot
        calendar_view.currentDaySelectedIconRes = R.drawable.calendar_dot_selected
        calendar_view.currentDayTextColor = Color.parseColor("#006CFF")// NOT WORKING
        calendar_view.weekendDayTextColor = Color.parseColor("#252A31")
        calendar_view.selectedDayBackgroundColor = Color.parseColor("#006CFF")

        calendar_view.isShowDaysOfWeek = true
        calendar_view.isShowDaysOfWeekTitle = false
        calendar_view.nextMonthIconRes = R.drawable.ic_chevron_right
        calendar_view.previousMonthIconRes = R.drawable.ic_chevron_left

        calendar_view.selectionManager.toggleDay(currentDay)

        /*val disabledDaysSet: MutableSet<Long> = HashSet()

        var job: Job? = null
        calendar_view.setOnMonthChangeListener { month: Month ->
            Timber.d(month.monthName)
            job?.cancel()
            job = lifecycleScope.launch {
                for (day in month.days) {
                    if (day.calendar.time.before(currentDay.calendar.time)) {
                        if (!day.isDisabled) {
                            disabledDaysSet.add(day.calendar.timeInMillis)
                        }
                    }
                }
                //withContext(Dispatchers.Main) {
                calendar_view.disabledDays = disabledDaysSet
                //}
            }
        }*/

        calendar_button.setOnClickListener { view: View ->
            UIUtil.hideKeyboard(this)
            calendar_view_container.visibility = View.VISIBLE
            //AnimUtils.slideView(calendar_view_container, 0, calendar_view_container.layoutParams.height)
            //view.isSelected = true

            lifecycleScope.launch {
                delay(50)
                view.isSelected = true
                calendar_view_container.visibility = View.VISIBLE
                time_view_container.visibility = View.GONE

                calendar_view_container.layoutParams.height = 0
                AnimUtils.slideView(
                    calendar_view_container,
                    0,
                    calendar_view_container.layoutParams.height
                )

            }
        }

        date_cancel_button.setOnClickListener {
            calendar_view_container.visibility = View.GONE
        }

        date_done_button.setOnClickListener {
            if (calendar_view.selectedDates.size > 0) {

                if (calendar_view.selectedDates[0][Calendar.DAY_OF_MONTH] < calendarInstance[Calendar.DAY_OF_MONTH]) {
                    showError("Selected Date should be today or ahead.")
                    return@setOnClickListener
                }

                val formatter = SimpleDateFormat("d LLL YYYY", Locale.ENGLISH)
                val formattedDate = formatter.format(calendar_view.selectedDates[0].time)
                task_date.text = formattedDate

                calendar_view_container.visibility = View.GONE
            } else {
                showError("Please select a date.")
            }
        }

        time_view.setIs24HourView(true)

        time_button.setOnClickListener { view: View ->
            UIUtil.hideKeyboard(this)
            time_view_container.visibility = View.VISIBLE
            val height = time_view_container.layoutParams.height
            time_view_container.layoutParams.height = 0
            AnimUtils.slideView(calendar_view_container, 0, height)
            /*lifecycleScope.launch {
                delay(50)
                view.isSelected = true
                time_view_container.visibility = View.VISIBLE
                calendar_view_container.visibility = View.GONE

                AnimUtils.slideView(calendar_view_container, 0, time_view_container.layoutParams.height)
            }*/
        }

    }
}
