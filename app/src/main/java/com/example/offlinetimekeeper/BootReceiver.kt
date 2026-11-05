package com.example.offlinetimekeeper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action == "android.intent.action.QUICKBOOT_POWERON") {
            val prefs = context.getSharedPreferences("TimePrefs", Context.MODE_PRIVATE)
            val lastSavedTime = prefs.getLong("last_time", 0L)

            // Jika waktu sistem saat ini lebih lama dari waktu terakhir disimpan (reset ke masa lalu)
            if (lastSavedTime > 0 && System.currentTimeMillis() < lastSavedTime) {
                // Tambah 30 detik sebagai kompensasi waktu booting TV
                val targetTime = lastSavedTime + 30000L
                TimeHelper.restoreSystemTime(targetTime)
            }

            val serviceIntent = Intent(context, TimeSaverService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }
}
