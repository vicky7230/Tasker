package com.vicky7230.tasker.ui.home

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.AdapterView
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.vicky7230.tasker.R
import com.vicky7230.tasker.ui._0base.BaseActivity
import com.vicky7230.tasker.ui.newTask.NewTaskActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import javax.inject.Inject


class HomeActivity : BaseActivity(), AdapterView.OnItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var listPopupWindow: ListPopupWindow

    var products = arrayListOf("Task", "List")

    companion object {
        fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        init()
    }

    private fun init() {

        createPopup()

        add_task_button.setOnClickListener { rotateFab() }

        homeViewModel.loading.observe(this, Observer {
            if (it)
                showLoading()
            else
                hideLoading()
        })

        homeViewModel.error.observe(this, Observer {
            showError(it)
        })

        homeViewModel.getAllList()
    }

    private fun rotateFab() {
        add_task_button.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorBlue))

        add_task_button.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorWhite))

        ViewCompat.animate(add_task_button)
            .rotation(45.0F)
            .withLayer()
            .setDuration(200L)
            .setInterpolator(LinearInterpolator())
            .start()

        listPopupWindow.show()
    }

    private fun rotateBack() {
        add_task_button.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorWhite))

        add_task_button.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorBlue))

        ViewCompat.animate(add_task_button)
            .rotation(0.0F)
            .withLayer()
            .setDuration(200L)
            .setInterpolator(LinearInterpolator())
            .start()
    }

    private fun createPopup(): ListPopupWindow {

        listPopupWindow = ListPopupWindow(this)
        listPopupWindow.setAdapter(PopupAdapter(products))
        listPopupWindow.anchorView = add_task_button
        listPopupWindow.setDropDownGravity(Gravity.RIGHT)
        listPopupWindow.width = resources.getDimension(R.dimen.popup_width).toInt()
        listPopupWindow.height = ListPopupWindow.WRAP_CONTENT
        listPopupWindow.verticalOffset =
            resources.getDimension(R.dimen.popup_vertical_offset).toInt()
        listPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.curved_bg))
        listPopupWindow.isModal = true
        listPopupWindow.setOnItemClickListener(this)
        listPopupWindow.setOnDismissListener { rotateBack() }
        return listPopupWindow
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position == 0) {
            startActivity(NewTaskActivity.getStartIntent(this))
        }

        listPopupWindow.dismiss()
    }
}
