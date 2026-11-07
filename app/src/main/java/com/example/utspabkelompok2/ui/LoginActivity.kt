package com.example.utspabkelompok2.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.utspabkelompok2.R
import com.example.utspabkelompok2.data.AppDatabase
import com.example.utspabkelompok2.data.dao.UserDao
import com.example.utspabkelompok2.data.entity.User
import com.example.utspabkelompok2.util.PasswordUtils
import com.example.utspabkelompok2.util.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

/**
 * Modul 196 — Login & Sesi.
 * Variabel milik modul ini memakai suffix 196.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername_196: TextInputEditText
    private lateinit var etPassword_196: TextInputEditText
    private lateinit var btnLogin_196: MaterialButton
    private lateinit var session_196: SessionManager
    private lateinit var userDao_003: UserDao

    private val registerLauncher_196 = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val username = result.data?.getStringExtra(RegisterActivity.EXTRA_USERNAME_003)
            if (!username.isNullOrBlank()) {
                etUsername_196.setText(username)
                etPassword_196.requestFocus()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session_196 = SessionManager(this)
        if (session_196.isLoggedIn() && session_196.getUserId() != SessionManager.NO_USER_196) {
            goToDashboard()
            return
        }

        setContentView(R.layout.activity_login)

        userDao_003 = AppDatabase.getInstance(this).userDao()

        etUsername_196 = findViewById(R.id.etUsername_196)
        etPassword_196 = findViewById(R.id.etPassword_196)
        btnLogin_196 = findViewById(R.id.btnLogin_196)

        btnLogin_196.setOnClickListener { attemptLogin() }

        findViewById<TextView>(R.id.tvGoRegister_196).setOnClickListener {
            registerLauncher_196.launch(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val username = etUsername_196.text?.toString()?.trim().orEmpty()
        val password = etPassword_196.text?.toString().orEmpty()

        if (username.isEmpty()) {
            etUsername_196.error = getString(R.string.error_username_empty)
            return
        }
        if (password.isEmpty()) {
            etPassword_196.error = getString(R.string.error_password_empty)
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            try {
                val user: User? = userDao_003.findByUsername(username)
                if (user == null || !PasswordUtils.verify(password, user.password)) {
                    Toast.makeText(
                        this@LoginActivity,
                        R.string.error_login,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                session_196.saveSession(user.id, user.username)
                Toast.makeText(
                    this@LoginActivity,
                    R.string.login_success,
                    Toast.LENGTH_SHORT
                ).show()
                goToDashboard()
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    R.string.error_unknown,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        btnLogin_196.isEnabled = !isLoading
        btnLogin_196.text = if (isLoading) "Memuat..." else getString(R.string.login_button)
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
