package com.example.utspabkelompok2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.utspabkelompok2.data.dao.BookDao
import com.example.utspabkelompok2.data.dao.UserDao
import com.example.utspabkelompok2.data.entity.Book
import com.example.utspabkelompok2.data.entity.User

@Database(
    entities = [User::class, Book::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao

    companion object {
        // Nama file database diberi suffix 037 (modul data buku).
        private const val DATABASE_NAME_037 = "utspab_037.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME_037
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
