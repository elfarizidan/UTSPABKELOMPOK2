package com.example.utspabkelompok2.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.utspabkelompok2.data.entity.Book
import kotlinx.coroutines.flow.Flow

/**
 * DAO buku (modul 196). Seluruh query memakai nama tabel/kolom ber-suffix 196
 * dan selalu memfilter user_id_196 agar data tiap user terisolasi.
 */
@Dao
interface BookDao {

    /**
     * Semua query selalu memfilter dengan user_id_196 agar data tiap user terisolasi.
     */
    @Query(
        "SELECT * FROM books_196 WHERE user_id_196 = :userId " +
            "ORDER BY judul_196 COLLATE NOCASE ASC"
    )
    fun observeBooksByUser(userId: Long): Flow<List<Book>>

    /**
     * Pencarian berdasarkan pengarang ATAU tahun terbit.
     * Query kosong akan menampilkan seluruh buku milik user.
     */
    @Query(
        "SELECT * FROM books_196 WHERE user_id_196 = :userId AND (" +
            "pengarang_196 LIKE '%' || :query || '%' OR " +
            "CAST(tahun_196 AS TEXT) LIKE '%' || :query || '%') " +
            "ORDER BY judul_196 COLLATE NOCASE ASC"
    )
    fun searchBooks(userId: Long, query: String): Flow<List<Book>>

    @Query("SELECT * FROM books_196 WHERE id_196 = :bookId AND user_id_196 = :userId LIMIT 1")
    suspend fun findById(userId: Long, bookId: Long): Book?

    @Query("SELECT COUNT(*) FROM books_196 WHERE user_id_196 = :userId")
    fun observeBookCount(userId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(book: Book): Long

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("DELETE FROM books_196 WHERE id_196 = :bookId AND user_id_196 = :userId")
    suspend fun deleteById(userId: Long, bookId: Long)
}
