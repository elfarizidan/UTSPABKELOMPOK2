package com.example.utspabkelompok2.util

import java.security.MessageDigest

/**
 * Hash password dengan SHA-256 sebelum disimpan ke database.
 */
object PasswordUtils {

    fun hash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verify(password: String, storedHash: String): Boolean =
        hash(password) == storedHash
}
