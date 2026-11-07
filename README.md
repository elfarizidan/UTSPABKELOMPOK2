# Perpustakaan Pribadi — UTS PAB

Aplikasi Android sederhana untuk mengelola koleksi buku pribadi. Dibuat dengan **Kotlin**, **Room (SQLite)**, dan **SharedPreferences** tanpa koneksi internet/API.

## Fitur

- **Autentikasi**: register, login, sesi login tersimpan (SharedPreferences), dan logout.
- **Isolasi data**: setiap buku memiliki `user_id` (foreign key ke tabel `users`). Semua query daftar, pencarian, detail, update, dan hapus selalu memfilter `user_id`, sehingga user hanya bisa melihat/mengubah datanya sendiri.
- **CRUD Buku**: tambah, lihat daftar (RecyclerView), edit, dan hapus dengan konfirmasi.
- **Pencarian**: berdasarkan **pengarang** atau **tahun terbit** (langsung memfilter data di database).
- **Dashboard**: sapaan user, jumlah total buku milik user (realtime), dan menu navigasi.
- **Password** disimpan sebagai hash SHA-256, bukan teks biasa.

## Alur navigasi

```
Login ──► Dashboard ──► Daftar Buku ──► Form Tambah
   ▲           │              │
   │           │              └──► Form Edit (ketuk kartu)
   └───────────┴── Logout ────┴──► Hapus (tombol + dialog konfirmasi)
```

Saat aplikasi dibuka, `LoginActivity` mengecek sesi di SharedPreferences. Jika masih login, user langsung diarahkan ke Dashboard.

## Struktur database

```
users
- id (INTEGER, PK autoGenerate)
- username (TEXT, unique index)
- password (TEXT, SHA-256)

books
- id (INTEGER, PK autoGenerate)
- user_id (INTEGER, FK -> users.id, ON DELETE CASCADE, index)
- judul (TEXT)
- pengarang (TEXT)
- tahun (INTEGER)
```

Entity & DAO: `app/src/main/java/com/example/utspabkelompok2/data/`.
Database dibuat lewat `AppDatabase` (Room) dengan nama `utspab.db`.

Query pencarian:

```sql
SELECT * FROM books
WHERE user_id = :userId
  AND (pengarang LIKE '%' || :query || '%'
       OR CAST(tahun AS TEXT) LIKE '%' || :query || '%')
ORDER BY judul COLLATE NOCASE ASC
```

## Struktur project

```
app/src/main/java/com/example/utspabkelompok2/
├── data/
│   ├── AppDatabase.kt
│   ├── dao/UserDao.kt, BookDao.kt
│   └── entity/User.kt, Book.kt
├── ui/
│   ├── LoginActivity.kt
│   ├── RegisterActivity.kt
│   ├── DashboardActivity.kt
│   ├── BookListActivity.kt
│   ├── BookFormActivity.kt
│   └── BookAdapter.kt
└── util/
    ├── SessionManager.kt
    └── PasswordUtils.kt
```

## Cara menjalankan

1. Buka folder project ini di **Android Studio**.
2. Tunggu Gradle sync selesai (Room memakai KSP; library diunduh otomatis).
3. Jalankan pada emulator/device Android (minSdk 28).
4. Daftar akun baru → login → kelola buku.

Build dari terminal (PowerShell):

```powershell
$env:JAVA_HOME = "E:\Android Studio\jbr"   # sesuaikan lokasi JDK/Android Studio
.\gradlew.bat :app:assembleDebug
```

APK hasil build: `app/build/outputs/apk/debug/app-debug.apk`.

## Pengujian cepat isolasi data

1. Daftar user **budi** dan **siti**.
2. Login sebagai budi, tambahkan beberapa buku.
3. Logout, login sebagai siti — daftar buku kosong (data budi tidak terlihat).
4. Tambahkan buku siti, lalu cari berdasarkan pengarang/tahun. Hanya buku siti yang muncul.
5. Login kembali sebagai budi — hanya buku budi yang tampil.

## Catatan

- KSP digunakan untuk kompilasi Room (`androidx.room:room-compiler`).
- `fallbackToDestructiveMigration` dipakai agar perubahan skema saat pengembangan tidak membuat aplikasi crash; data akan direset ketika versi database naik.
- Tidak ada permission internet, seluruh data disimpan lokal di SQLite.
