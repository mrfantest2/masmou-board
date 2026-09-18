package com.fantest.masmou.family

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import com.fantest.masmou.core.CommunicationEvent
import com.fantest.masmou.core.EventProtocol
import com.fantest.masmou.core.MasmouBle
import java.nio.charset.StandardCharsets
import java.util.UUID

class BleFamilyClient(
    private val context: Context,
    private val onState: (String) -> Unit,
    private val onEvent: (CommunicationEvent) -> Unit,
) {
    private val manager = context.getSystemService(BluetoothManager::class.java)
    private val adapter get() = manager.adapter
    private val handler = Handler(Looper.getMainLooper())
    private var gatt: BluetoothGatt? = null
    private var scanning = false

    private val cccdUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            stopScanOnly()
            onState("Connecting")
            gatt?.close()
            gatt = result.device.connectGatt(
                context,
                false,
                gattCallback,
                android.bluetooth.BluetoothDevice.TRANSPORT_LE,
            )
        }

        override fun onScanFailed(errorCode: Int) {
            scanning = false
            onState("Scan unavailable ($errorCode)")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                onState("Connected · discovering")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED || status != BluetoothGatt.GATT_SUCCESS) {
                runCatching { gatt.close() }
                if (this@BleFamilyClient.gatt === gatt) this@BleFamilyClient.gatt = null
                onState("Disconnected · retrying")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({ start() }, 1500L)
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                onState("Service discovery failed")
                return
            }
            val service: BluetoothGattService =
                gatt.getService(UUID.fromString(MasmouBle.SERVICE_UUID)) ?: run {
                    onState("Masmou service not found")
                    return
                }
            val characteristic =
                service.getCharacteristic(UUID.fromString(MasmouBle.EVENT_CHARACTERISTIC_UUID)) ?: run {
                    onState("Masmou event channel not found")
                    return
                }

            gatt.setCharacteristicNotification(characteristic, true)
            val descriptor = characteristic.getDescriptor(cccdUuid)
            if (descriptor != null) {
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }
            onState("Connected")
            gatt.readCharacteristic(characteristic)
        }

        @Deprecated("Deprecated in API 33 but still used on older devices")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
        ) {
            handlePayload(characteristic.value)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
        ) {
            handlePayload(value)
        }

        @Deprecated("Deprecated in API 33 but still used on older devices")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int,
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) handlePayload(characteristic.value)
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int,
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) handlePayload(value)
        }
    }

    private fun handlePayload(value: ByteArray?) {
        if (value == null || value.isEmpty()) return
        val raw = value.toString(StandardCharsets.UTF_8)
        EventProtocol.decode(raw)?.let(onEvent)
    }

    @SuppressLint("MissingPermission")
    fun start() {
        handler.removeCallbacksAndMessages(null)
        if (!adapter.isEnabled) {
            onState("Bluetooth is off")
            return
        }
        if (gatt != null) return
        val scanner = adapter.bluetoothLeScanner ?: run {
            onState("BLE scanning unavailable")
            return
        }
        return try {
            stopScanOnly()
            val filter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(UUID.fromString(MasmouBle.SERVICE_UUID)))
                .build()
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
            scanning = true
            onState("Searching for Masmou Patient")
            scanner.startScan(listOf(filter), settings, scanCallback)
        } catch (_: SecurityException) {
            scanning = false
            onState("Bluetooth permission required")
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScanOnly() {
        if (!scanning) return
        runCatching { adapter.bluetoothLeScanner?.stopScan(scanCallback) }
        scanning = false
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        handler.removeCallbacksAndMessages(null)
        stopScanOnly()
        runCatching { gatt?.disconnect() }
        runCatching { gatt?.close() }
        gatt = null
        onState("Stopped")
    }
}
