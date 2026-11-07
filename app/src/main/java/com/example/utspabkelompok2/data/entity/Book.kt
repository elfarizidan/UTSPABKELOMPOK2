package com.example.utspabkelompok2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabel buku (modul 037). user_id_037 adalah foreign key ke users_003.id_003
 * sehingga setiap buku hanya dimiliki oleh satu user (isolasi data).
 *
 * Nama tabel & kolom diberi suffix 037 sebagai penanda kontribusi anggota
 * dengan NIM ...037.
 */
@Entity(
    tableName = "books_037",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id_003"],
            childColumns = ["user_id_037"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id_037"])]
)
data class Book(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_037")
    val id: Long = 0,

    @ColumnInfo(name = "user_id_037")
    val userId: Long,

    @ColumnInfo(name = "judul_037")
    val judul: String,

    @ColumnInfo(name = "pengarang_037")
    val pengarang: String,

    @ColumnInfo(name = "tahun_037")
    val tahun: Int
)
