package com.vicky7230.tasker.data.prefs

/**
 * Created by vicky on 1/3/18.
 */

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.vicky7230.tasker.di.ApplicationContext
import javax.inject.Inject


class AppPreferencesHelper @Inject constructor(@ApplicationContext context: Context) :
    PreferencesHelper {

    private val PREF_KEY_ARE_LISTS_FETCHED = "PREF_KEY_ARE_LISTS_FETCHED"
    private val PREF_KEY_ARE_TASKS_FETCHED = "PREF_KEY_ARE_TASKS_FETCHED"
    private val PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"
    private val PREF_KEY_USER_EMAIL = "PREF_KEY_USER_EMAIL"
    private val PREF_KEY_USER_ID = "PREF_KEY_USER_ID"
    private val PREF_KEY_USER_LOGGED_IN = "PREF_KEY_USER_LOGGED_IN"

    private val sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    override fun areListsFetched(): Boolean {
        return sharedPreferences.getBoolean(PREF_KEY_ARE_LISTS_FETCHED, false)
    }

    override fun setListsFetched() {
        sharedPreferences.edit().putBoolean(PREF_KEY_ARE_LISTS_FETCHED, true).apply()
    }

    override fun areTasksFetched(): Boolean {
        return sharedPreferences.getBoolean(PREF_KEY_ARE_TASKS_FETCHED, false)
    }

    override fun setTasksFetched() {
        sharedPreferences.edit().putBoolean(PREF_KEY_ARE_TASKS_FETCHED, true).apply()
    }

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

    override fun setUserLoggedOut() {
        sharedPreferences.edit {
            remove(PREF_KEY_ACCESS_TOKEN)
            remove(PREF_KEY_USER_EMAIL)
            remove(PREF_KEY_USER_ID)
            remove(PREF_KEY_USER_LOGGED_IN)
        }
    }
}