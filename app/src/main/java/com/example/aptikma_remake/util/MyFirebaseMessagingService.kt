package com.example.aptikma_remake.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.aptikma_remake.R
import com.example.aptikma_remake.data.model.Notification
import com.example.aptikma_remake.data.repository.NotificationRepository
import com.example.aptikma_remake.ui.activity.MainActivity
import com.example.aptikma_remake.util.Constants.notifyCount
import com.example.aptikma_remake.util.extension.Resource
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val dataUser = getData()
    private val myCoroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    private val repository = NotificationRepository()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        showNotification(title, body)
        val notification = Notification(title, body, nip = dataUser!!.username)
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
                        Log.d("TAG", "Terjadi kesalahan saat menambahkan notifikasi: ${this.message}")
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

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id"
            val channelName = "channel_name"
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, "channel_id")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(123, notificationBuilder.build())
    }
}

//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    private val TAG = "MyFirebaseMessagingService"
//    private val NOTIFICATION_CHANNEL_ID = "channel_id"
//    private val NOTIFICATION_CHANNEL_NAME = "channel_name"
//    private val NOTIFICATION_ID = 1001
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        Log.d(TAG, "Pesan FCM diterima")
//
//        remoteMessage.notification?.let {
//            val title = it.title
//            val body = it.body
//            showNotification(title, body)
//        }
//
//        remoteMessage.data.isNotEmpty().let {
//
//            val nick = remoteMessage.data["key1"]
//            val room = remoteMessage.data["key2"]
//            showNotification(nick, room)
//            Log.d(TAG, "Data Nick: $nick, Room: $room")
//        }
//    }
//
//    override fun onNewToken(token: String) {
//        Log.d(TAG, "Token FCM diperbarui: $token")
//    }
//
//    private fun showNotification(title: String?, message: String?) {
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                NOTIFICATION_CHANNEL_ID,
//                NOTIFICATION_CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_ONE_SHOT
//        )
//
//        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_notification)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//
//        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
//    }
//
//}


//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    override fun onNewToken(token: String) {
//        Log.d("TAG", "Refreshed token: $token")
//    }
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//        Toast.makeText(this, "notification apps", Toast.LENGTH_SHORT).show()
//        NotificationHelper.handleNotification(remoteMessage)
//
//        val notification = remoteMessage.notification
//
//        if (notification != null) {
//            val title = notification.title
//            val body = notification.body
//
//            val intent = Intent(this, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            val pendingIntent =
//                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
//
//            val channelId = "notification"
//            val defaultSoundUri =
//                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//            val notificationBuilder = NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.ic_notification)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//
//            val notificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel(
//                    channelId,
//                    "Notifikasi",
//                    NotificationManager.IMPORTANCE_DEFAULT
//                )
//                notificationManager.createNotificationChannel(channel)
//            }
//
//            notificationManager.notify(0, notificationBuilder.build())
//
//        }
//    }
//
//    private fun showNotification(title: String?, body: String?) {
//
//    }
//
//    private fun isAppInForeground(): Boolean {
//
//        return false
//    }
//}

class NotificationHelper {
    companion object {

        const val UNREAD_COUNT = "unread_count"

        fun handleNotification(remoteMessage: RemoteMessage) {
            val unreadCount = Paper.book().read<Int>(UNREAD_COUNT, 0)!! + 1
//            Paper.book().write(UNREAD_COUNT, unreadCount)
        }

        fun clearUnreadCount() {
            Paper.book().write(UNREAD_COUNT, 0)
        }

        fun getUnreadCount(): Int? {
            return Paper.book().read<Int>(UNREAD_COUNT, 0)
        }


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