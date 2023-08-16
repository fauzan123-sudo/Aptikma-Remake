package com.example.aptikma_remake.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.model.Notification
import com.example.aptikma_remake.data.repository.NotificationRepository
import com.example.aptikma_remake.util.Constants.notifyCount
import com.example.aptikma_remake.util.extension.Resource
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    val dataUser = getData()
    private val myCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val type = remoteMessage.data["tipe"]
        showNotification(title, body)
        val uidId = UUID.randomUUID().toString()
        val notification =
            Notification(id = uidId, title, body, nip = dataUser?.username ?: "", type = type ?: "")
        saveNotification(notification)
        saveNotificationCount()
    }

    private fun saveNotification(notification: Notification) {
        val repository = NotificationRepository()
        myCoroutineScope.launch {
            repository.addNotification(notification).apply {
                when (this) {
                    is Resource.Success -> {
                        Log.d("TAG", "Notifikasi berhasil ditambahkan")
                    }
                    is Resource.Loading -> {
                        Log.d("TAG", "Sedang dalam proses penambahan notifikasi")
                    }
                    is Resource.Error -> {
                        Log.d(
                            "TAG",
                            "Terjadi kesalahan saat menambahkan notifikasi: ${this.message}"
                        )
                    }
                }
            }
        }
    }

    private fun saveNotificationCount() {
        val currentNotificationCount = Paper.book().read(notifyCount, 0)
        val updatedNotificationCount = currentNotificationCount!! + 1
        Paper.book().write(notifyCount, updatedNotificationCount)

        val intent = Intent("update_badge_action")
        intent.putExtra("badge_count", updatedNotificationCount)
        sendBroadcast(intent)
    }

    private fun showNotification(title: String?, message: String?) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, AfterNotification::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentView = RemoteViews(packageName, R.layout.activity_after_notification)

        val channelId = "my_channel_id"
        val builder: NotificationCompat.Builder
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = NotificationCompat.Builder(this, channelId)
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_launcher_background
                    )
                )
                .setContentIntent(pendingIntent)
        } else {
            builder = NotificationCompat.Builder(this)
                .setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_launcher_background
                    )
                )
                .setContentIntent(pendingIntent)
        }

        notificationManager.notify(1234, builder.build())

    }
}


//class MyFirebaseInstanceIDService : FirebaseMessagingService() {
//
//    private val TAG = "FireBaseMessagingService"
//
//    companion object {
//        const val CHANNEL_ID = "aptikma_remake"
//
//    }
//
//    private val NOTIFICATION_ID = 100
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//
//        Log.e("message", "Message Received ...")
//        Log.i("message", "Message Received ...")
//        Log.d("message", "Message Received ...")
//        if (remoteMessage.data.isNotEmpty()) {
//            val title = remoteMessage.data["title"]
//            val body = remoteMessage.data["body"]
//            showNotification(applicationContext, title, body)
//        } else {
//            val title = remoteMessage.notification!!.title
//            val body = remoteMessage.notification!!.body
//            showNotification(applicationContext, title, body)
//        }
//    }
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        Log.e("token", "New Token : $token")
//    }
//
//    private fun showNotification(
//        context: Context,
//        title: String?,
//        message: String?
//    ) {
//        val ii: Intent = Intent(context, MainActivity::class.java)
//        ii.data = Uri.parse("custom://" + System.currentTimeMillis())
//        ii.action = "actionstring" + System.currentTimeMillis()
//        ii.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
//        val pi =
//            PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT)
//        val notification: Notification
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //Log.e("Notification", "Created in up to orio OS device");
//            notification = NotificationCompat.Builder(context, CHANNEL_ID)
//                .setOngoing(true)
//                .setSmallIcon(getNotificationIcon())
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setContentIntent(pi)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(Notification.CATEGORY_SERVICE)
//                .setWhen(System.currentTimeMillis())
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setContentTitle(title).build()
//            val notificationManager = context.getSystemService(
//                Context.NOTIFICATION_SERVICE
//            ) as NotificationManager
//            val notificationChannel = NotificationChannel(
//                CHANNEL_ID,
//                title,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(notificationChannel)
//            notificationManager.notify(NOTIFICATION_ID, notification)
//        } else {
//            notification = NotificationCompat.Builder(context)
//                .setSmallIcon(getNotificationIcon())
//                .setAutoCancel(true)
//                .setContentText(message)
//                .setContentIntent(pi)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setContentTitle(title).build()
//            val notificationManager = context.getSystemService(
//                Context.NOTIFICATION_SERVICE
//            ) as NotificationManager
//            notificationManager.notify(NOTIFICATION_ID, notification)
//        }
//    }
//
//    private fun getNotificationIcon(): Int {
//        val useWhiteIcon =
//            true
//        return if (useWhiteIcon) R.mipmap.ic_launcher
//        else R.mipmap.ic_launcher
//    }
//}