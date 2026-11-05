# 📺 OfflineTimeKeeper_AndroidTV_Project

Solusi otomatis dan mandiri untuk perangkat **Android TV** atau **STB Android** yang selalu mengalami reset jam/tanggal ke pengaturan pabrik setiap kali dimatikan (akibat baterai CMOS/RTC yang melemah), yang menyebabkan koneksi internet Wi-Fi/LAN gagal terhubung akibat penolakan sertifikat validasi SSL/TLS.

Proyek ini bekerja dengan sistem **Hybrid Smart Sync**: memulihkan waktu terakhir secara instan begitu TV dinyalakan (*offline boot restore*) agar internet bisa masuk, lalu secara otomatis menyinkronkan jam dan tanggal ke waktu presisi resmi dari server NTP internet begitu sinyal online terdeteksi—tanpa mematikan proses latar belakang atau mengosongkan RAM aplikasi lain.

---

## ✨ Fitur Utama

* **Auto-Save Berkala (Background Service):** Menyimpan waktu sistem (`last_time`) ke `SharedPreferences` setiap 3 menit sekali secara otomatis di latar belakang.
* **Proteksi Tahun Reset:** Dilengkapi filter validasi (Tahun >= 2025) agar aplikasi tidak sengaja menimpa data jam valid dengan tanggal reset bawaan pabrik saat TV baru menyala.
* **Auto-Restore saat Booting (`BootReceiver`):** Mendeteksi sinyal sistem `BOOT_COMPLETED` dan `QUICKBOOT_POWERON`, lalu langsung memulihkan jam sistem ditambah kompensasi durasi booting (+30 detik) sebelum layanan jaringan memeriksa sertifikat.
* **Smart Online NTP Sync (`SntpHelper`):** Memantau status jaringan secara *real-time* via `ConnectivityManager.NetworkCallback`. Begitu TV terhubung ke sinyal internet (Wi-Fi atau LAN), aplikasi langsung mengambil data waktu terkini dari server NTP internasional (`pool.ntp.org`, `time.google.com`, `id.pool.ntp.org`) dan menyetel detik jam secara presisi.
* **Dual-Method Time Setter (`TimeHelper`):**
  * Menggunakan API standar Android (`SystemClock.setCurrentTimeMillis`) melalui izin `SET_TIME` via ADB.
  * Otomatis *fallback* menggunakan perintah shell Superuser (`su -c date`) jika perangkat sudah dalam keadaan Root (Magisk).
* **Antarmuka Ramah Remote TV:** Tampilan minimalis berlatar gelap yang mendukung remote TV (`LEANBACK_LAUNCHER`), lengkap dengan tombol manual: *Simpan Waktu Sekarang* dan *Sinkronkan Jam Online Sekarang*.
* **GitHub Actions CI/CD:** Menggunakan alur kerja cloud otomatis (`build_apk.yml`) dengan JDK 17 dan Gradle 8.4 terbaru untuk merakit kode sumber langsung menjadi file `.apk` tanpa perlu menginstal Android Studio di laptop.

---

## 📂 Struktur Proyek

* `.github/workflows/build_apk.yml` — Skrip otomatisasi GitHub Actions untuk mengompilasi APK debug di server cloud.
* `app/src/main/AndroidManifest.xml` — Konfigurasi izin sistem (`SET_TIME`, `RECEIVE_BOOT_COMPLETED`, `INTERNET`, `ACCESS_NETWORK_STATE`).
* `app/src/main/java/com/example/offlinetimekeeper/`
  * `MainActivity.kt` — Antarmuka pengguna di layar TV untuk memantau status dan pemicu manual.
  * `BootReceiver.kt` — Penerima sinyal booting TV untuk memulihkan jam offline pertama kali.
  * `TimeSaverService.kt` — Layanan latar belakang untuk penyimpanan rutin tiap 3 menit dan pendengar sinyal internet.
  * `TimeHelper.kt` — Eksekutor pengatur jam sistem (metode API Android dan Root shell).
  * `SntpHelper.kt` — Modul klien SNTP UDP untuk mengambil waktu online resmi dari server NTP.

---

## 🚀 Cara Mendapatkan File `.apk` (Via GitHub Actions)

1. Buka tab **Actions** di bilah menu bagian atas repositori GitHub ini.
2. Klik proses build terbaru yang bertanda **centang hijau (✅)**.
3. Gulir layar ke bagian paling bawah ke tabel **Artifacts**.
4. Klik tautan file biner yang tersedia (misalnya: `OfflineTimeKeeper-NoRAM-APK` atau `OfflineTimeKeeper-Standard-APK`) untuk mengunduh arsip zip.
5. Ekstrak file zip tersebut di laptop/komputer untuk mendapatkan file instalasi **`app-debug.apk`**.

---

## 🛠️ Cara Instalasi & Penggunaan di Android TV

1. Salin file **`app-debug.apk`** ke flashdisk, lalu colokkan dan instal di Android TV / STB Anda menggunakan aplikasi File Manager.
2. **Pemberian Izin Sistem (Hanya dilakukan 1 kali saja):**
   * **Untuk TV Non-Root:** Hubungkan TV ke laptop atau HP via ADB (kabel USB atau Wi-Fi ADB), lalu ketik perintah:
     ```bash
     adb shell pm grant com.example.offlinetimekeeper android.permission.SET_TIME
     ```
   * **Untuk TV yang Sudah di-Root (Magisk):** Tidak memerlukan perintah ADB di atas. Aplikasi akan otomatis meminta konfirmasi izin Superuser (`su`) saat pertama kali berjalan.
3. Buka aplikasi **Offline Time Keeper** satu kali di menu aplikasi TV Anda.
4. Klik tombol **Simpan Waktu Sekarang** (pastikan jam TV saat itu sudah benar).
5. Selesai! TV kini siap digunakan seperti biasa. Setiap kali TV dimatikan total dan dinyalakan kembali, jam akan langsung terpulihkan secara otomatis, lalu disinkronkan ke waktu online terkini begitu internet terhubung.

---

## 📜 Lisensi

Proyek ini dirilis di bawah lisensi **MIT License** — bebas digunakan, dimodifikasi, dan didistribusikan untuk kebutuhan pribadi maupun komunitas.
