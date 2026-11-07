# Pembagian Tugas & Rekayasa Penanda NIM

Dokumen ini menjelaskan pembagian tugas 6 anggota. Setiap kontribusi diberi
penanda **3 digit terakhir NIM** pada identitas buatan sendiri (nama tabel,
nama kolom, nama file SharedPreferences, key, ID view XML, field/konstanta
Kotlin). Nama bawaan framework/Kotlin (`onCreate`, `Intent`, `R`, `Flow`, dsb.)
tidak diubah agar aplikasi tetap kompatibel.

## Daftar NIM & suffix

| No | NIM | Suffix | Modul |
|----|-----|--------|-------|
| 1 | 102042300196 | `196` | Login & Sesi |
| 2 | 102042300003 | `003` | Registrasi, Keamanan Password & Logout |
| 3 | 102042300037 | `037` | Data Buku (Entity, DAO, Room) |
| 4 | 102042300030 | `030` | Dashboard & Statistik |
| 5 | 102042300146 | `146` | Daftar Buku, Pencarian, RecyclerView & Hapus |
| 6 | 102042300019 | `019` | Form Tambah/Edit Buku & Validasi |

## Bukti penanda pada database

```sql
CREATE TABLE users_003 (
  id_003 INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  username_003 TEXT NOT NULL,
  password_003 TEXT NOT NULL
);

CREATE UNIQUE INDEX index_users_003_username_003 ON users_003(username_003);

CREATE TABLE books_037 (
  id_037 INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  user_id_037 INTEGER NOT NULL,
  judul_037 TEXT NOT NULL,
  pengarang_037 TEXT NOT NULL,
  tahun_037 INTEGER NOT NULL,
  FOREIGN KEY(user_id_037) REFERENCES users_003(id_003) ON DELETE CASCADE
);

CREATE INDEX index_books_037_user_id_037 ON books_037(user_id_037);
```

Nama file database: `utspab_037.db` (lihat `AppDatabase.kt`).

## Bukti penanda pada SharedPreferences

File preference: **`session_196`**

| Key | Nilai |
|-----|-------|
| `user_id_196` | id user yang login |
| `username_196` | username yang login |
| `logged_in_196` | status sesi |

## Bukti penanda pada XML View ID

| Suffix | File layout | ID |
|--------|-------------|----|
| 196 | `activity_login.xml` | `etUsername_196`, `etPassword_196`, `btnLogin_196`, `tvGoRegister_196` |
| 003 | `activity_register.xml` | `etUsername_003`, `etPassword_003`, `etPasswordConfirm_003`, `btnRegister_003`, `tvGoLogin_003` |
| 003 | `activity_dashboard.xml` | `btnLogout_003` |
| 030 | `activity_dashboard.xml` | `tvGreeting_030`, `tvBookCount_030`, `cardMyBooks_030`, `cardAddBook_030` |
| 146 | `activity_book_list.xml` | `etSearch_146`, `rvBooks_146`, `layoutEmpty_146`, `tvEmptyTitle_146`, `tvEmptyMessage_146`, `fabAdd_146` |
| 146 | `item_book.xml` | `tvJudul_146`, `tvPengarang_146`, `tvTahun_146`, `btnDelete_146` |
| 019 | `activity_book_form.xml` | `etJudul_019`, `etPengarang_019`, `etTahun_019`, `btnSave_019`, `btnCancel_019` |

## Pembagian fungsi per anggota

### 102042300196 — Login & Sesi

File:
- `ui/LoginActivity.kt`
- `util/SessionManager.kt`
- `res/layout/activity_login.xml`

Fungsi/variabel:
- `SessionManager.saveSession()`, `isLoggedIn()`, `getUserId()`, `getUsername()`, `logout()`
- `prefs_196`, `PREF_NAME_196 = "session_196"`, `KEY_*_196`, `NO_USER_196`
- `LoginActivity` field: `etUsername_196`, `etPassword_196`, `btnLogin_196`, `session_196`, `registerLauncher_196`
- `LoginActivity.attemptLogin()` — verifikasi kredensial + simpan sesi
- `LoginActivity.setLoading()`, `goToDashboard()`
- Cek sesi otomatis di `onCreate()` agar user yang masih login langsung ke Dashboard

### 102042300003 — Registrasi, Keamanan Password & Logout

File:
- `ui/RegisterActivity.kt`
- `util/PasswordUtils.kt`
- `data/entity/User.kt`
- `data/dao/UserDao.kt`
- `res/layout/activity_register.xml`

Fungsi/variabel:
- `User` → tabel `users_003`, kolom `id_003`, `username_003`, `password_003`
- `UserDao.insert()`, `findByUsername()`, `findById()`, `countByUsername()`
- `PasswordUtils.hash()`, `verify()`, `ALGORITHM_003`, `digest_003`, `bytes_003`
- `RegisterActivity.validate()`, `setLoading()`
- `RegisterActivity` field: `etUsername_003`, `etPassword_003`, `etPasswordConfirm_003`, `btnRegister_003`
- `EXTRA_USERNAME_003`, validasi username unik + konfirmasi password
- `DashboardActivity.confirmLogout_003()` + `btnLogout_003` — logout & hapus sesi

### 102042300037 — Data Buku (Entity, DAO, Room)

File:
- `data/entity/Book.kt`
- `data/dao/BookDao.kt`
- `data/AppDatabase.kt`
- `app/schemas/.../1.json`

Fungsi/variabel:
- `Book` → tabel `books_037`, kolom `id_037`, `user_id_037`, `judul_037`, `pengarang_037`, `tahun_037`
- Foreign key `user_id_037` → `users_003.id_003`, `ON DELETE CASCADE`, index `user_id_037`
- `BookDao.observeBooksByUser()`, `searchBooks()`, `findById()`, `observeBookCount()`
- `BookDao.insert()`, `update()`, `delete()`, `deleteById()`
- `AppDatabase` + `DATABASE_NAME_037 = "utspab_037.db"`
- Semua query memfilter `user_id_037` (isolasi data)

### 102042300030 — Dashboard & Statistik

File:
- `ui/DashboardActivity.kt`
- `res/layout/activity_dashboard.xml` (selain tombol logout)

Fungsi/variabel:
- `DashboardActivity.onCreate()` — greeting user + navigasi
- `DashboardActivity.onResume()` — validasi sesi
- `userId_030`, `bookDao_030`
- `tvGreeting_030`, `tvBookCount_030`, `cardMyBooks_030`, `cardAddBook_030`
- Koleksi `observeBookCount()` real-time untuk statistik jumlah buku
- `goToLogin()` — redirect jika sesi tidak valid

### 102042300146 — Daftar Buku, Pencarian, RecyclerView & Hapus

File:
- `ui/BookListActivity.kt`
- `ui/BookAdapter.kt`
- `res/layout/activity_book_list.xml`
- `res/layout/item_book.xml`

Fungsi/variabel:
- `BookAdapter` + `BookViewHolder.bind()` — render kartu buku
- `adapter_146`, `queryFlow_146`, `layoutEmpty_146`, `tvEmptyTitle_146`, `tvEmptyMessage_146`
- `BookListActivity.onCreate()` — pasang RecyclerView, search, FAB
- `updateEmptyState()` — state kosong vs hasil pencarian kosong
- `confirmDelete()` + `deleteBook()` — dialog hapus & eksekusi `deleteById()`
- Pencarian `flatMapLatest` ke `searchBooks()` (pengarang/tahun)
- `rvBooks_146`, `etSearch_146`, `fabAdd_146`, `tvJudul_146`, `tvPengarang_146`, `tvTahun_146`, `btnDelete_146`

### 102042300019 — Form Tambah/Edit Buku & Validasi

File:
- `ui/BookFormActivity.kt`
- `res/layout/activity_book_form.xml`

Fungsi/variabel:
- `BookFormActivity.onCreate()` — mode tambah/edit dari `EXTRA_BOOK_ID_019`
- `loadBook()` — ambil buku berdasarkan `user_id_037` + `id_037`
- `saveBook()` — validasi judul, pengarang, tahun 4 digit; insert/update
- `setLoading()`, `goToLogin()`
- `newIntentForAdd()`, `newIntentForEdit()`
- `bookId_019`, `existingBook_019`, `etJudul_019`, `etPengarang_019`, `etTahun_019`, `btnSave_019`, `btnCancel_019`
- `EXTRA_BOOK_ID_019`

## Rekap fungsi (ringkas)

| NIM | Fungsi utama |
|-----|--------------|
| 102042300196 | `saveSession`, `isLoggedIn`, `getUserId`, `getUsername`, `logout`, `attemptLogin`, `setLoading`, `goToDashboard` |
| 102042300003 | `hash`, `verify`, `validate`, `insert`, `findByUsername`, `findById`, `countByUsername`, `confirmLogout_003`, `setLoading` |
| 102042300037 | `observeBooksByUser`, `searchBooks`, `findById`, `observeBookCount`, `insert`, `update`, `delete`, `deleteById`, `getInstance` |
| 102042300030 | `DashboardActivity.onCreate`, `onResume`, `goToLogin`, koleksi `observeBookCount` |
| 102042300146 | `BookAdapter.onCreateViewHolder`, `onBindViewHolder`, `bind`, `updateEmptyState`, `confirmDelete`, `deleteBook`, `goToLogin` |
| 102042300019 | `loadBook`, `saveBook`, `setLoading`, `newIntentForAdd`, `newIntentForEdit`, `goToLogin` |
