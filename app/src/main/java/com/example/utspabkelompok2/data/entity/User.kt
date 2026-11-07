package com.example.utspabkelompok2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabel user untuk autentikasi (modul 003).
 * Password disimpan dalam bentuk hash SHA-256 (lihat PasswordUtils).
 *
 * Nama tabel & kolom diberi suffix 003 sebagai penanda kontribusi anggota
 * dengan NIM ...003.
 */
@Entity(
    tableName = "users_003",
    indices = [Index(value = ["username_003"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_003")
    val id: Long = 0,

    @ColumnInfo(name = "username_003")
    val username: String,

    @ColumnInfo(name = "password_003")
    val password: String
)
