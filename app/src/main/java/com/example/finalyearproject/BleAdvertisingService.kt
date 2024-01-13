package com.example.finalyearproject

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.AdvertiseData
import java.util.UUID

class BleAdvertisingService : Service() {
    private lateinit var bleAdvertiser: BleAdvertiser

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val YOUR_UNIQUE_ID = "replace-with-your-unique-uuid"
    }

    override fun onCreate() {
        super.onCreate()
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        bleAdvertiser = BleAdvertiser(bluetoothAdapter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        bleAdvertiser.startAdvertising(YOUR_UNIQUE_ID)
        return START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("ble_advertising_service", "BLE Advertising Service")
        } else {
            ""
        }

        val notificationBuilder = Notification.Builder(this, channelId)
        notificationBuilder.setOngoing(true)
            .setContentTitle("BLE Advertising")
            .setContentText("Advertising device information via Bluetooth.")
            .setSmallIcon(R.drawable.ic_launcher_background) // Replace with your icon resource
            .setPriority(Notification.PRIORITY_HIGH)

        return notificationBuilder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    override fun onDestroy() {
        bleAdvertiser.stopAdvertising()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}

class BleAdvertiser(private val bluetoothAdapter: BluetoothAdapter) {
    private var bluetoothLeAdvertiser: BluetoothLeAdvertiser? = bluetoothAdapter.bluetoothLeAdvertiser

    fun startAdvertising(uniqueId: String) {
        // Implementation of BLE advertising using uniqueId
        // ...
    }

    fun stopAdvertising() {
        // Stop BLE advertising
        // ...
    }
}
