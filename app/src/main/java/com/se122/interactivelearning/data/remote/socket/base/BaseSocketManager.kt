package com.se122.interactivelearning.data.remote.socket.base

import android.util.Log
import com.se122.interactivelearning.utils.TokenManager
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import java.net.URISyntaxException

abstract class BaseSocketManager(
    private val tokenManager: TokenManager
) {
    protected var socket: Socket? = null
    protected var isInitialized = false

    abstract val namespaceUrl: String

    open fun connect(onConnected: (() -> Unit)? = null) {
        val token = tokenManager.getAccessTokenSync()
        initSocket(token)

        socket?.on(Socket.EVENT_CONNECT) {
            Log.i(TAG, "Connected to: $namespaceUrl")
            onConnected?.invoke()
        }

        socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
            val error = args.getOrNull(0) as? String
            Log.e(TAG, "Connection error: $error")
        }

        socket?.connect()
    }

    open fun disconnect() {
        socket?.disconnect()
        socket?.off()
        isInitialized = false
        Log.i(TAG, "Disconnected from: $namespaceUrl")
    }

    protected fun emit(event: String, data: Any) {
        socket?.emit(event, data)
        Log.d(TAG, "Emit event: $event with data: $data")
    }

    protected fun on(event: String, callback: (args: Array<Any>) -> Unit) {
        socket?.on(event) {
            Log.d(TAG, "Received event: $event with data: ${it.joinToString()}")
            callback(it)
        }
    }

    protected fun off (event: String) {
        socket?.off(event)
        Log.d(TAG, "Off event: $event")
    }

    private fun initSocket(token: String?) {
        if (isInitialized)
            return

        try {
            val options = IO.Options().apply {
                reconnection = true
                path = "/socket.io"
                auth = mapOf("token" to token)
            }
            socket = IO.socket(namespaceUrl, options)
            isInitialized = true
            Log.i(TAG, "initSocket: $namespaceUrl")
        } catch (e: URISyntaxException) {
            Log.e(TAG, "invalid socket URL: $namespaceUrl, ${e.message}")
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "BaseSocketManager"
    }
}