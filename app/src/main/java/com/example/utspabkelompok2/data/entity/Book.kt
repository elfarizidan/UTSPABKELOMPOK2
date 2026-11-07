package com.example.utspabkelompok2.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabel buku. user_id adalah foreign key ke tabel users sehingga
 * setiap buku hanya dimiliki oleh satu user (isolasi data).
 */
@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class Book(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "judul")
    val judul: String,

    @ColumnInfo(name = "pengarang")
    val pengarang: String,

    @ColumnInfo(name = "tahun")
    val tahun: Int
)
