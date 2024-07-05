package com.the.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.LocationProvider
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.the.app.*

class ServiceApp : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private val channelId = "ForegroundServiceChannel"

    override fun onCreate() {
        super.onCreate()

        audioPlay()
        notificationChannel()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val notificationIntent = Intent(this, MainActivity::class.java)
            .apply { action = OPEN_LOCATION_NOTIFICATION_CLICK }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Task Service")
            .setContentText("Play music and get current location!")
            .setSmallIcon(R.drawable.start)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)


        mediaPlayer.start()
        ProviderLocation(this).getDeviceLocationForGrantedPermissions {

            val intentLocationInfo = Intent(INTENT_SEND_LOCATION)
                .apply { putExtra(DATA_KEY_LOCATION, it) }
            sendBroadcast(intentLocationInfo)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer.stop()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    private fun notificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                channelId,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun audioPlay() {
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.hayahaya)
        mediaPlayer.isLooping = true
    }
}