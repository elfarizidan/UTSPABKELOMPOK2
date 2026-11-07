package com.example.utspabkelompok2.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.utspabkelompok2.R
import com.example.utspabkelompok2.data.AppDatabase
import com.example.utspabkelompok2.util.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

/**
 * Modul 030 — Dashboard & statistik jumlah buku (dengan logout modul 003).
 * Variabel milik modul ini memakai suffix 030.
 */
class DashboardActivity : AppCompatActivity() {

    private lateinit var session_196: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session_196 = SessionManager(this)
        if (!session_196.isLoggedIn() || session_196.getUserId() == SessionManager.NO_USER_196) {
            goToLogin()
            return
        }

        setContentView(R.layout.activity_dashboard)

        val userId_030 = session_196.getUserId()

        findViewById<TextView>(R.id.tvGreeting_030).text =
            getString(R.string.dashboard_greeting, session_196.getUsername())

        findViewById<MaterialCardView>(R.id.cardMyBooks_030).setOnClickListener {
            startActivity(Intent(this, BookListActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.cardAddBook_030).setOnClickListener {
            startActivity(BookFormActivity.newIntentForAdd(this))
        }

        findViewById<MaterialButton>(R.id.btnLogout_003).setOnClickListener {
            confirmLogout_003()
        }

        val bookDao_030 = AppDatabase.getInstance(this).bookDao()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookDao_030.observeBookCount(userId_030).collect { count ->
                    findViewById<TextView>(R.id.tvBookCount_030).text = count.toString()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!session_196.isLoggedIn()) {
            goToLogin()
        }
    }

    private fun confirmLogout_003() {
        AlertDialog.Builder(this)
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.logout) { _, _ ->
                session_196.logout()
                Toast.makeText(this, "Berhasil keluar", Toast.LENGTH_SHORT).show()
                goToLogin()
            }
            .show()
    }

    private fun goToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }
}
