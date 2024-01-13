package com.example.finalyearproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finalyearproject.Signup.BluetoothViewModel
import com.example.finalyearproject.Signup.SignUpScreen
import com.example.finalyearproject.Signup.SignUpViewModel

class MainActivity : ComponentActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 101
    }

    private var hasLocationPermission = false
    private var hasBluetoothPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestLocationPermissions()
    }

    private fun setupContent() {
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "signUp") {
                composable("signUp") {
                    val signUpViewModel = SignUpViewModel()
                    val bluetoothViewModel = BluetoothViewModel()

                    if (hasBluetoothPermission && hasLocationPermission) {
                        val serviceIntent = Intent(this@MainActivity, BleAdvertisingService::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(serviceIntent)
                        } else {
                            startService(serviceIntent)
                        }
                    }

                    SignUpScreen(navController, signUpViewModel, bluetoothViewModel)
                }
                // Add more composable screens for navigation as needed
            }
        }
    }

    private fun checkAndRequestLocationPermissions() {
        if (ContextCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            hasLocationPermission = true
            checkAndRequestBluetoothPermissions()
        }
    }

    private fun checkAndRequestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), BLUETOOTH_PERMISSION_REQUEST_CODE)
        } else {
            hasBluetoothPermission = true
            if (hasLocationPermission) {
                setupContent()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasLocationPermission = true
                    checkAndRequestBluetoothPermissions()
                } else {
                    // Handle the case where location permissions are not granted
                }
            }
            BLUETOOTH_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasBluetoothPermission = true
                } else {
                    // Handle the case where Bluetooth permissions are not granted
                }
            }
        }
        if (hasLocationPermission && hasBluetoothPermission) {
            setupContent()
        }
    }
}
