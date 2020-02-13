package com.vicky7230.tasker.ui.newTask

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui.base.BaseActivity
import com.vicky7230.tasker.utils.KeyboardUtils
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_new_task.*

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
        KeyboardUtils.showSoftInput(task_edit_text, this)
    }
}
