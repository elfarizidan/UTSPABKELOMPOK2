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
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)
        if (!session.isLoggedIn() || session.getUserId() == SessionManager.NO_USER) {
            goToLogin()
            return
        }

        setContentView(R.layout.activity_dashboard)

        val userId = session.getUserId()

        findViewById<TextView>(R.id.tvGreeting).text =
            getString(R.string.dashboard_greeting, session.getUsername())

        findViewById<MaterialCardView>(R.id.cardMyBooks).setOnClickListener {
            startActivity(Intent(this, BookListActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.cardAddBook).setOnClickListener {
            startActivity(BookFormActivity.newIntentForAdd(this))
        }

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            confirmLogout()
        }

        val bookDao = AppDatabase.getInstance(this).bookDao()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                bookDao.observeBookCount(userId).collect { count ->
                    findViewById<TextView>(R.id.tvBookCount).text = count.toString()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!session.isLoggedIn()) {
            goToLogin()
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_message)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.logout) { _, _ ->
                session.logout()
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
