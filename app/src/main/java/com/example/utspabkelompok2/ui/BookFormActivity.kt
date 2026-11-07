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

class BookFormActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var etJudul: TextInputEditText
    private lateinit var etPengarang: TextInputEditText
    private lateinit var etTahun: TextInputEditText
    private lateinit var btnSave: MaterialButton

    private var bookId: Long = 0L
    private var existingBook: Book? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)
        if (!session.isLoggedIn() || session.getUserId() == SessionManager.NO_USER) {
            goToLogin()
            return
        }

        setContentView(R.layout.activity_book_form)

        bookId = intent.getLongExtra(EXTRA_BOOK_ID, 0L)
        val isEdit = bookId > 0L
        supportActionBar?.title =
            getString(if (isEdit) R.string.form_edit_title else R.string.form_add_title)

        etJudul = findViewById(R.id.etJudul)
        etPengarang = findViewById(R.id.etPengarang)
        etTahun = findViewById(R.id.etTahun)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener { saveBook(isEdit) }
        findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener { finish() }

        if (isEdit) loadBook()
    }

    private fun loadBook() {
        val userId = session.getUserId()
        lifecycleScope.launch {
            try {
                // findById selalu menyertakan user_id -> mencegah akses buku milik user lain.
                val book = AppDatabase.getInstance(this@BookFormActivity)
                    .bookDao()
                    .findById(userId, bookId)

                if (book == null) {
                    Toast.makeText(
                        this@BookFormActivity,
                        R.string.book_not_found,
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    return@launch
                }

                existingBook = book
                etJudul.setText(book.judul)
                etPengarang.setText(book.pengarang)
                etTahun.setText(book.tahun.toString())
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
        val judul = etJudul.text?.toString()?.trim().orEmpty()
        val pengarang = etPengarang.text?.toString()?.trim().orEmpty()
        val tahunText = etTahun.text?.toString()?.trim().orEmpty()
        val tahun = tahunText.toIntOrNull()

        if (judul.isEmpty()) {
            etJudul.error = getString(R.string.error_judul_empty)
            return
        }
        if (pengarang.isEmpty()) {
            etPengarang.error = getString(R.string.error_pengarang_empty)
            return
        }
        if (tahun == null || tahunText.length != 4 || tahun !in 1000..2100) {
            etTahun.error = getString(R.string.error_tahun_invalid)
            return
        }

        val userId = session.getUserId()
        val bookDao = AppDatabase.getInstance(this).bookDao()

        setLoading(true)
        lifecycleScope.launch {
            try {
                if (isEdit) {
                    val current = existingBook ?: bookDao.findById(userId, bookId)
                    if (current == null) {
                        Toast.makeText(
                            this@BookFormActivity,
                            R.string.book_not_found,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                        return@launch
                    }

                    bookDao.update(
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
                    bookDao.insert(
                        Book(
                            userId = userId,
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
        btnSave.isEnabled = !isLoading
        btnSave.text = if (isLoading) "Menyimpan..." else getString(R.string.save)
    }

    private fun goToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    companion object {
        private const val EXTRA_BOOK_ID = "extra_book_id"

        fun newIntentForAdd(context: Context): Intent =
            Intent(context, BookFormActivity::class.java)

        fun newIntentForEdit(context: Context, bookId: Long): Intent =
            Intent(context, BookFormActivity::class.java)
                .putExtra(EXTRA_BOOK_ID, bookId)
    }
}
