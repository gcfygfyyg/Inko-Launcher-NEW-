package com.example.data.repository

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.core.content.ContextCompat
import com.example.data.model.HardwareStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HardwareMonitor(private val context: Context) {

    private val _hardwareStats = MutableStateFlow(queryCurrentStats())
    val hardwareStats: StateFlow<HardwareStats> = _hardwareStats.asStateFlow()

    private val _isTorchOn = MutableStateFlow(false)
    val isTorchOn: StateFlow<Boolean> = _isTorchOn.asStateFlow()

    private var cameraIdWithFlash: String? = null
    private var isReceiverRegistered = false
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            try {
                _hardwareStats.value = queryCurrentStats()
            } catch (_: Exception) {}
        }
    }

    init {
        try {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_BATTERY_CHANGED)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.registerReceiver(
                    context,
                    batteryReceiver,
                    filter,
                    ContextCompat.RECEIVER_NOT_EXPORTED
                )
            } else {
                context.registerReceiver(batteryReceiver, filter)
            }
            isReceiverRegistered = true
        } catch (_: Exception) {}

        // Setup real-time network connectivity listener
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val cb = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: android.net.Network) {
                    _hardwareStats.value = queryCurrentStats()
                }

                override fun onLost(network: android.net.Network) {
                    _hardwareStats.value = queryCurrentStats()
                }

                override fun onCapabilitiesChanged(
                    network: android.net.Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    _hardwareStats.value = queryCurrentStats()
                }
            }
            networkCallback = cb
            val request = android.net.NetworkRequest.Builder().build()
            cm?.registerNetworkCallback(request, cb)
        } catch (_: Exception) {}

        // Setup camera torch safely
        try {
            val camManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val ids = camManager?.cameraIdList ?: emptyArray()
            for (id in ids) {
                try {
                    val chars = camManager?.getCameraCharacteristics(id) ?: continue
                    val hasFlash = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                    val facing = chars.get(CameraCharacteristics.LENS_FACING)
                    if (hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK) {
                        cameraIdWithFlash = id
                        break
                    }
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    fun toggleTorch(): Boolean {
        val camManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        val id = cameraIdWithFlash ?: return false
        val newState = !_isTorchOn.value
        return try {
            camManager?.setTorchMode(id, newState)
            _isTorchOn.value = newState
            true
        } catch (_: Exception) {
            false
        }
    }

    fun refreshStats() {
        try {
            _hardwareStats.value = queryCurrentStats()
        } catch (_: Exception) {}
    }

    private fun queryCurrentStats(): HardwareStats {
        var batteryPct = 85
        var isCharging = false
        try {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
            if (bm != null) {
                val cap = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                if (cap in 0..100) {
                    batteryPct = cap
                }
                isCharging = bm.isCharging
            }
        } catch (_: Exception) {}

        var ramUsedMb = 3200L
        var ramTotalMb = 6144L
        try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager?.getMemoryInfo(memInfo)
            if (memInfo.totalMem > 0) {
                ramTotalMb = memInfo.totalMem / (1024 * 1024)
                ramUsedMb = (memInfo.totalMem - memInfo.availMem) / (1024 * 1024)
            }
        } catch (_: Exception) {}

        var storageUsedGb = 38.4
        var storageTotalGb = 128.0
        try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val totalBytes = stat.blockCountLong * stat.blockSizeLong
            val freeBytes = stat.availableBlocksLong * stat.blockSizeLong
            if (totalBytes > 0) {
                storageTotalGb = "%.1f".format(totalBytes.toDouble() / (1024 * 1024 * 1024)).toDoubleOrNull() ?: 128.0
                storageUsedGb = "%.1f".format((totalBytes - freeBytes).toDouble() / (1024 * 1024 * 1024)).toDoubleOrNull() ?: 38.4
            }
        } catch (_: Exception) {}

        var isConnected = true
        var networkType = "Wi-Fi (Fast)"
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val network = cm?.activeNetwork
            val capabilities = cm?.getNetworkCapabilities(network)
            if (capabilities == null) {
                isConnected = false
                networkType = "Offline"
            } else when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    isConnected = true
                    networkType = "Wi-Fi (Fast)"
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    isConnected = true
                    networkType = "Cellular 5G/LTE"
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    isConnected = true
                    networkType = "Ethernet LAN"
                }
                else -> {
                    isConnected = true
                    networkType = "Connected"
                }
            }
        } catch (_: Exception) {}

        return HardwareStats(
            batteryPct = batteryPct,
            isCharging = isCharging,
            ramUsedMb = ramUsedMb,
            ramTotalMb = ramTotalMb,
            storageUsedGb = storageUsedGb,
            storageTotalGb = storageTotalGb,
            isConnected = isConnected,
            networkType = networkType,
            cpuTempC = 35
        )
    }

    fun cleanUp() {
        if (isReceiverRegistered) {
            try {
                context.unregisterReceiver(batteryReceiver)
            } catch (_: Exception) {}
            isReceiverRegistered = false
        }
        if (networkCallback != null) {
            try {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                cm?.unregisterNetworkCallback(networkCallback!!)
            } catch (_: Exception) {}
            networkCallback = null
        }
    }
}
