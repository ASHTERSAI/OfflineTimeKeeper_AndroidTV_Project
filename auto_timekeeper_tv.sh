#!/system/bin/sh
# ==============================================================================
# SKRIP OTOMATIS PENYIMPAN & PEMULIH JAM ANDROID TV (TANPA PERLU COMPILE APK)
# Bisa dijalankan via ADB / Termux / atau ditaruh di /data/adb/service.d/ (Magisk)
# ==============================================================================

SAVE_FILE="/sdcard/tv_last_time.txt"

# Tunggu sistem selesai booting
while [ "$(getprop sys.boot_completed)" != "1" ]; do
    sleep 2
done

# 1. Saat TV baru menyala, cek apakah tahun saat ini mereset (di bawah 2025)
CURRENT_YEAR=$(date +%Y)
if [ "$CURRENT_YEAR" -lt 2025 ] && [ -f "$SAVE_FILE" ]; then
    SAVED_TIME=$(cat "$SAVE_FILE")
    if [ -n "$SAVED_TIME" ]; then
        # Pulihkan waktu terakhir
        date "$SAVED_TIME"
    fi
fi

# 2. Loop di latar belakang: Simpan waktu setiap 3 menit (180 detik)
while true; do
    YEAR_NOW=$(date +%Y)
    if [ "$YEAR_NOW" -ge 2025 ]; then
        # Format MMddHHmmyyyy.ss untuk command date Android
        date +%m%d%H%M%Y.%S > "$SAVE_FILE"
    fi
    sleep 180
done
