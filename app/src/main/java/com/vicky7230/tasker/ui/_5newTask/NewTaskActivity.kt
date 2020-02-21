package com.vicky7230.tasker.ui._5newTask

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.applikeysolutions.cosmocalendar.model.Day
import com.applikeysolutions.cosmocalendar.model.Month
import com.applikeysolutions.cosmocalendar.utils.CalendarUtils
import com.applikeysolutions.cosmocalendar.utils.SelectionType
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._0base.BaseActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_new_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
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

        val disabledDaysSet: MutableSet<Long> = HashSet()

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
        }


        //calendar_view.

        //calendar_view.selectionManager.

        //calendar_view.disabledDaysCriteria = DisabledDaysCriteria(1, 31, DisabledDaysCriteriaType.DAYS_OF_MONTH)

        //calendar_view.selectionManager.toggleDay(Day(calendarInstance))

        //PreviousMonthCriteria


        calendar_button.setOnClickListener { view: View ->
            if (view.isSelected) {
                view.isSelected = false
                calendar_view.visibility = View.GONE
            } else {
                view.isSelected = true
                calendar_view.visibility = View.VISIBLE
            }
        }
    }
}
