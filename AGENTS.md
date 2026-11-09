# AGENTS.md — Perpustakaan Pribadi (UTS PAB)

Panduan untuk AI coding agent yang bekerja di repository ini.

## 1. Ringkasan project

Aplikasi Android native sederhana untuk mengelola koleksi buku pribadi.
Semua data lokal (offline), tanpa API/internet.

- **Tujuan akademik**: UTS Pemrograman Aplikasi Bergerak (PAB).
- **Fitur**: register, login, sesi login, logout, dashboard, CRUD buku,
  pencarian buku berdasarkan pengarang/tahun, isolasi data per user.
- **Bahasa UI**: Indonesia.
- **Status**: build sukses (`assembleDebug`), unit test lulus, belum diuji di
  emulator/device (tidak ada device terhubung saat build).

Dokumen pendukung:
- `README.md` — dokumentasi fitur & cara menjalankan untuk manusia.
- `PEMBAGIAN_TUGAS.md` — peta suffix NIM pada seluruh penanda project.

## 2. Stack & versi

| Komponen | Versi / keterangan |
|---|---|
| Bahasa | Kotlin (built-in Kotlin dari AGP, **tanpa** plugin `kotlin-android`) |
| AGP | 9.4.1 (`com.android.application`) |
| Gradle | 9.6.0 (wrapper) |
| compileSdk / targetSdk | 37 |
| minSdk | 28 |
| Java/Kotlin target | Java 11 |
| Room | 2.8.5 (runtime, ktx, compiler) |
| KSP | 2.3.13 (`com.google.devtools.ksp`) |
| UI | XML + Material Components 1.14.0 + RecyclerView 1.4.0 |
| Async | Kotlin Coroutines 1.11.0 + Flow |
| Lifecycle | `lifecycle-runtime-ktx` 2.11.0 |
| Test | JUnit 4 + Espresso (template) |

Versi dependency didefinisikan di `gradle/libs.versions.toml`.
Jangan menambah plugin Kotlin terpisah: AGP 9 sudah memiliki built-in Kotlin.

## 3. Cara build & test

JDK tidak ada di `PATH`. Gunakan JBR bawaan Android Studio:

```powershell
$env:JAVA_HOME = "E:\Android Studio\jbr"
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:testDebugUnitTest
```

- APK: `app/build/outputs/apk/debug/app-debug.apk`
- Schema Room: `app/schemas/com.example.utspabkelompok2.data.AppDatabase/1.json`
- Build memakai configuration cache (`gradle.properties`), jadi jangan heran
  bila task terasa incremental.
- **Definition of done** setiap perubahan: `assembleDebug` sukses dan
  `testDebugUnitTest` sukses.

## 4. Struktur file penting

```
app/src/main/java/com/example/utspabkelompok2/
├── data/
│   ├── AppDatabase.kt          # Room database, singleton, nama DB
│   ├── dao/
│   │   ├── UserDao.kt          # query users_196
│   │   └── BookDao.kt          # query books_196 + isolasi user_id_196
│   └── entity/
│       ├── User.kt             # tabel users_196
│       └── Book.kt             # tabel books_196 + FK
├── ui/
│   ├── LoginActivity.kt        # login + cek sesi
│   ├── RegisterActivity.kt     # register + validasi
│   ├── DashboardActivity.kt    # sapaan, statistik, navigasi, logout
│   ├── BookListActivity.kt     # list, search, delete
│   ├── BookAdapter.kt          # ListAdapter + DiffUtil
│   └── BookFormActivity.kt     # tambah/edit + validasi
└── util/
    ├── SessionManager.kt       # SharedPreferences "session_196"
    └── PasswordUtils.kt        # SHA-256

app/src/main/res/layout/
├── activity_login.xml
├── activity_register.xml
├── activity_dashboard.xml
├── activity_book_list.xml
├── item_book.xml
└── activity_book_form.xml
```

Manifest: `app/src/main/AndroidManifest.xml` (launcher = `LoginActivity`).

## 5. Arsitektur & pola kode

- **Pola**: Activity + DAO langsung (tanpa ViewModel, Repository, Hilt, atau
  Navigation Component). Jangan menambahkan arsitektur baru kecuali diminta.
- **View binding**: project memakai `findViewById`, bukan ViewBinding/DataBinding.
  Ikuti pola yang ada.
- **Layout**: XML dengan Material Components
  (`MaterialCardView`, `TextInputLayout`, `MaterialButton`,
  `ExtendedFloatingActionButton`). Tema:
  `Theme.MaterialComponents.DayNight.DarkActionBar` di
  `res/values/themes.xml` dan `values-night/themes.xml`.
- **Akses DB**: `AppDatabase.getInstance(context)` → `userDao()`/`bookDao()`.
  DAO `suspend` dipanggil dari `lifecycleScope.launch`. Flow dikoleksi dengan
  `repeatOnLifecycle(Lifecycle.State.STARTED)`.
- **State pencarian**: `MutableStateFlow<String>` + `flatMapLatest` di
  `BookListActivity`. Query kosong → `observeBooksByUser`, selain itu →
  `searchBooks`.
- **Password**: hash SHA-256 (`PasswordUtils`), bukan plaintext. Jangan ubah
  tanpa alasan keamanan yang jelas.

## 6. Database (Room)

Nama file: **`utspab_196.db`** (`DATABASE_NAME_196`), versi **1**,
`exportSchema = true`, `fallbackToDestructiveMigration(dropAllTables = true)`.

```sql
users_196(
  id_196 INTEGER PK AUTOINCREMENT,
  username_196 TEXT NOT NULL UNIQUE,
  password_196 TEXT NOT NULL
)

books_196(
  id_196 INTEGER PK AUTOINCREMENT,
  user_id_196 INTEGER NOT NULL,
  judul_196 TEXT NOT NULL,
  pengarang_196 TEXT NOT NULL,
  tahun_196 INTEGER NOT NULL,
  FOREIGN KEY(user_id_196) REFERENCES users_196(id_196) ON DELETE CASCADE
)
INDEX index_books_196_user_id_196 ON books_196(user_id_196)
```

Kotlin property tetap memakai nama bersih (`id`, `userId`, `judul`, ...);
nama tabel/kolom fisik diatur lewat `@Entity(tableName=...)` dan
`@ColumnInfo(name=...)`.

**Aturan isolasi data (wajib dipertahankan):**
- Semua query baca/tulis/hapus buku harus memfilter `user_id_196`.
- `BookDao.findById` dan `deleteById` menerima `userId` + `bookId`.
- Update buku di `BookFormActivity` memakai objek hasil `findById(userId, id)`
  agar user tidak bisa mengubah buku user lain.
- Jangan pernah mengambil `user_id` dari input UI; selalu dari `SessionManager`.

**Perubahan skema:**
1. Ubah entity/DAO.
2. Naikkan `@Database(version = ...)`.
3. Build; schema JSON baru akan dibuat.
4. `fallbackToDestructiveMigration` akan mereset data lokal (aplikasi akademik,
   data seed tidak diperlukan).

## 7. Autentikasi, sesi & navigasi

Alur:

```
Login ──► Dashboard ──► Daftar Buku ──► Form Tambah
  ▲            │              │
  │            │              └──► Form Edit (ketuk kartu)
  └────────────┴── Logout ────┴──► Hapus (dialog konfirmasi)
```

- `LoginActivity` adalah launcher. Di `onCreate`, jika `SessionManager`
  menunjukkan sudah login, langsung ke `DashboardActivity`.
- Register sukses → kembali ke login membawa `EXTRA_USERNAME_196` (prefill).
- Logout: dialog konfirmasi → `SessionManager.logout()` → hapus task stack →
  `LoginActivity`.
- Setiap Activity memvalidasi sesi di awal (`isLoggedIn()` dan
  `getUserId() != NO_USER_196`); jika tidak valid, redirect ke login.
- Password disimpan sebagai hash; sesi hanya menyimpan `user_id` dan
  `username` di SharedPreferences.

## 8. Konvensi suffix NIM (penting!)

Seluruh penanda project menggunakan **satu NIM: 102042300196** dengan suffix
`196`. **Jangan menghapus atau mengganti suffix ini** saat refactor; itu bagian
dari bukti kepemilikan tugas.

| Suffix | NIM | Cakupan |
|---|---|---|
| `196` | 102042300196 | Seluruh tabel, kolom, SharedPreferences, ID view XML, dan field/konstanta Kotlin milik project |

Detail lengkap ada di `PEMBAGIAN_TUGAS.md`.

Aturan penamaan:
- Suffix berada di **akhir** identifier: `session_196`, `book_id_196`,
  `etJudul_196`, `tvBookCount_196`.
- Suffix diterapkan pada identitas buatan sendiri: tabel, kolom, nama/key
  SharedPreferences, ID view XML, field/konstanta Kotlin.
- **Jangan** memberi suffix pada nama framework/API (`onCreate`, `Intent`,
  `R`, `Flow`, `RecyclerView`, dsb.) atau nama class inti
  (`LoginActivity`, `BookDao`, dst.).
- Menambah ID view baru: gunakan suffix `196`, dan pastikan unik secara global
  (ID resource Android bersifat global).
- Menambah field/variabel baru: gunakan suffix `196`.

## 9. Detail perilaku UI

- `BookListActivity`:
  - Search field memfilter langsung (Flow).
  - Empty state membedakan "belum ada buku" vs "hasil pencarian kosong".
  - Ketuk kartu → edit; tombol Hapus → dialog konfirmasi.
  - FAB → form tambah.
- `BookFormActivity`:
  - Mode ditentukan `EXTRA_BOOK_ID_196` (> 0 = edit).
  - Validasi: judul wajib, pengarang wajib, tahun wajib 4 digit (1000–2100).
  - Tombol simpan dinonaktifkan saat proses simpan (teks "Menyimpan...").
- `DashboardActivity`:
  - Menampilkan `Halo, <username>!` dan jumlah buku real-time.
  - Statistik memakai `observeBookCount(userId)`.
- Beberapa teks status ("Memuat...", "Memproses...", "Menyimpan...",
  "Berhasil keluar", validasi username minimal) masih hardcoded di Kotlin.
  Jika menambah string baru, idealnya taruh di `res/values/strings.xml`.

## 10. Testing & verifikasi

- Unit test: `app/src/test/java/com/example/utspabkelompok2/PasswordUtilsTest.kt`
  (deterministik hash, bukan plaintext, verifikasi password).
- Test template: `ExampleUnitTest`, `ExampleInstrumentedTest` (tidak diubah).
- Belum ada test DAO/Room (butuh instrumentation + emulator).
- Jika mengubah skema/query, minimal jalankan `assembleDebug`; KSP akan
  memvalidasi query Room saat compile.
- Tidak ada emulator pada environment ini; jangan mengklaim sudah diuji runtime
  kecuali device benar-benar terhubung (`adb devices`).

## 11. Lokasi data saat runtime

```
/data/data/com.example.utspabkelompok2/databases/utspab_196.db
/data/data/com.example.utspabkelompok2/databases/utspab_196.db-wal
/data/data/com.example.utspabkelompok2/databases/utspab_196.db-shm
```

SharedPreferences:
```
/data/data/com.example.utspabkelompok2/shared_prefs/session_196.xml
```

Akses debug:
```powershell
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
& $adb shell run-as com.example.utspabkelompok2 ls databases
& $adb exec-out run-as com.example.utspabkelompok2 cat databases/utspab_196.db > utspab_196.db
```

Atau gunakan **App Inspection → Database Inspector** di Android Studio.

## 12. Yang harus dihindari

- Jangan menambahkan permission internet/API jaringan — project sengaja offline.
- Jangan menghapus filter `user_id_196` dari query apa pun.
- Jangan menyimpan password plaintext.
- Jangan mengedit `app/build/` atau `app/schemas/` secara manual.
- Jangan memakai `fallbackToDestructiveMigration` sebagai alasan untuk
  menghapus validasi/migrasi tanpa berpikir; ini hanya untuk pengembangan.
- Jangan rename ID XML atau tabel tanpa memperbarui semua referensi Kotlin/DAO
  dan menjalankan build.
- Jangan mengubah suffix NIM pada identifier yang sudah ada.
