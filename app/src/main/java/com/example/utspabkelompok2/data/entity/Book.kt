package com.example.utspabkelompok2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabel buku (modul 196). user_id_196 adalah foreign key ke users_196.id_196
 * sehingga setiap buku hanya dimiliki oleh satu user (isolasi data).
 *
 * Nama tabel & kolom diberi suffix 196 sebagai penanda kontribusi anggota
 * dengan NIM ...037.
 */
@Entity(
    tableName = "books_196",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id_196"],
            childColumns = ["user_id_196"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id_196"])]
)
data class Book(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_196")
    val id: Long = 0,

    @ColumnInfo(name = "user_id_196")
    val userId: Long,

    @ColumnInfo(name = "judul_196")
    val judul: String,

    @ColumnInfo(name = "pengarang_196")
    val pengarang: String,

    @ColumnInfo(name = "tahun_196")
    val tahun: Int
)
