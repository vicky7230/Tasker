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

    private val PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"

    private val PREF_KEY_USER_EMAIL = "PREF_KEY_USER_EMAIL"

    private val PREF_KEY_USER_ID = "PREF_KEY_USER_ID"

    private val PREF_KEY_USER_LOGGED_IN = "PREF_KEY_USER_LOGGED_IN"

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    override fun getAccessToken(): String? {
        return sharedPreferences.getString(PREF_KEY_ACCESS_TOKEN, null)
    }

    override fun setAccessToken(accessToken: String) {
        sharedPreferences.edit().putString(PREF_KEY_ACCESS_TOKEN, accessToken).apply()
    }

    override fun getUserEmail(): String? {
        return sharedPreferences.getString(PREF_KEY_USER_EMAIL, null)
    }

    override fun setUserEmail(email: String) {
        sharedPreferences.edit().putString(PREF_KEY_USER_EMAIL, email).apply()
    }

    override fun getUserId(): String? {
        return sharedPreferences.getString(PREF_KEY_USER_ID, null)
    }

    override fun setUserId(userId: String) {
        sharedPreferences.edit().putString(PREF_KEY_USER_ID, userId).apply()
    }

    override fun getUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(PREF_KEY_USER_LOGGED_IN, false)
    }

    override fun setUserLoggedIn() {
        sharedPreferences.edit().putBoolean(PREF_KEY_USER_LOGGED_IN, true).apply()
    }
}