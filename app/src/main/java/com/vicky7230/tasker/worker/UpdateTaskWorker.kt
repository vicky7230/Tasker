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

class UpdateTaskWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val dataManager: DataManager
) : CoroutineWorker(appContext, params) {

    companion object {
        const val TASK_LONG_ID = "taskLongId"
    }

    override suspend fun doWork(): Result {
        var success = false
        val taskLongId = inputData.getLong(TASK_LONG_ID, -1L)
        if (taskLongId != -1L) {

            supervisorScope {

                val getTaskFromDbJob = async {
                    dataManager.getTask(taskLongId)
                }

                val task = getTaskFromDbJob.await()

                if (task.taskSlack != (-1).toString()) {

                    if (dataManager.getAccessToken() != null) {
                        val updateTaskJob = async {
                            dataManager.updateTask(
                                TaskData(
                                    dataManager.getUserId(),
                                    dataManager.getAccessToken(),
                                    task
                                )
                            )
                        }
                        try {
                            val response = updateTaskJob.await()
                            if (response.isSuccessful) {
                                val jsonObject = response.body()!!.asJsonObject
                                if (jsonObject["success"].asBoolean && jsonObject["updated"].asBoolean) {
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
                } else {
                    success = false
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