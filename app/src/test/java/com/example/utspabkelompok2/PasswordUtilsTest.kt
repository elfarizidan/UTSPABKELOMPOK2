package com.example.utspabkelompok2

import com.example.utspabkelompok2.util.PasswordUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordUtilsTest {

    @Test
    fun hash_isDeterministic() {
        assertEquals(PasswordUtils.hash("rahasia123"), PasswordUtils.hash("rahasia123"))
    }

    @Test
    fun hash_isNotPlainText() {
        assertFalse(PasswordUtils.hash("rahasia123").contains("rahasia123"))
    }

    @Test
    fun verify_matchesOnlyCorrectPassword() {
        val stored = PasswordUtils.hash("rahasia123")
        assertTrue(PasswordUtils.verify("rahasia123", stored))
        assertFalse(PasswordUtils.verify("salah", stored))
    }
}
