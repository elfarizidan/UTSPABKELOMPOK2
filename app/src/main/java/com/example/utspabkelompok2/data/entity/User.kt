package com.example.utspabkelompok2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabel user untuk autentikasi (modul 196).
 * Password disimpan dalam bentuk hash SHA-256 (lihat PasswordUtils).
 *
 * Nama tabel & kolom diberi suffix 196 sebagai penanda kontribusi anggota
 * dengan NIM ...003.
 */
@Entity(
    tableName = "users_196",
    indices = [Index(value = ["username_196"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_196")
    val id: Long = 0,

    @ColumnInfo(name = "username_196")
    val username: String,

    @ColumnInfo(name = "password_196")
    val password: String
)
