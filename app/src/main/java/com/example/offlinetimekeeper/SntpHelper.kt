package com.example.offlinetimekeeper

import android.os.SystemClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object SntpHelper {

    private const val NTP_PORT = 123
    private const val NTP_TIMEOUT_MS = 5000 // Batas waktu tunggu respons (5 detik)
    private const val NTP_MODE_CLIENT = 3
    private const val NTP_VERSION = 3

    // Selisih antara era NTP (1900) dan UNIX epoch (1970) dalam detik
    private const val OFFSET_1900_TO_1970 = 2208988800L

    // Daftar server NTP publik yang andal
    private val NTP_SERVERS = listOf(
        "pool.ntp.org",
        "id.pool.ntp.org",
        "time.google.com",
        "time.cloudflare.com"
    )

    /**
     * Mengambil waktu aktual (Epoch Millis) dari server NTP secara asynchronous.
     * Mengembalikan null jika semua server gagal dihubungi/koneksi bermasalah.
     */
    suspend fun fetchCurrentTime(): Long? = withContext(Dispatchers.IO) {
        for (server in NTP_SERVERS) {
            val time = queryServer(server)
            if (time != null) {
                return@withContext time
            }
        }
        null
    }

    private fun queryServer(host: String): Long? {
        var socket: DatagramSocket? = null
        return try {
            val address = InetAddress.getByName(host)
            val buffer = ByteArray(48)
            buffer[0] = ((NTP_VERSION shl 3) or NTP_MODE_CLIENT).toByte()

            val requestTime = System.currentTimeMillis()
            val requestTicks = SystemClock.elapsedRealtime()
            writeTimeStamp(buffer, 40, requestTime)

            socket = DatagramSocket().apply {
                soTimeout = NTP_TIMEOUT_MS
            }

            val requestPacket = DatagramPacket(buffer, buffer.size, address, NTP_PORT)
            socket.send(requestPacket)

            val responsePacket = DatagramPacket(buffer, buffer.size)
            socket.receive(responsePacket)

            val responseTicks = SystemClock.elapsedRealtime()
            val responseTime = requestTime + (responseTicks - requestTicks)

            // Baca timestamp dari respons NTP
            val originateTime = readTimeStamp(buffer, 24)
            val receiveTime = readTimeStamp(buffer, 32)
            val transmitTime = readTimeStamp(buffer, 40)

            // Hitung koreksi selisih waktu jaringan (clock offset)
            val roundTripDelay = (responseTicks - requestTicks) - (transmitTime - receiveTime)
            val clockOffset = ((receiveTime - originateTime) + (transmitTime - responseTime)) / 2

            // Waktu aktual sekarang dihitung dari titik acuan
            responseTime + clockOffset
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            socket?.close()
        }
    }

    private fun readTimeStamp(buffer: ByteArray, offset: Int): Long {
        val seconds = read32(buffer, offset)
        val fraction = read32(buffer, offset + 4)
        return ((seconds - OFFSET_1900_TO_1970) * 1000) + ((fraction * 1000) / 0x100000000L)
    }

    private fun writeTimeStamp(buffer: ByteArray, offset: Int, time: Long) {
        val seconds = time / 1000L
        val milliseconds = time - (seconds * 1000L)
        val ntpSeconds = seconds + OFFSET_1900_TO_1970

        // Tulis detik
        buffer[offset] = (ntpSeconds shr 24).toByte()
        buffer[offset + 1] = (ntpSeconds shr 16).toByte()
        buffer[offset + 2] = (ntpSeconds shr 8).toByte()
        buffer[offset + 3] = (ntpSeconds shr 0).toByte()

        // Tulis pecahan detik
        val fraction = (milliseconds * 0x100000000L) / 1000L
        buffer[offset + 4] = (fraction shr 24).toByte()
        buffer[offset + 5] = (fraction shr 16).toByte()
        buffer[offset + 6] = (fraction shr 8).toByte()
        buffer[offset + 7] = (fraction shr 0).toByte()
    }

    private fun read32(buffer: ByteArray, offset: Int): Long {
        val b0 = buffer[offset].toLong() and 0xFF
        val b1 = buffer[offset + 1].toLong() and 0xFF
        val b2 = buffer[offset + 2].toLong() and 0xFF
        val b3 = buffer[offset + 3].toLong() and 0xFF
        return (b0 shl 24) or (b1 shl 16) or (b2 shl 8) or b3
    }
}
