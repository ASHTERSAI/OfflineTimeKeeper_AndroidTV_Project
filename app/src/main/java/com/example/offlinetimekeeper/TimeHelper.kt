package com.example.offlinetimekeeper

import android.os.SystemClock
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeHelper {
    fun restoreSystemTime(timeMillis: Long): Boolean {
        // Metode 1: Via SystemClock API (Jika sudah di-grant via ADB pm grant)
        try {
            val success = SystemClock.setCurrentTimeMillis(timeMillis)
            if (success) {
                Log.d("TimeKeeper", "Berhasil set jam via SystemClock API")
                return true
            }
        } catch (e: Exception) {
            Log.w("TimeKeeper", "SystemClock gagal: ${e.message}")
        }

        // Metode 2: Via Root Shell (su -c date MMddHHmmyyyy.ss) jika TV berstatus Root
        try {
            val sdf = SimpleDateFormat("MMddHHmmyyyy.ss", Locale.US)
            val dateStr = sdf.format(Date(timeMillis))
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "date $dateStr"))
            val exitCode = process.waitFor()
            if (exitCode == 0) {
                Log.d("TimeKeeper", "Berhasil set jam via Root su date")
                return true
            }
        } catch (e: Exception) {
            Log.e("TimeKeeper", "Root shell gagal: ${e.message}")
        }
        return false
    }
}
