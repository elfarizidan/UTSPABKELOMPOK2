package com.example.utspabkelompok2.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.utspabkelompok2.R
import com.example.utspabkelompok2.data.AppDatabase
import com.example.utspabkelompok2.data.entity.Book
import com.example.utspabkelompok2.util.SessionManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

/**
 * Modul 146 — Daftar buku, pencarian, RecyclerView, dan aksi hapus.
 * Variabel milik modul ini memakai suffix 146.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BookListActivity : AppCompatActivity() {

    private lateinit var session_196: SessionManager
    private lateinit var adapter_146: BookAdapter
    private lateinit var layoutEmpty_146: LinearLayout
    private lateinit var tvEmptyTitle_146: TextView
    private lateinit var tvEmptyMessage_146: TextView

    private val queryFlow_146 = MutableStateFlow("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session_196 = SessionManager(this)
        if (!session_196.isLoggedIn() || session_196.getUserId() == SessionManager.NO_USER_196) {
            goToLogin()
            return
        }

        setContentView(R.layout.activity_book_list)

        val userId_146 = session_196.getUserId()
        val bookDao_146 = AppDatabase.getInstance(this).bookDao()

        layoutEmpty_146 = findViewById(R.id.layoutEmpty_146)
        tvEmptyTitle_146 = findViewById(R.id.tvEmptyTitle_146)
        tvEmptyMessage_146 = findViewById(R.id.tvEmptyMessage_146)

        adapter_146 = BookAdapter(
            onItemClick = { book ->
                startActivity(BookFormActivity.newIntentForEdit(this, book.id))
            },
            onDeleteClick = { book -> confirmDelete(book) }
        )

        findViewById<RecyclerView>(R.id.rvBooks_146).adapter = adapter_146

        findViewById<ExtendedFloatingActionButton>(R.id.fabAdd_146).setOnClickListener {
            startActivity(BookFormActivity.newIntentForAdd(this))
        }

        findViewById<TextInputEditText>(R.id.etSearch_146).addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                override fun afterTextChanged(s: Editable?) {
                    queryFlow_146.value = s?.toString().orEmpty()
                }
            }
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                queryFlow_146.flatMapLatest { rawQuery ->
                    val query = rawQuery.trim()
                    if (query.isEmpty()) {
                        bookDao_146.observeBooksByUser(userId_146)
                    } else {
                        bookDao_146.searchBooks(userId_146, query)
                    }
                }.collect { books ->
                    adapter_146.submitList(books)
                    updateEmptyState(books.isEmpty(), queryFlow_146.value.trim())
                }
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean, query: String) {
        if (!isEmpty) {
            layoutEmpty_146.visibility = LinearLayout.GONE
            return
        }

        layoutEmpty_146.visibility = LinearLayout.VISIBLE
        if (query.isEmpty()) {
            tvEmptyTitle_146.setText(R.string.empty_books_title)
            tvEmptyMessage_146.setText(R.string.empty_books_message)
        } else {
            tvEmptyTitle_146.setText(R.string.empty_search_title)
            tvEmptyMessage_146.setText(R.string.empty_search_message)
        }
    }

    private fun confirmDelete(book: Book) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_confirm_title)
            .setMessage(getString(R.string.delete_confirm_message, book.judul))
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteBook(book)
            }
            .show()
    }

    private fun deleteBook(book: Book) {
        val userId_146 = session_196.getUserId()
        val bookDao_146 = AppDatabase.getInstance(this).bookDao()
        lifecycleScope.launch {
            try {
                // Query tetap memakai user_id_037 sehingga user tidak bisa menghapus data user lain.
                bookDao_146.deleteById(userId_146, book.id)
                Toast.makeText(this@BookListActivity, R.string.book_deleted, Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(this@BookListActivity, R.string.error_unknown, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun goToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }
}
