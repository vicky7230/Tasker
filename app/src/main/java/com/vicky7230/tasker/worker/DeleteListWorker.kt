package com.vicky7230.tasker.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.di.ChildWorkerFactory
import com.vicky7230.tasker.events.TokenExpireEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class DeleteListWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val dataManager: DataManager
) : CoroutineWorker(appContext, params) {

    companion object {
        const val TASK_LIST_LONG_ID = "taskListLongId"
    }

    override suspend fun doWork(): Result {
        var success = false
        val taskLongId = inputData.getLong(TASK_LIST_LONG_ID, -1L)
        if (taskLongId != -1L) {
            supervisorScope {

                val getTaskFromDbJob = async {
                    dataManager.getTask(taskLongId)
                }

                val task = getTaskFromDbJob.await()

                if (dataManager.getAccessToken() != null) {

                    val createTaskJob = async {
                        dataManager.createTask(
                            TaskData(
                                dataManager.getUserId(),
                                dataManager.getAccessToken(),
                                task
                            )
                        )
                    }

                    try {
                        val response = createTaskJob.await()
                        if (response.isSuccessful) {
                            val jsonObject = response.body()!!.asJsonObject
                            if (jsonObject["success"].asBoolean && jsonObject["created"].asBoolean) {
                                task.taskSlack = jsonObject["task_slack"].asString
                                val updateTaskSlackJob = async {
                                    dataManager.updateTask(task)
                                }
                                val rowsUpdated = updateTaskSlackJob.await()
                                if (rowsUpdated > 0)
                                    success = true
                            } else if (!jsonObject["success"].asBoolean) {
                                EventBus.getDefault().post(TokenExpireEvent())
                            }
                        }
                    } catch (e: Exception) {
                        if (e is CancellationException) {
                            Timber.d("Job was Cancelled....")
                        }
                        //Log exception
                        Timber.e("Handling Exception......")
                        Timber.e(e)
                    }
                }
            }
        } else {
            success = true
        }

        return if (success)
            Result.success()
        else
            Result.retry()
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory
}