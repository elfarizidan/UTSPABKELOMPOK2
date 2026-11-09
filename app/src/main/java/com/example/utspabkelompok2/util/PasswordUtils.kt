package com.example.utspabkelompok2.util

import java.security.MessageDigest

/**
 * Hash password dengan SHA-256 sebelum disimpan ke database (modul 196).
 * Konstanta & variabel lokal diberi suffix 196.
 */
object PasswordUtils {

    private const val ALGORITHM_196 = "SHA-256"

    fun hash(password: String): String {
        val digest_196 = MessageDigest.getInstance(ALGORITHM_196)
        val bytes_196 = digest_196.digest(password.toByteArray(Charsets.UTF_8))
        return bytes_196.joinToString("") { "%02x".format(it) }
    }

    fun verify(password: String, storedHash: String): Boolean =
        hash(password) == storedHash
}
