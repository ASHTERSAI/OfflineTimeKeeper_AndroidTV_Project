package com.example.offlinetimekeeper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.Timer
import java.util.TimerTask

class TimeSaverService : Service() {
    private var timer: Timer? = null
    private val CHANNEL_ID = "TimeKeeperChannel"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Offline Time Keeper Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)

            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Offline Time Keeper Aktif")
                .setContentText("Menyimpan jam TV secara otomatis setiap 3 menit")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .build()

            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer?.cancel()
        timer = Timer()
        // Simpan waktu setiap 3 menit (180.000 ms)
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val now = System.currentTimeMillis()
                // Hanya simpan jika tahun >= 2025 (mencegah menyimpan jam yang salah/reset)
                if (now > 1735689600000L) {
                    val prefs = getSharedPreferences("TimePrefs", Context.MODE_PRIVATE)
                    prefs.edit().putLong("last_time", now).apply()
                }
            }
        }, 0, 3 * 60 * 1000L)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}
