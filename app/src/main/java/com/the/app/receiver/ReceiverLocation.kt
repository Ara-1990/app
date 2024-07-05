package com.the.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import com.the.app.INTENT_SEND_LOCATION
import com.the.app.DATA_KEY_LOCATION

class ReceiverLocation (private val locationUpdate: (com.the.app.models.Location) -> Unit
) : BroadcastReceiver() {

    private val intentFilter by lazy { IntentFilter(INTENT_SEND_LOCATION) }

    override fun onReceive(context: Context?, intent: Intent?) {

        intent?.run {
            if (action == INTENT_SEND_LOCATION) {
                val locInfo: com.the.app.models.Location? = getParcelableExtra(DATA_KEY_LOCATION)
                locInfo?.let { locationUpdate(it) }
            }
        }
    }

    fun register(context: Context) {
        context.registerReceiver(this, intentFilter)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }
}