package com.fantest.masmou.patient

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.ParcelUuid
import com.fantest.masmou.core.CommunicationEvent
import com.fantest.masmou.core.EventProtocol
import com.fantest.masmou.core.MasmouBle
import java.nio.charset.StandardCharsets
import java.util.UUID

class BlePatientServer(private val context: Context) {
    private val manager = context.getSystemService(BluetoothManager::class.java)
    private val adapter get() = manager.adapter
    private var server: BluetoothGattServer? = null
    private var eventCharacteristic: BluetoothGattCharacteristic? = null
    private val connectedDevices = linkedSetOf<BluetoothDevice>()
    private var lastPayload = ByteArray(0)
    var running: Boolean = false
        private set

    private val cccdUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private val advertiseCallback = object : AdvertiseCallback() {}

    private val serverCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                connectedDevices.remove(device)
                return
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectedDevices.add(device)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectedDevices.remove(device)
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicReadRequest(
            device: BluetoothDevice,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic,
        ) {
            if (characteristic.uuid != UUID.fromString(MasmouBle.EVENT_CHARACTERISTIC_UUID)) {
                server?.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, offset, null)
                return
            }
            val value = if (offset in 0..lastPayload.size) lastPayload.copyOfRange(offset, lastPayload.size) else ByteArray(0)
            server?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
        }

        @SuppressLint("MissingPermission")
        override fun onDescriptorReadRequest(
            device: BluetoothDevice,
            requestId: Int,
            offset: Int,
            descriptor: BluetoothGattDescriptor,
        ) {
            server?.sendResponse(
                device,
                requestId,
                BluetoothGatt.GATT_SUCCESS,
                offset,
                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE,
            )
        }

        @SuppressLint("MissingPermission")
        override fun onDescriptorWriteRequest(
            device: BluetoothDevice,
            requestId: Int,
            descriptor: BluetoothGattDescriptor,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray,
        ) {
            descriptor.value = value
            if (responseNeeded) {
                server?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, value)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun start(): Boolean {
        if (running) return true
        if (!adapter.isEnabled) return false
        return try {
            val gattServer = manager.openGattServer(context, serverCallback) ?: return false
            val serviceUuid = UUID.fromString(MasmouBle.SERVICE_UUID)
            val service = BluetoothGattService(serviceUuid, BluetoothGattService.SERVICE_TYPE_PRIMARY)
            val characteristic = BluetoothGattCharacteristic(
                UUID.fromString(MasmouBle.EVENT_CHARACTERISTIC_UUID),
                BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ,
            )
            characteristic.addDescriptor(
                BluetoothGattDescriptor(
                    cccdUuid,
                    BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE,
                ),
            )
            service.addCharacteristic(characteristic)
            gattServer.addService(service)
            server = gattServer
            eventCharacteristic = characteristic

            adapter.bluetoothLeAdvertiser?.startAdvertising(
                AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setConnectable(true)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                    .build(),
                AdvertiseData.Builder()
                    .setIncludeDeviceName(false)
                    .addServiceUuid(ParcelUuid(serviceUuid))
                    .build(),
                advertiseCallback,
            )
            running = true
            true
        } catch (_: SecurityException) {
            false
        } catch (_: IllegalStateException) {
            false
        }
    }

    @SuppressLint("MissingPermission")
    fun publish(event: CommunicationEvent) {
        val characteristic = eventCharacteristic ?: return
        val payload = EventProtocol.encode(event).toByteArray(StandardCharsets.UTF_8)
        lastPayload = payload
        characteristic.value = payload
        val currentServer = server ?: return
        connectedDevices.toList().forEach { device ->
            runCatching { currentServer.notifyCharacteristicChanged(device, characteristic, false) }
        }
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        runCatching { adapter.bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback) }
        connectedDevices.clear()
        runCatching { server?.close() }
        server = null
        eventCharacteristic = null
        running = false
    }
}
