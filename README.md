# 📺 OfflineTimeKeeper_AndroidTV_Project

Solusi otomatis untuk perangkat **Android TV** (atau STB Android) yang selalu mengalami reset jam/tanggal ke pengaturan pabrik setiap kali dimatikan (akibat baterai CMOS/RTC lemah), yang menyebabkan koneksi internet/Wi-Fi gagal terhubung karena kegagalan validasi tanggal sertifikat jaringan.

Proyek ini bekerja dengan **menyimpan waktu sistem secara berkala ke memori internal** saat TV menyala, lalu **memulihkan waktu terakhir tersebut secara otomatis begitu TV dinyalakan kembali** sebelum layanan internet memeriksa koneksi.

---

## ✨ Fitur Utama

* **Auto-Save Berkala (Background Service):** Menyimpan waktu sistem (`last_time`) ke `SharedPreferences` setiap 3 menit sekali secara otomatis di latar belakang.
* **Proteksi Tahun Reset:** Dilengkapi filter validasi (`Tahun >= 2025`) agar aplikasi tidak sengaja menimpa simpanan waktu yang benar dengan waktu reset pabrik saat TV baru menyala.
* **Auto-Restore saat Booting (`BootReceiver`):** Mendeteksi event `BOOT_COMPLETED` dan `QUICKBOOT_POWERON`, lalu langsung memulihkan jam sistem ditambah kompensasi waktu booting (+30 detik).
* **Dual-Method Time Setter (`TimeHelper`):**
  1. Menggunakan API standar Android (`SystemClock.setCurrentTimeMillis`) dengan izin `SET_TIME` via ADB.
  2. Otomatis *fallback* menggunakan perintah shell Root (`su -c date MMddHHmmyyyy.ss`) jika perangkat sudah berstatus Root.
* **Antarmuka Ramah Remote TV:**Mendukung `LEANBACK_LAUNCHER` dengan tombol uji coba manual (*Simpan Waktu Sekarang* & *Tes Pulihkan Waktu Terakhir*).
* **GitHub Actions CI/CD:** Sudah dilengkapi workflow otomatis untuk meng-compile kode sumber menjadi file `.apk` langsung di server GitHub tanpa perlu menginstal Android Studio.

---

## 📂 Struktur Proyek

* `app/src/main/java/com/example/offlinetimekeeper/` — Kode sumber utama aplikasi Android berbasis Kotlin (`MainActivity.kt`, `TimeSaverService.kt`, `BootReceiver.kt`, `TimeHelper.kt`).
* `.github/workflows/build_apk.yml` — Konfigurasi GitHub Actions untuk mem-build `app-debug.apk` secara otomatis di cloud.
* `auto_timekeeper_tv.sh` — Skrip shell mandiri (alternatif tanpa APK) untuk pengguna Root / Magisk (`service.d`) atau Local ADB.

---

## 🚀 Cara Mendapatkan File `.apk` (Via GitHub Actions)

1. Buka tab **Actions** di bagian atas repository GitHub ini.
2. Pilih workflow **Build OfflineTimeKeeper APK** di menu sebelah kiri.
3. Klik tombol **Run workflow** (atau tunggu proses build otomatis selesai setelah melakukan *push*).
4. Setelah status berubah menjadi centang hijau (sekitar 1–2 menit), buka detail prosesnya dan unduh file **`OfflineTimeKeeper-APK`** pada bagian **Artifacts**.
5. Ekstrak file ZIP tersebut untuk mendapatkan `app-debug.apk`.

---

## 🛠️ Cara Instalasi & Penggunaan

### Opsi A: Menggunakan Aplikasi (`.apk`)
1. Instal `app-debug.apk` ke Android TV Anda.
2. **Untuk TV Non-Root:** Berikan izin sistem `SET_TIME` satu kali saja melalui ADB (dari PC atau HP):
   ```bash
   adb shell pm grant com.example.offlinetimekeeper android.permission.SET_TIME
