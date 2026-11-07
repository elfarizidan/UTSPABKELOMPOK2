package com.example.utspabkelompok2.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.utspabkelompok2.R
import com.example.utspabkelompok2.data.AppDatabase
import com.example.utspabkelompok2.data.entity.User
import com.example.utspabkelompok2.util.PasswordUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

/**
 * Modul 003 — Registrasi, keamanan password, dan logout.
 * Variabel milik modul ini memakai suffix 003.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername_003: TextInputEditText
    private lateinit var etPassword_003: TextInputEditText
    private lateinit var etPasswordConfirm_003: TextInputEditText
    private lateinit var btnRegister_003: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val userDao_003 = AppDatabase.getInstance(this).userDao()

        etUsername_003 = findViewById(R.id.etUsername_003)
        etPassword_003 = findViewById(R.id.etPassword_003)
        etPasswordConfirm_003 = findViewById(R.id.etPasswordConfirm_003)
        btnRegister_003 = findViewById(R.id.btnRegister_003)

        btnRegister_003.setOnClickListener {
            val username = etUsername_003.text?.toString()?.trim().orEmpty()
            val password = etPassword_003.text?.toString().orEmpty()
            val confirm = etPasswordConfirm_003.text?.toString().orEmpty()

            if (!validate(username, password, confirm)) return@setOnClickListener

            setLoading(true)
            lifecycleScope.launch {
                try {
                    if (userDao_003.countByUsername(username) > 0) {
                        etUsername_003.error = getString(R.string.error_username_taken)
                        return@launch
                    }

                    userDao_003.insert(
                        User(
                            username = username,
                            password = PasswordUtils.hash(password)
                        )
                    )

                    Toast.makeText(
                        this@RegisterActivity,
                        R.string.register_success,
                        Toast.LENGTH_SHORT
                    ).show()

                    // Kembali ke halaman login dengan membawa username.
                    val result = Intent().putExtra(EXTRA_USERNAME_003, username)
                    setResult(RESULT_OK, result)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@RegisterActivity,
                        R.string.error_unknown,
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    setLoading(false)
                }
            }
        }

        findViewById<TextView>(R.id.tvGoLogin_003).setOnClickListener { finish() }
    }

    private fun validate(username: String, password: String, confirm: String): Boolean {
        if (username.isEmpty()) {
            etUsername_003.error = getString(R.string.error_username_empty)
            return false
        }
        if (username.length < 3) {
            etUsername_003.error = "Username minimal 3 karakter"
            return false
        }
        if (password.isEmpty()) {
            etPassword_003.error = getString(R.string.error_password_empty)
            return false
        }
        if (password.length < 6) {
            etPassword_003.error = getString(R.string.error_password_short)
            return false
        }
        if (password != confirm) {
            etPasswordConfirm_003.error = getString(R.string.error_password_mismatch)
            return false
        }
        return true
    }

    private fun setLoading(isLoading: Boolean) {
        btnRegister_003.isEnabled = !isLoading
        btnRegister_003.text =
            if (isLoading) "Memproses..." else getString(R.string.register_button)
    }

    companion object {
        const val EXTRA_USERNAME_003 = "extra_username_003"
    }
}
