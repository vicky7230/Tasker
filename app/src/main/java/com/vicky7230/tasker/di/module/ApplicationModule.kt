package com.vicky7230.tasker.di.module

import android.app.Application
import android.content.Context
import com.vicky7230.tasker.di.ApplicationContext
import com.vicky7230.tasker.di.BaseUrl
import com.vicky7230.tasker.TaskerApplication
import com.vicky7230.tasker.data.AppDataManager
import com.vicky7230.tasker.data.Config
import com.vicky7230.tasker.data.DataManager
import com.vicky7230.tasker.data.db.AppDbHelper
import com.vicky7230.tasker.data.db.DbHelper
import com.vicky7230.tasker.data.db.AppDatabase
import com.vicky7230.tasker.data.network.ApiHelper
import com.vicky7230.tasker.data.network.AppApiHelper
import com.vicky7230.tasker.data.prefs.AppPreferencesHelper
import com.vicky7230.tasker.data.prefs.PreferencesHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by vicky on 31/12/17.
 */
@Module
class ApplicationModule {

    @Provides
    @ApplicationContext
    internal fun provideContext(taskerApplication: TaskerApplication): Context {
        return taskerApplication.applicationContext
    }

    @Provides
    internal fun provideApplication(taskerApplication: TaskerApplication): Application {
        return taskerApplication
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)!!
    }

    @Provides
    @BaseUrl
    internal fun provideBaseUrl(): String {
        return Config.BASE_URL
    }

    @Provides
    @Singleton
    internal fun provideDataManager(appDataManager: AppDataManager): DataManager {
        return appDataManager
    }

    @Provides
    @Singleton
    internal fun provideDbHelper(appDbHelper: AppDbHelper): DbHelper {
        return appDbHelper
    }

    @Provides
    @Singleton
    internal fun provideApiHelper(appApiHelper: AppApiHelper): ApiHelper {
        return appApiHelper
    }

    @Provides
    @Singleton
    internal fun providePreferencesHelper(appPreferencesHelper: AppPreferencesHelper): PreferencesHelper {
        return appPreferencesHelper
    }
}