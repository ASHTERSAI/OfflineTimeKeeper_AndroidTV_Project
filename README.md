# 📺 Offline Time Keeper v1.0 (Hybrid Smart Sync)

Rilis perdana aplikasi utilitas untuk **Android TV** dan **STB Android** guna mengatasi masalah tanggal/jam yang reset ke pengaturan pabrik setiap kali perangkat dimatikan total akibat baterai CMOS/RTC yang melemah[cite: 2].

Versi ini berfokus murni pada sinkronisasi waktu dua langkah tanpa mematikan aplikasi di latar belakang atau mengosongkan RAM sistem.

---

### 🔄 Alur Kerja Sistem (2 Langkah Cerdas):

1. **Langkah 1 (Saat Booting / Kondisi Offline):**
   * Saat TV menyala dengan kondisi jam reset pabrik, aplikasi langsung memulihkannya ke **catatan waktu terakhir sebelum TV dimatikan**[cite: 39].
   * **Tujuannya:** Menyelamatkan validitas sertifikat keamanan SSL/TLS agar jaringan Wi-Fi atau kabel LAN tidak memblokir akses internet.

2. **Langkah 2 (Begitu Sinyal Internet Masuk):**
   * Karena koneksi internet sudah tidak terhalang masalah sertifikat, TV berhasil online.
   * Aplikasi langsung mendeteksi jaringan via `NetworkCallback` dan menghubungi server NTP internasional (`pool.ntp.org` / `time.google.com`)[cite: 38].
   * Jam TV otomatis disinkronkan ke **jam dan tanggal asli yang presisi** hingga satuan detik[cite: 38].
   * Waktu resmi ini selanjutnya terus disimpan setiap 3 menit sebagai acuan berikutnya[cite: 38].

---

### ✨ Fitur Utama:

* **Hybrid Restore & Sync:** Menggabungkan pemulihan offline saat menyala dengan sinkronisasi NTP online otomatis saat terhubung internet[cite: 38, 39].
* **Auto-Save Berkala:** Menyimpan tanggal & jam sistem ke penyimpanan internal TV setiap 3 menit di latar belakang[cite: 38].
* **Proteksi Waktu Rusak:** Mencegah pencatatan waktu jika jam sistem masih berada di tahun bawaan pabrik (di bawah 2025).
* **Ringan & Aman:** Murni mengelola waktu sistem tanpa fitur pembersih RAM sehingga proses aplikasi lain tetap berjalan normal.
* **Dual Execution Mode:** Mendukung izin standar `SET_TIME` via ADB maupun hak akses Superuser (`su`) jika TV sudah di-root[cite: 2, 40].

---

### 📥 File Unduhan:

* **`app-debug.apk`**: File biner aplikasi siap pasang di Android TV / Android Box[cite: 2].

---

### 🛠️ Petunjuk Pemasangan (Hanya 1 Kali):

1. Pasang file APK di Android TV Anda menggunakan flashdisk[cite: 2].
2. Buka aplikasi **Offline Time Keeper** satu kali di layar TV[cite: 2, 37].
3. **Pemberian Izin Sistem:**
   * **TV Non-Root:** Hubungkan TV ke ADB lalu jalankan perintah[cite: 2]:
     ```bash
     adb shell pm grant com.example.offlinetimekeeper android.permission.SET_TIME
     ```
   * **TV Root (Magisk):** Tidak membutuhkan perintah ADB, aplikasi akan otomatis meminta hak akses Superuser (`su`)[cite: 2, 40].
4. Klik tombol **Simpan Waktu Sekarang** (pastikan jam TV saat itu sudah sesuai)[cite: 37].
5. Selesai! TV kini dapat digunakan dan dimatikan seperti biasa secara otomatis[cite: 2].
