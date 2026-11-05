package com.example.offlinetimekeeper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Timer
import java.util.TimerTask

class TimeSaverService : Service() {
    private var timer: Timer? = null
    private val CHANNEL_ID = "TimeKeeperChannel"
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

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
                .setContentText("Memantau jam sistem & sinkronisasi otomatis")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .build()

            startForeground(1, notification)
        }

        registerNetworkListener()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer?.cancel()
        timer = Timer()
        // Simpan jam lokal tiap 3 menit
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val now = System.currentTimeMillis()
                if (now > 1735689600000L) {
                    val prefs = getSharedPreferences("TimePrefs", Context.MODE_PRIVATE)
                    prefs.edit().putLong("last_time", now).apply()
                }
            }
        }, 0, 3 * 60 * 1000L)

        triggerOnlineSync()

        return START_STICKY
    }

    private fun registerNetworkListener() {
        try {
            connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.d("TimeKeeper", "Internet terdeteksi, sinkronkan ke NTP...")
                    triggerOnlineSync()
                }
            }
            connectivityManager?.registerNetworkCallback(request, networkCallback!!)
        } catch (e: Exception) {
            Log.e("TimeKeeper", "Gagal register network callback: ${e.message}")
        }
    }

    private fun triggerOnlineSync() {
        Thread {
            try {
                // Beri jeda 3 detik agar sambungan internet stabil
                Thread.sleep(3000)
                val ntpServers = listOf("pool.ntp.org", "time.google.com", "id.pool.ntp.org")
                var onlineTime: Long? = null

                for (server in ntpServers) {
                    onlineTime = SntpHelper.getNetworkTime(server, 4000)
                    if (onlineTime != null) {
                        break
                    }
                }

                if (onlineTime != null && onlineTime > 1735689600000L) {
                    TimeHelper.restoreSystemTime(onlineTime)
                    val prefs = getSharedPreferences("TimePrefs", Context.MODE_PRIVATE)
                    prefs.edit().putLong("last_time", onlineTime).apply()
                    Log.d("TimeKeeper", "Jam TV berhasil disinkronkan ke waktu online!")
                }
            } catch (e: Exception) {
                Log.e("TimeKeeper", "Sinkronisasi gagal: ${e.message}")
            }
        }.start()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        timer?.cancel()
        networkCallback?.let { connectivityManager?.unregisterNetworkCallback(it) }
        super.onDestroy()
    }
}
