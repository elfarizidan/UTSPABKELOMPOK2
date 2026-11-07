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

@OptIn(ExperimentalCoroutinesApi::class)
class BookListActivity : AppCompatActivity() {

    private lateinit var session: SessionManager
    private lateinit var adapter: BookAdapter
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var tvEmptyTitle: TextView
    private lateinit var tvEmptyMessage: TextView

    private val queryFlow = MutableStateFlow("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        session = SessionManager(this)
        if (!session.isLoggedIn() || session.getUserId() == SessionManager.NO_USER) {
            goToLogin()
            return
        }

        setContentView(R.layout.activity_book_list)

        val userId = session.getUserId()
        val bookDao = AppDatabase.getInstance(this).bookDao()

        layoutEmpty = findViewById(R.id.layoutEmpty)
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)

        adapter = BookAdapter(
            onItemClick = { book ->
                startActivity(BookFormActivity.newIntentForEdit(this, book.id))
            },
            onDeleteClick = { book -> confirmDelete(book) }
        )

        findViewById<RecyclerView>(R.id.rvBooks).adapter = adapter

        findViewById<ExtendedFloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(BookFormActivity.newIntentForAdd(this))
        }

        findViewById<TextInputEditText>(R.id.etSearch).addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                override fun afterTextChanged(s: Editable?) {
                    queryFlow.value = s?.toString().orEmpty()
                }
            }
        )

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                queryFlow.flatMapLatest { rawQuery ->
                    val query = rawQuery.trim()
                    if (query.isEmpty()) {
                        bookDao.observeBooksByUser(userId)
                    } else {
                        bookDao.searchBooks(userId, query)
                    }
                }.collect { books ->
                    adapter.submitList(books)
                    updateEmptyState(books.isEmpty(), queryFlow.value.trim())
                }
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean, query: String) {
        if (!isEmpty) {
            layoutEmpty.visibility = LinearLayout.GONE
            return
        }

        layoutEmpty.visibility = LinearLayout.VISIBLE
        if (query.isEmpty()) {
            tvEmptyTitle.setText(R.string.empty_books_title)
            tvEmptyMessage.setText(R.string.empty_books_message)
        } else {
            tvEmptyTitle.setText(R.string.empty_search_title)
            tvEmptyMessage.setText(R.string.empty_search_message)
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
        val userId = session.getUserId()
        val bookDao = AppDatabase.getInstance(this).bookDao()
        lifecycleScope.launch {
            try {
                // Query tetap memakai user_id sehingga user tidak bisa menghapus data user lain.
                bookDao.deleteById(userId, book.id)
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
