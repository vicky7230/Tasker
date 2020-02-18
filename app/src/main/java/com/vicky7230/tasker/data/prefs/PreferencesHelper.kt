package com.vicky7230.tasker.data.prefs

/**
 * Created by vicky on 1/3/18.
 */
interface PreferencesHelper {

    fun getAccessToken(): String?

    fun setAccessToken(accessToken: String)

    fun getUserEmail(): String?

    fun setUserEmail(email: String)

    fun getUserId(): String?

    fun setUserId(userId: String)

    fun getUserLoggedIn(): Boolean

    fun setUserLoggedIn()
}