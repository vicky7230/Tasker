package com.vicky7230.tasker.ui._7finishedDeleted

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vicky7230.tasker.databinding.ActivityFinishedDeletedTasksBinding
import com.vicky7230.tasker.ui.base.BaseActivity
import com.vicky7230.tasker.ui.base.BaseViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class FinishedDeletedTasksActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var finishedTasksAdapter: DeletedFinishedTasksAdapter

    private lateinit var finishedDeletedTasksViewModel: FinishedDeletedTasksViewModel

    private lateinit var binding: ActivityFinishedDeletedTasksBinding

    companion object {

        const val FLAG_DELETED = "deleted"
        const val FLAG_FINISHED = "finished"
        const val FLAG = "flag"

        fun getStartIntent(context: Context, flag: String): Intent {
            val intent = Intent(context, FinishedDeletedTasksActivity::class.java)
            intent.putExtra(FLAG, flag)
            return intent
        }
    }

    override fun getViewModel(): BaseViewModel {
        finishedDeletedTasksViewModel =
            ViewModelProvider(this, viewModelFactory)[FinishedDeletedTasksViewModel::class.java]
        return finishedDeletedTasksViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityFinishedDeletedTasksBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()

    }

    private fun init() {

        binding.deletedFinishedTasks.layoutManager = LinearLayoutManager(this)
        binding.deletedFinishedTasks.adapter = finishedTasksAdapter

        finishedDeletedTasksViewModel.tasks.observe(this, Observer {
            finishedTasksAdapter.updateItems(it)
        })

        if (intent != null && intent.getStringExtra(FLAG) != null) {
            val flag = intent.getStringExtra(FLAG)
            if (flag == FLAG_DELETED)
                finishedDeletedTasksViewModel.getDeletedTasks()
            if (flag == FLAG_FINISHED)
                finishedDeletedTasksViewModel.getFinishedTasks()
        }
    }
}
