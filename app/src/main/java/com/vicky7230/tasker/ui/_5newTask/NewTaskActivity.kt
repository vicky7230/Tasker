package com.vicky7230.tasker.ui._5newTask

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.utils.AnimUtils
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_new_task.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
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

        var timeViewContainerHeight = 0
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

        var calendarViewContainerHeight = 0
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

        //KeyboardUtils.showSoftInput(task_edit_text, this)

        val calendarInstance = Calendar.getInstance()

        val dateFormatter = SimpleDateFormat("d LLL YYYY", Locale.ENGLISH)
        val formattedDate = dateFormatter.format(calendarInstance.time)
        task_date.text = formattedDate

        val timeFormatter = SimpleDateFormat("h:m a", Locale.ENGLISH)
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
            UIUtil.hideKeyboard(this)
            view.isSelected = true
            calendar_view_container.visibility = View.VISIBLE
            time_view_container.visibility = View.GONE
            AnimUtils.slideView(calendar_view_container, 0, calendarViewContainerHeight)
        }

        date_cancel_button.setOnClickListener {
            //calendar_view_container.visibility = View.GONE
            AnimUtils.slideView(calendar_view_container, calendarViewContainerHeight, 0)
        }

        date_done_button.setOnClickListener {
            //calendar_view_container.visibility = View.GONE
            AnimUtils.slideView(calendar_view_container, calendarViewContainerHeight, 0)
        }

        time_view.setOnTimeChangedListener { view, hourOfDay, minute ->
            calendarInstance.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendarInstance.set(Calendar.MINUTE, minute)

            task_time.text = timeFormatter.format(calendarInstance.time)
        }

        time_button.setOnClickListener { view: View ->
            UIUtil.hideKeyboard(this)
            view.isSelected = true
            time_view_container.visibility = View.VISIBLE
            calendar_view_container.visibility = View.GONE
            Timber.d("Height of time widget : ${time_view_container.height}")
            AnimUtils.slideView(time_view_container, 0, timeViewContainerHeight)
        }

        time_cancel_button.setOnClickListener {
            //time_view_container.visibility = View.GONE
            AnimUtils.slideView(time_view_container, timeViewContainerHeight, 0)
        }

        time_done_button.setOnClickListener {
            //time_view_container.visibility = View.GONE
            AnimUtils.slideView(time_view_container, timeViewContainerHeight, 0)
        }
    }
}
