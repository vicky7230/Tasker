package com.vicky7230.tasker.data.prefs

/**
 * Created by vicky on 1/3/18.
 */
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

import com.vicky7230.tasker.di.ApplicationContext
import javax.inject.Inject

class AppPreferencesHelper @Inject constructor(@ApplicationContext context: Context) :
    PreferencesHelper {

    private val NULL_TYPE = "null"
    private val USER_GENRES = "USER_GENRES"
    private val BASE_IMAGE_URL = "BASE_IMAGE_URL"
    private val ARE_GENRES_SELECTED = "ARE_GENRES_SELECTED"

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

}