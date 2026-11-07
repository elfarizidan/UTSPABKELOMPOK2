package com.example.utspabkelompok2.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.utspabkelompok2.data.entity.User

/**
 * DAO user (modul 003). Seluruh query memakai nama tabel/kolom ber-suffix 003.
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users_003 WHERE username_003 = :username LIMIT 1")
    suspend fun findByUsername(username: String): User?

    @Query("SELECT * FROM users_003 WHERE id_003 = :id LIMIT 1")
    suspend fun findById(id: Long): User?

    @Query("SELECT COUNT(*) FROM users_003 WHERE username_003 = :username")
    suspend fun countByUsername(username: String): Int
}
