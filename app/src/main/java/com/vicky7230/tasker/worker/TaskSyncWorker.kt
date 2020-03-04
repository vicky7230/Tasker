package com.vicky7230.tasker.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.di.ChildWorkerFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import timber.log.Timber

class TaskSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val dataManager: DataManager
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        var success = false
        coroutineScope {
            val job = async {
                dataManager.syncSingleTask(
                    dataManager.getUserId(),
                    dataManager.getAccessToken()
                )
            }

            try {
                val response = job.await()
                if (response.isSuccessful) {
                    val jsonObject = response.body()!!.asJsonObject
                    success = true
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    Timber.d("Job was Cancelled....")
                }
                //Log exception
                Timber.e(e)
            }
        }

        return if (success)
            Result.success()
        else
            Result.retry()
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory
}