package com.example.utspabkelompok2.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Menyimpan sesi login di SharedPreferences.
 * Data yang disimpan hanya id & username, bukan password.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSession(userId: Long, username: String) {
        prefs.edit()
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, NO_USER)

    fun getUsername(): String = prefs.getString(KEY_USERNAME, "").orEmpty()

    fun logout() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "utspab_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_LOGGED_IN = "logged_in"
        const val NO_USER = -1L
    }
}
