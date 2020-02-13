package com.vicky7230.tasker.data

import com.vicky7230.tasker.data.db.DbHelper
import com.vicky7230.tasker.data.network.ApiHelper
import com.vicky7230.tasker.data.prefs.PreferencesHelper


interface DataManager : ApiHelper, DbHelper, PreferencesHelper {
}