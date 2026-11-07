package com.example.utspabkelompok2.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Menyimpan sesi login di SharedPreferences (modul 196).
 * Nama file preference, key, dan variabel diberi suffix 196.
 * Data yang disimpan hanya id & username, bukan password.
 */
class SessionManager(context: Context) {

    private val prefs_196: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME_196, Context.MODE_PRIVATE)

    fun saveSession(userId: Long, username: String) {
        prefs_196.edit()
            .putLong(KEY_USER_ID_196, userId)
            .putString(KEY_USERNAME_196, username)
            .putBoolean(KEY_LOGGED_IN_196, true)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs_196.getBoolean(KEY_LOGGED_IN_196, false)

    fun getUserId(): Long = prefs_196.getLong(KEY_USER_ID_196, NO_USER_196)

    fun getUsername(): String = prefs_196.getString(KEY_USERNAME_196, "").orEmpty()

    fun logout() {
        prefs_196.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME_196 = "session_196"
        private const val KEY_USER_ID_196 = "user_id_196"
        private const val KEY_USERNAME_196 = "username_196"
        private const val KEY_LOGGED_IN_196 = "logged_in_196"
        const val NO_USER_196 = -1L
    }
}
