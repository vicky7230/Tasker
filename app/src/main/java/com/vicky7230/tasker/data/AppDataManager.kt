package com.vicky7230.tasker.data

import com.vicky7230.tasker.data.db.AppDbHelper
import com.vicky7230.tasker.data.db.entities.Task
import com.vicky7230.tasker.data.network.AppApiHelper
import com.vicky7230.tasker.data.prefs.AppPreferencesHelper
import javax.inject.Inject

class AppDataManager @Inject
constructor(
    private val appApiHelper: AppApiHelper,
    private val appDbHelper: AppDbHelper,
    private val appPreferencesHelper: AppPreferencesHelper
) : DataManager {

    override suspend fun insertTodo(task: Task) {
        appDbHelper.insertTodo(task)
    }


}