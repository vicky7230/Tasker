package com.vicky7230.tasker.ui._6taskList

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._0base.BaseActivity
import kotlinx.android.synthetic.main.activity_task_list.*
import javax.inject.Inject

class TaskListActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    //lateinit var verifyOtpViewModel: VerifyOtpViewModel

    companion object {

        const val EXTRAS_LIST_SLACK = "list_slack"
        const val EXTRAS_LIST_COLOR = "list_color"
        const val EXTRAS_LIST_NAME = "list_name"

        fun getStartIntent(
            context: Context,
            listSlack: String,
            listColor: String,
            listName: String
        ): Intent {
            val intent = Intent(context, TaskListActivity::class.java)
            intent.putExtra(EXTRAS_LIST_SLACK, listSlack)
            intent.putExtra(EXTRAS_LIST_COLOR, listColor)
            intent.putExtra(EXTRAS_LIST_NAME, listName)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        if (intent != null
            && intent.getStringExtra(EXTRAS_LIST_COLOR) != null
            && intent.getStringExtra(EXTRAS_LIST_NAME) != null
        ) {
            val listColor = intent.getStringExtra(EXTRAS_LIST_COLOR)
            task_list_card.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(listColor))
            val listName = intent.getStringExtra(EXTRAS_LIST_NAME)
            list_name.text = listName
        }
    }

    override fun onBackPressed() {
        list_name.visibility = View.GONE
        edit_list_name.visibility = View.GONE
        super.onBackPressed()
    }
}
