package com.example.offlinetimekeeper

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#121212"))
            setPadding(64, 64, 64, 64)
        }

        val titleView = TextView(this).apply {
            text = "Offline Time Keeper (Android TV)"
            textSize = 26f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val statusView = TextView(this).apply {
            textSize = 18f
            setTextColor(Color.parseColor("#BBBBBB"))
            gravity = Gravity.CENTER
            setPadding(0, 32, 0, 48)
        }

        val btnSaveNow = Button(this).apply {
            text = "Simpan Waktu Sekarang"
            setOnClickListener {
                val now = System.currentTimeMillis()
                getSharedPreferences("TimePrefs", Context.MODE_PRIVATE)
                    .edit().putLong("last_time", now).apply()
                updateStatusText(statusView)
                Toast.makeText(this@MainActivity, "Waktu berhasil disimpan!", Toast.LENGTH_SHORT).show()
            }
        }

        val btnRestoreNow = Button(this).apply {
            text = "Tes Pulihkan Waktu Terakhir"
            setOnClickListener {
                val last = getSharedPreferences("TimePrefs", Context.MODE_PRIVATE).getLong("last_time", 0L)
                if (last > 0) {
                    val ok = TimeHelper.restoreSystemTime(last)
                    val msg = if (ok) "Berhasil memulihkan jam!" else "Gagal: Butuh izin ADB SET_TIME atau akses Root"
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                }
            }
        }

        layout.addView(titleView)
        layout.addView(statusView)
        layout.addView(btnSaveNow)
        layout.addView(btnRestoreNow)
        setContentView(layout)

        updateStatusText(statusView)

        val serviceIntent = Intent(this, TimeSaverService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun updateStatusText(tv: TextView) {
        val prefs = getSharedPreferences("TimePrefs", Context.MODE_PRIVATE)
        val lastTime = prefs.getLong("last_time", 0L)
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
        val lastStr = if (lastTime > 0) sdf.format(Date(lastTime)) else "Belum ada"
        val nowStr = sdf.format(Date(System.currentTimeMillis()))

        tv.text = "Jam Sistem Saat Ini: $nowStr\nWaktu Terakhir Tersimpan: $lastStr\n\nStatus Service: Aktif (Auto-Save tiap 3 menit)"
    }
}
