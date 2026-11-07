package com.example.utspabkelompok2.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.utspabkelompok2.R
import com.example.utspabkelompok2.data.AppDatabase
import com.example.utspabkelompok2.data.entity.Book
import com.example.utspabkelompok2.util.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

/**
 * Modul 019 — Form tambah & edit buku beserta validasi input.
 * Variabel milik modul ini memakai suffix 019.
 */
class BookFormActivity : AppCompatActivity() {

    private lateinit var session_196: SessionManager
    private lateinit var etJudul_019: TextInputEditText
    private lateinit var etPengarang_019: TextInputEditText
    private lateinit var etTahun_019: TextInputEditText
    private lateinit var btnSave_019: MaterialButton

    private var bookId_019: Long = 0L
    private var existingBook_019: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session_196 = SessionManager(this)
        if (!session_196.isLoggedIn() || session_196.getUserId() == SessionManager.NO_USER_196) {
            goToLogin()
            return
        }

        setContentView(R.layout.activity_book_form)

        bookId_019 = intent.getLongExtra(EXTRA_BOOK_ID_019, 0L)
        val isEdit = bookId_019 > 0L
        supportActionBar?.title =
            getString(if (isEdit) R.string.form_edit_title else R.string.form_add_title)

        etJudul_019 = findViewById(R.id.etJudul_019)
        etPengarang_019 = findViewById(R.id.etPengarang_019)
        etTahun_019 = findViewById(R.id.etTahun_019)
        btnSave_019 = findViewById(R.id.btnSave_019)

        btnSave_019.setOnClickListener { saveBook(isEdit) }
        findViewById<MaterialButton>(R.id.btnCancel_019).setOnClickListener { finish() }

        if (isEdit) loadBook()
    }

    private fun loadBook() {
        val userId_019 = session_196.getUserId()
        lifecycleScope.launch {
            try {
                // findById selalu menyertakan user_id_037 -> mencegah akses buku milik user lain.
                val book = AppDatabase.getInstance(this@BookFormActivity)
                    .bookDao()
                    .findById(userId_019, bookId_019)

                if (book == null) {
                    Toast.makeText(
                        this@BookFormActivity,
                        R.string.book_not_found,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    return@launch
                }

                existingBook_019 = book
                etJudul_019.setText(book.judul)
                etPengarang_019.setText(book.pengarang)
                etTahun_019.setText(book.tahun.toString())
            } catch (e: Exception) {
                Toast.makeText(
                    this@BookFormActivity,
                    R.string.error_unknown,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun saveBook(isEdit: Boolean) {
        val judul = etJudul_019.text?.toString()?.trim().orEmpty()
        val pengarang = etPengarang_019.text?.toString()?.trim().orEmpty()
        val tahunText = etTahun_019.text?.toString()?.trim().orEmpty()
        val tahun = tahunText.toIntOrNull()

        if (judul.isEmpty()) {
            etJudul_019.error = getString(R.string.error_judul_empty)
            return
        }
        if (pengarang.isEmpty()) {
            etPengarang_019.error = getString(R.string.error_pengarang_empty)
            return
        }
        if (tahun == null || tahunText.length != 4 || tahun !in 1000..2100) {
            etTahun_019.error = getString(R.string.error_tahun_invalid)
            return
        }

        val userId_019 = session_196.getUserId()
        val bookDao_019 = AppDatabase.getInstance(this).bookDao()

        setLoading(true)
        lifecycleScope.launch {
            try {
                if (isEdit) {
                    val current = existingBook_019 ?: bookDao_019.findById(userId_019, bookId_019)
                    if (current == null) {
                        Toast.makeText(
                            this@BookFormActivity,
                            R.string.book_not_found,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return@launch
                    }

                    bookDao_019.update(
                        current.copy(
                            judul = judul,
                            pengarang = pengarang,
                            tahun = tahun
                        )
                    )
                    Toast.makeText(
                        this@BookFormActivity,
                        R.string.book_updated,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    bookDao_019.insert(
                        Book(
                            userId = userId_019,
                            judul = judul,
                            pengarang = pengarang,
                            tahun = tahun
                        )
                    )
                    Toast.makeText(
                        this@BookFormActivity,
                        R.string.book_saved,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@BookFormActivity,
                    R.string.error_unknown,
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        btnSave_019.isEnabled = !isLoading
        btnSave_019.text = if (isLoading) "Menyimpan..." else getString(R.string.save)
    }

    private fun goToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    companion object {
        private const val EXTRA_BOOK_ID_019 = "extra_book_id_019"

        fun newIntentForAdd(context: Context): Intent =
            Intent(context, BookFormActivity::class.java)

        fun newIntentForEdit(context: Context, bookId: Long): Intent =
            Intent(context, BookFormActivity::class.java)
                .putExtra(EXTRA_BOOK_ID_019, bookId)
    }
}
