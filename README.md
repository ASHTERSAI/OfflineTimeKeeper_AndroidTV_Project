# 📺 OfflineTimeKeeper_AndroidTV_Project

Solusi otomatis dan mandiri untuk perangkat **Android TV** atau **STB Android** yang selalu mengalami reset jam/tanggal ke pengaturan pabrik setiap kali dimatikan total akibat baterai CMOS/RTC yang melemah[cite: 2], yang menyebabkan internet Wi-Fi/LAN gagal tersambung akibat penolakan sertifikat keamanan SSL/TLS jaringan.

---

## 🔄 Bagaimana Cara Kerjanya? (Sistem 2 Langkah Cerdas)

Aplikasi ini menggunakan alur **Hybrid Smart Sync** dua tahap agar TV langsung bisa online secara mandiri:

1. **Langkah 1 (Saat Booting / Offline):**
   * TV menyala dengan kondisi jam reset ke tahun pabrik (misalnya 1 Januari 1970)[cite: 2].
   * Aplikasi langsung memulihkan jam reset tersebut ke **jam terakhir yang tersimpan** sebelum TV dimatikan[cite: 39].
   * **Tujuannya:** Menyelamatkan validitas sertifikat SSL/TLS sistem Android agar Wi-Fi/kabel LAN tidak menolak sambungan internet akibat tahun yang kedaluwarsa.

2. **Langkah 2 (Begitu Sinyal Internet Masuk):**
   * Karena internet sudah tidak terblokir sertifikat, TV berhasil mendapatkan sinyal online.
   * Aplikasi langsung mendeteksi jaringan dan menghubungi server waktu resmi dunia via NTP (`pool.ntp.org` / `time.google.com`)[cite: 38].
   * Jam TV otomatis ditimpa kembali dan disesuaikan ke **jam dan tanggal asli yang presisi** hingga satuan detik[cite: 38].
   * Jam asli ini selanjutnya akan terus dicatat otomatis setiap 3 menit sekali sebagai cadangan saat TV dimatikan kembali[cite: 38].

---

## ✨ Fitur Utama

* **Pencatat Waktu Berkala:** Menyimpan tanggal & jam ke memori TV setiap 3 menit di latar belakang[cite: 38].
* **Proteksi Tanggal Rusak:** Memiliki filter validasi agar jam yang sedang reset tidak sengaja menimpa catatan waktu yang valid.
* **Auto-Restore saat Booting (`BootReceiver`):** Mengembalikan waktu terakhir + kompensasi waktu booting (+30 detik) secara instan saat TV dinyalakan[cite: 39].
* **Smart Online NTP Sync (`SntpHelper`):** Sinkronisasi otomatis begitu internet aktif menggunakan server waktu NTP internasional[cite: 38].
* **Dual Execution Mode (`TimeHelper`):** Mendukung izin standar `SET_TIME` via ADB (Non-Root) maupun izin Superuser (`su`) otomatis jika TV sudah di-root[cite: 2, 40].
* **Ringan & Stabil:** Murni mengatur sinkronisasi jam tanpa mengganggu atau mematikan proses aplikasi lain di RAM.
* **GitHub Actions CI/CD:** Dilengkapi skrip otomatis untuk merakit kode sumber menjadi file `.apk` langsung di server cloud tanpa perlu instal Android Studio di laptop[cite: 2].

---

## 📂 Struktur Proyek

* `.github/workflows/build_apk.yml` — Konfigurasi build APK otomatis di GitHub Actions.
* `app/src/main/AndroidManifest.xml` — Konfigurasi izin sistem (`SET_TIME`, `RECEIVE_BOOT_COMPLETED`, `INTERNET`, `ACCESS_NETWORK_STATE`)[cite: 36].
* `app/src/main/java/com/example/offlinetimekeeper/`
  * `MainActivity.kt` — Antarmuka pengguna TV dengan tombol simpan dan sinkronisasi manual[cite: 37].
  * `BootReceiver.kt` — Memulihkan waktu offline saat booting[cite: 39].
  * `TimeSaverService.kt` — Layanan latar belakang untuk pencatatan berkala dan pendeteksi internet[cite: 38].
  * `TimeHelper.kt` — Modul eksekusi waktu (API Android & Root shell)[cite: 40].
  * `SntpHelper.kt` — Modul klien NTP untuk mengambil data waktu resmi dari internet[cite: 41].

---

## 🚀 Cara Mengunduh File `.apk` (Via GitHub Actions)

1. Buka tab **Actions** di bagian atas repositori ini[cite: 2].
2. Klik proses build terbaru yang bertanda **centang hijau (✅)**[cite: 2].
3. Gulir layar ke bagian paling bawah ke tabel **Artifacts**[cite: 2].
4. Klik tautan file yang muncul (misalnya: `OfflineTimeKeeper-NoRAM-APK` atau `OfflineTimeKeeper-Standard-APK`) untuk mengunduh arsip zip[cite: 2].
5. Ekstrak file zip tersebut di laptop untuk mendapatkan file instalasi **`OfflineTimeKeeper_With_RamCleaner_Project.apk`**[cite: 2].

---

## 🛠️ Panduan Pemasangan di Android TV

1. Pindahkan file **`OfflineTimeKeeper_With_RamCleaner_Project.apk`** ke flashdisk, lalu pasang di TV Anda menggunakan aplikasi File Manager[cite: 2].
2. **Pemberian Izin Sistem (Hanya dilakukan 1 kali saja):**
   * **Untuk TV Non-Root:** Hubungkan TV ke laptop via ADB, lalu ketik perintah[cite: 2]:
     ```bash
     adb shell pm grant com.example.offlinetimekeeper android.permission.SET_TIME
     ```
   * **Untuk TV yang Sudah di-Root (Magisk):** Tidak memerlukan perintah ADB[cite: 2, 40]. Aplikasi otomatis meminta hak akses Superuser (`su`)[cite: 40].
3. Buka aplikasi **Offline Time Keeper** 1 kali di layar TV[cite: 2, 37].
4. Klik tombol **Simpan Waktu Sekarang** (pastikan jam TV saat itu sudah dalam kondisi benar)[cite: 37].
5. Selesai! TV kini dapat digunakan dan dimatikan seperti biasa secara otomatis[cite: 2].

---

## 📜 Lisensi

Proyek ini dirilis di bawah lisensi **MIT License** — bebas digunakan dan dimodifikasi untuk kebutuhan pribadi maupun komunitas.
