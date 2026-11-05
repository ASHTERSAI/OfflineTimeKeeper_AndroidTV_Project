package com.example.offlinetimekeeper

import android.os.SystemClock
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object SntpHelper {
    private const val NTP_PACKET_SIZE = 48
    private const val NTP_PORT = 123
    private const val NTP_MODE_CLIENT = 3
    private const val NTP_VERSION = 3
    private const val TRANSMIT_TIME_OFFSET = 40
    private const val OFFSET_1900_TO_1970 = 2208988800L

    fun getNetworkTime(host: String = "pool.ntp.org", timeout: Int = 4000): Long? {
        var socket: DatagramSocket? = null
        return try {
            socket = DatagramSocket()
            socket.soTimeout = timeout
            val address = InetAddress.getByName(host)
            val buffer = ByteArray(NTP_PACKET_SIZE)
            buffer[0] = (NTP_MODE_CLIENT or (NTP_VERSION shl 3)).toByte()

            val requestTime = System.currentTimeMillis()
            val requestTicks = SystemClock.elapsedRealtime()
            writeTimeStamp(buffer, TRANSMIT_TIME_OFFSET, requestTime)

            val request = DatagramPacket(buffer, buffer.size, address, NTP_PORT)
            socket.send(request)

            val response = DatagramPacket(buffer, buffer.size)
            socket.receive(response)
            val responseTicks = SystemClock.elapsedRealtime()

            val transmitTime = readTimeStamp(buffer, TRANSMIT_TIME_OFFSET)
            val roundTripTime = responseTicks - requestTicks
            transmitTime + (roundTripTime / 2)
        } catch (e: Exception) {
            null
        } finally {
            socket?.close()
        }
    }

    private fun readTimeStamp(buffer: ByteArray, offset: Int): Long {
        var seconds = 0L
        for (i in 0..3) {
            val b = buffer[offset + i].toLong() and 0xff
            seconds = (seconds shl 8) or b
        }
        var fraction = 0L
        for (i in 4..7) {
            val b = buffer[offset + i].toLong() and 0xff
            fraction = (fraction shl 8) or b
        }
        return (seconds - OFFSET_1900_TO_1970) * 1000L + (fraction * 1000L / 0x100000000L)
    }

    private fun writeTimeStamp(buffer: ByteArray, offset: Int, time: Long) {
        val seconds = time / 1000L + OFFSET_1900_TO_1970
        val fraction = ((time % 1000L) * 0x100000000L) / 1000L
        var temp = seconds
        for (i in 3 downTo 0) {
            buffer[offset + i] = (temp and 0xff).toByte()
            temp = temp ushr 8
        }
        temp = fraction
        for (i in 7 downTo 4) {
            buffer[offset + i] = (temp and 0xff).toByte()
            temp = temp ushr 8
        }
    }
}
