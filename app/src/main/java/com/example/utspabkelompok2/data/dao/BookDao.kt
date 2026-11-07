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
 * DAO buku (modul 037). Seluruh query memakai nama tabel/kolom ber-suffix 037
 * dan selalu memfilter user_id_037 agar data tiap user terisolasi.
 */
@Dao
interface BookDao {

    /**
     * Semua query selalu memfilter dengan user_id_037 agar data tiap user terisolasi.
     */
    @Query(
        "SELECT * FROM books_037 WHERE user_id_037 = :userId " +
            "ORDER BY judul_037 COLLATE NOCASE ASC"
    )
    fun observeBooksByUser(userId: Long): Flow<List<Book>>

    /**
     * Pencarian berdasarkan pengarang ATAU tahun terbit.
     * Query kosong akan menampilkan seluruh buku milik user.
     */
    @Query(
        "SELECT * FROM books_037 WHERE user_id_037 = :userId AND (" +
            "pengarang_037 LIKE '%' || :query || '%' OR " +
            "CAST(tahun_037 AS TEXT) LIKE '%' || :query || '%') " +
            "ORDER BY judul_037 COLLATE NOCASE ASC"
    )
    fun searchBooks(userId: Long, query: String): Flow<List<Book>>

    @Query("SELECT * FROM books_037 WHERE id_037 = :bookId AND user_id_037 = :userId LIMIT 1")
    suspend fun findById(userId: Long, bookId: Long): Book?

    @Query("SELECT COUNT(*) FROM books_037 WHERE user_id_037 = :userId")
    fun observeBookCount(userId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(book: Book): Long

    @Update
    suspend fun update(book: Book)

    @Delete
    suspend fun delete(book: Book)

    @Query("DELETE FROM books_037 WHERE id_037 = :bookId AND user_id_037 = :userId")
    suspend fun deleteById(userId: Long, bookId: Long)
}
