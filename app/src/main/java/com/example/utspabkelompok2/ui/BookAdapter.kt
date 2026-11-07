package com.example.utspabkelompok2.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.utspabkelompok2.R
import com.example.utspabkelompok2.data.entity.Book
import com.google.android.material.button.MaterialButton

/**
 * Modul 146 — Adapter RecyclerView daftar buku.
 * Variabel milik modul ini memakai suffix 146.
 */
class BookAdapter(
    private val onItemClick: (Book) -> Unit,
    private val onDeleteClick: (Book) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {

        private val tvJudul_146: TextView = itemView.findViewById(R.id.tvJudul_146)
        private val tvPengarang_146: TextView = itemView.findViewById(R.id.tvPengarang_146)
        private val tvTahun_146: TextView = itemView.findViewById(R.id.tvTahun_146)
        private val btnDelete_146: MaterialButton = itemView.findViewById(R.id.btnDelete_146)

        fun bind(book: Book) {
            val context = itemView.context
            tvJudul_146.text = book.judul
            tvPengarang_146.text = context.getString(R.string.book_by, book.pengarang)
            tvTahun_146.text = context.getString(R.string.book_year, book.tahun)

            itemView.setOnClickListener { onItemClick(book) }
            btnDelete_146.setOnClickListener { onDeleteClick(book) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean =
                oldItem == newItem
        }
    }
}
