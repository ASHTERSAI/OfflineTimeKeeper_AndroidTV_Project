# Offline Time Keeper - Android TV

Paket ini berisi **2 solusi siap pakai** untuk mengatasi jam Android TV yang selalu reset saat dimatikan:

## Cara 1: Compile menjadi `.apk` Tanpa Install Android Studio (Via GitHub Actions - Gratis & Otomatis)
Karena laptop tidak perlu diinstal Android Studio yang berat, proyek ini sudah dilengkapi robot **GitHub Actions** (`.github/workflows/build_apk.yml`):
1. Buat repository baru di akun GitHub Anda, lalu upload seluruh isi folder ini.
2. Klik menu **Actions** di repository GitHub Anda, lalu jalankan workflow **Build OfflineTimeKeeper APK**.
3. Tunggu ~2 menit, lalu download file **`OfflineTimeKeeper-APK.zip`** (berisi `app-debug.apk` siap instal).
4. Setelah diinstal ke TV, jalankan perintah ADB ini 1 kali:
   ```bash
   adb shell pm grant com.example.offlinetimekeeper android.permission.SET_TIME
   ```
   *(Catatan: Jika TV sudah Root, aplikasi otomatis menggunakan akses `su` tanpa perlu perintah ADB).*

## Cara 2: Langsung Pakai Tanpa Perlu APK (`auto_timekeeper_tv.sh`)
Jika TV Anda sudah memiliki akses Root (seperti Magisk) atau menggunakan Local ADB Shell:
- Cukup salin file `auto_timekeeper_tv.sh` ke `/data/adb/service.d/auto_timekeeper_tv.sh` lalu beri izin eksekusi (`chmod +x`).
- Skrip akan otomatis bekerja setiap TV menyala tanpa perlu menginstal aplikasi apa pun!
