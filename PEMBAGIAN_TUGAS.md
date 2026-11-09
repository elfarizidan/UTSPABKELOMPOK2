# Penanda NIM & Daftar Fungsi

Seluruh penanda pada project ini menggunakan **satu NIM**:

| NIM | Suffix |
|-----|--------|
| **102042300196** | **`196`** |

Suffix `196` diterapkan pada identitas buatan sendiri: nama tabel, nama kolom,
nama/key SharedPreferences, ID view XML, serta field/konstanta Kotlin. Nama
bawaan framework/Kotlin (`onCreate`, `Intent`, `R`, `Flow`, dsb.) tidak diubah
agar aplikasi tetap kompatibel.

## Bukti penanda pada database

```sql
CREATE TABLE users_196 (
  id_196 INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  username_196 TEXT NOT NULL,
  password_196 TEXT NOT NULL
);

CREATE UNIQUE INDEX index_users_196_username_196 ON users_196(username_196);

CREATE TABLE books_196 (
  id_196 INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  user_id_196 INTEGER NOT NULL,
  judul_196 TEXT NOT NULL,
  pengarang_196 TEXT NOT NULL,
  tahun_196 INTEGER NOT NULL,
  FOREIGN KEY(user_id_196) REFERENCES users_196(id_196) ON DELETE CASCADE
);

CREATE INDEX index_books_196_user_id_196 ON books_196(user_id_196);
```

Nama file database: `utspab_196.db` (lihat `AppDatabase.kt`).

## Bukti penanda pada SharedPreferences

File preference: **`session_196`**

| Key | Nilai |
|-----|-------|
| `user_id_196` | id user yang login |
| `username_196` | username yang login |
| `logged_in_196` | status sesi |

## Bukti penanda pada XML View ID

| File layout | ID |
|-------------|----|
| `activity_login.xml` | `etUsername_196`, `etPassword_196`, `btnLogin_196`, `tvGoRegister_196` |
| `activity_register.xml` | `etUsername_196`, `etPassword_196`, `etPasswordConfirm_196`, `btnRegister_196`, `tvGoLogin_196` |
| `activity_dashboard.xml` | `tvGreeting_196`, `tvBookCount_196`, `cardMyBooks_196`, `cardAddBook_196`, `btnLogout_196` |
| `activity_book_list.xml` | `etSearch_196`, `rvBooks_196`, `layoutEmpty_196`, `tvEmptyTitle_196`, `tvEmptyMessage_196`, `fabAdd_196` |
| `item_book.xml` | `tvJudul_196`, `tvPengarang_196`, `tvTahun_196`, `btnDelete_196` |
| `activity_book_form.xml` | `etJudul_196`, `etPengarang_196`, `etTahun_196`, `btnSave_196`, `btnCancel_196` |

## Daftar modul & fungsi

### Autentikasi & Login

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

### Registrasi, Keamanan Password & Logout

File:
- `ui/RegisterActivity.kt`
- `util/PasswordUtils.kt`
- `data/entity/User.kt`
- `data/dao/UserDao.kt`
- `res/layout/activity_register.xml`

Fungsi/variabel:
- `User` → tabel `users_196`, kolom `id_196`, `username_196`, `password_196`
- `UserDao.insert()`, `findByUsername()`, `findById()`, `countByUsername()`
- `PasswordUtils.hash()`, `verify()`, `ALGORITHM_196`, `digest_196`, `bytes_196`
- `RegisterActivity.validate()`, `setLoading()`
- `RegisterActivity` field: `etUsername_196`, `etPassword_196`, `etPasswordConfirm_196`, `btnRegister_196`
- `EXTRA_USERNAME_196`, validasi username unik + konfirmasi password
- `DashboardActivity.confirmLogout_196()` + `btnLogout_196` — logout & hapus sesi

### Data Buku (Entity, DAO, Room)

File:
- `data/entity/Book.kt`
- `data/dao/BookDao.kt`
- `data/AppDatabase.kt`
- `app/schemas/.../1.json`

Fungsi/variabel:
- `Book` → tabel `books_196`, kolom `id_196`, `user_id_196`, `judul_196`, `pengarang_196`, `tahun_196`
- Foreign key `user_id_196` → `users_196.id_196`, `ON DELETE CASCADE`, index `user_id_196`
- `BookDao.observeBooksByUser()`, `searchBooks()`, `findById()`, `observeBookCount()`
- `BookDao.insert()`, `update()`, `delete()`, `deleteById()`
- `AppDatabase` + `DATABASE_NAME_196 = "utspab_196.db"`
- Semua query memfilter `user_id_196` (isolasi data)

### Dashboard & Statistik

File:
- `ui/DashboardActivity.kt`
- `res/layout/activity_dashboard.xml`

Fungsi/variabel:
- `DashboardActivity.onCreate()` — greeting user + navigasi
- `DashboardActivity.onResume()` — validasi sesi
- `userId_196`, `bookDao_196`
- `tvGreeting_196`, `tvBookCount_196`, `cardMyBooks_196`, `cardAddBook_196`
- Koleksi `observeBookCount()` real-time untuk statistik jumlah buku
- `goToLogin()` — redirect jika sesi tidak valid

### Daftar Buku, Pencarian, RecyclerView & Hapus

File:
- `ui/BookListActivity.kt`
- `ui/BookAdapter.kt`
- `res/layout/activity_book_list.xml`
- `res/layout/item_book.xml`

Fungsi/variabel:
- `BookAdapter` + `BookViewHolder.bind()` — render kartu buku
- `adapter_196`, `queryFlow_196`, `layoutEmpty_196`, `tvEmptyTitle_196`, `tvEmptyMessage_196`
- `BookListActivity.onCreate()` — pasang RecyclerView, search, FAB
- `updateEmptyState()` — state kosong vs hasil pencarian kosong
- `confirmDelete()` + `deleteBook()` — dialog hapus & eksekusi `deleteById()`
- Pencarian `flatMapLatest` ke `searchBooks()` (pengarang/tahun)
- `rvBooks_196`, `etSearch_196`, `fabAdd_196`, `tvJudul_196`, `tvPengarang_196`, `tvTahun_196`, `btnDelete_196`

### Form Tambah/Edit Buku & Validasi

File:
- `ui/BookFormActivity.kt`
- `res/layout/activity_book_form.xml`

Fungsi/variabel:
- `BookFormActivity.onCreate()` — mode tambah/edit dari `EXTRA_BOOK_ID_196`
- `loadBook()` — ambil buku berdasarkan `user_id_196` + `id_196`
- `saveBook()` — validasi judul, pengarang, tahun 4 digit; insert/update
- `setLoading()`, `goToLogin()`
- `newIntentForAdd()`, `newIntentForEdit()`
- `bookId_196`, `existingBook_196`, `etJudul_196`, `etPengarang_196`, `etTahun_196`, `btnSave_196`, `btnCancel_196`
- `EXTRA_BOOK_ID_196`

## Rekap fungsi

| NIM | Fungsi utama |
|-----|--------------|
| 102042300196 | `saveSession`, `isLoggedIn`, `getUserId`, `getUsername`, `logout`, `attemptLogin`, `hash`, `verify`, `validate`, `insert`, `findByUsername`, `findById`, `countByUsername`, `observeBooksByUser`, `searchBooks`, `observeBookCount`, `update`, `delete`, `deleteById`, `getInstance`, `onCreate`, `onResume`, `goToLogin`, `onCreateViewHolder`, `onBindViewHolder`, `bind`, `updateEmptyState`, `confirmDelete`, `deleteBook`, `loadBook`, `saveBook`, `setLoading`, `newIntentForAdd`, `newIntentForEdit` |
