package com.vicky7230.tasker.ui.finishedDeleted

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vicky7230.tasker.databinding.ActivityFinishedDeletedTasksBinding
import com.vicky7230.tasker.ui.base.BaseActivity
import dagger.android.AndroidInjection
import javax.inject.Inject

class FinishedDeletedTasksActivity : BaseActivity<ActivityFinishedDeletedTasksBinding>() {

    @Inject
    lateinit var finishedTasksAdapter: DeletedFinishedTasksAdapter
    private val finishedDeletedTasksViewModel: FinishedDeletedTasksViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[FinishedDeletedTasksViewModel::class.java]
    }

    override fun onBindingCreated() {
        AndroidInjection.inject(this)
        super.onBindingCreated()
        init()
    }

    private fun init() {
        binding.deletedFinishedTasks.layoutManager = LinearLayoutManager(this)
        binding.deletedFinishedTasks.adapter = finishedTasksAdapter
        if (intent != null && intent.getStringExtra(FLAG) != null) {
            val flag = intent.getStringExtra(FLAG)
            if (flag == FLAG_DELETED)
                finishedDeletedTasksViewModel.getDeletedTasks()
            if (flag == FLAG_FINISHED)
                finishedDeletedTasksViewModel.getFinishedTasks()
        }
    }

    override fun registerObservers() = with(finishedDeletedTasksViewModel) {
        tasks {
            finishedTasksAdapter.updateItems(it)
        }
    }

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
}
