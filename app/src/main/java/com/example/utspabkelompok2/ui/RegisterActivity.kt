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

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etPasswordConfirm: TextInputEditText
    private lateinit var btnRegister: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val userDao = AppDatabase.getInstance(this).userDao()

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val username = etUsername.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString().orEmpty()
            val confirm = etPasswordConfirm.text?.toString().orEmpty()

            if (!validate(username, password, confirm)) return@setOnClickListener

            setLoading(true)
            lifecycleScope.launch {
                try {
                    if (userDao.countByUsername(username) > 0) {
                        etUsername.error = getString(R.string.error_username_taken)
                        return@launch
                    }

                    userDao.insert(
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
                    val result = Intent().putExtra(EXTRA_USERNAME, username)
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

        findViewById<TextView>(R.id.tvGoLogin).setOnClickListener { finish() }
    }

    private fun validate(username: String, password: String, confirm: String): Boolean {
        if (username.isEmpty()) {
            etUsername.error = getString(R.string.error_username_empty)
            return false
        }
        if (username.length < 3) {
            etUsername.error = "Username minimal 3 karakter"
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = getString(R.string.error_password_empty)
            return false
        }
        if (password.length < 6) {
            etPassword.error = getString(R.string.error_password_short)
            return false
        }
        if (password != confirm) {
            etPasswordConfirm.error = getString(R.string.error_password_mismatch)
            return false
        }
        return true
    }

    private fun setLoading(isLoading: Boolean) {
        btnRegister.isEnabled = !isLoading
        btnRegister.text =
            if (isLoading) "Memproses..." else getString(R.string.register_button)
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }
}
