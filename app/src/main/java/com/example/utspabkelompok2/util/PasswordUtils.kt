package com.example.utspabkelompok2.util

import java.security.MessageDigest

/**
 * Hash password dengan SHA-256 sebelum disimpan ke database (modul 003).
 * Konstanta & variabel lokal diberi suffix 003.
 */
object PasswordUtils {

    private const val ALGORITHM_003 = "SHA-256"

    fun hash(password: String): String {
        val digest_003 = MessageDigest.getInstance(ALGORITHM_003)
        val bytes_003 = digest_003.digest(password.toByteArray(Charsets.UTF_8))
        return bytes_003.joinToString("") { "%02x".format(it) }
    }

    fun verify(password: String, storedHash: String): Boolean =
        hash(password) == storedHash
}
