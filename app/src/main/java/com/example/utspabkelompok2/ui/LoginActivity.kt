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
import com.example.utspabkelompok2.data.entity.User
import com.example.utspabkelompok2.util.PasswordUtils
import com.example.utspabkelompok2.util.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var session: SessionManager
    private lateinit var userDao: com.example.utspabkelompok2.data.dao.UserDao

    private val registerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val username = result.data?.getStringExtra(RegisterActivity.EXTRA_USERNAME)
            if (!username.isNullOrBlank()) {
                etUsername.setText(username)
                etPassword.requestFocus()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)
        if (session.isLoggedIn() && session.getUserId() != SessionManager.NO_USER) {
            goToDashboard()
            return
        }

        setContentView(R.layout.activity_login)

        userDao = AppDatabase.getInstance(this).userDao()

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener { attemptLogin() }

        findViewById<TextView>(R.id.tvGoRegister).setOnClickListener {
            registerLauncher.launch(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val username = etUsername.text?.toString()?.trim().orEmpty()
        val password = etPassword.text?.toString().orEmpty()

        if (username.isEmpty()) {
            etUsername.error = getString(R.string.error_username_empty)
            return
        }
        if (password.isEmpty()) {
            etPassword.error = getString(R.string.error_password_empty)
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            try {
                val user: User? = userDao.findByUsername(username)
                if (user == null || !PasswordUtils.verify(password, user.password)) {
                    Toast.makeText(
                        this@LoginActivity,
                        R.string.error_login,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                session.saveSession(user.id, user.username)
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
        btnLogin.isEnabled = !isLoading
        btnLogin.text = if (isLoading) "Memuat..." else getString(R.string.login_button)
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
