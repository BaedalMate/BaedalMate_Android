package com.mate.baedalmate.data.datasource.remote.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.messaging
import com.mate.baedalmate.BaedalMateApplication
import com.mate.baedalmate.R
import com.mate.baedalmate.common.extension.storeValue
import com.mate.baedalmate.data.di.tokenDataStore
import com.mate.baedalmate.presentation.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // 메시지가 data payload를 포함하고 있는 경우
        if (remoteMessage.data.isNotEmpty()) {
//            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            val image = remoteMessage.data["image"]
            val chatRoomId = remoteMessage.data["chatRoomId"]
            val notificationType = remoteMessage.data["type"]

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                // 오래 걸리는 작업인지를 판단하는 if문을 작성한 뒤, 오래 걸리는 경우엔 따로 처리
//                scheduleJob()
            } else {
                // 10초 이내에 핸들링 가능한 메시지만 처리
//                handleNow()
            }

            sendNotification(title, body, image, chatRoomId, notificationType ?: null)
        } else if (remoteMessage.notification != null) {
            // notification이 포함된 경우에는 foreground가 아닌이상 data로 들어온 항목들이 나중에 처리된다.
            remoteMessage.notification?.let {
                val body = it.body
                sendNotification("알림 메시지", body, null, null)
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private fun scheduleJob() {
        // [START dispatch_job]
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .build()
        WorkManager.getInstance(this)
            .beginWith(work)
            .enqueue()
        // [END dispatch_job]
    }

    private fun handleNow() {
//        Log.d(TAG, "Short lived task is done.")
    }

    private fun sendNotification(
        messageTitle: String?,
        messageBody: String?,
        image: String?,
        chatRoomId: String?,
        notificationType: String? = "default"
    ) {
        val id = System.currentTimeMillis().toInt()

        val notificationChannel = setNotificationChannel(notificationType ?: "default")
        val notificationManager = setNotificationManager(notificationChannel)
        val notificationBuilder =
            setNotificationContents(
                id,
                messageTitle,
                messageBody,
                image,
                chatRoomId,
                notificationChannel.id
            )
        notificationManager.notify(id /* ID of notification */, notificationBuilder.build())
    }

    private fun setNotificationChannel(notificationType: String): NotificationChannel {
        val channelId = notificationType

        val channelName = when (notificationType) {
            getString(R.string.notification_channel_chat_id) -> getString(R.string.notification_channel_chat_name)
            getString(R.string.notification_channel_recruit_id) -> getString(R.string.notification_channel_recruit_name)
            getString(R.string.notification_channel_notice_id) -> getString(R.string.notification_channel_notice_name)
            else -> getString(R.string.notification_channel_default_name)
        }

        val channelDescription = when (notificationType) {
            getString(R.string.notification_channel_chat_id) -> getString(R.string.notification_channel_chat_description)
            getString(R.string.notification_channel_recruit_id) -> getString(R.string.notification_channel_recruit_description)
            getString(R.string.notification_channel_notice_id) -> getString(R.string.notification_channel_notice_description)
            else -> getString(R.string.notification_channel_default_description)
        }

        return NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = channelDescription }
    }

    private fun setNotificationManager(channel: NotificationChannel): NotificationManager {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel)
        }
        return notificationManager
    }

    private fun setNotificationContents(
        messageId: Int,
        messageTitle: String?,
        messageBody: String?,
        image: String?,
        chatRoomId: String?,
        channelId: String
    ): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("ExtraFragment", "Notification")
        notificationIntent.putExtra("chatRoomId", chatRoomId)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            messageId /* Request code */,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationImage = if (!image.isNullOrEmpty()) {
            try {
                Glide.with(BaedalMateApplication.applicationContext())
                    .asBitmap()
                    .load("${IMAGE_URL_PREFIX}${image}")
                    .submit()
                    .get()
            } catch (exception: Exception) {
                null
            }
        } else null

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_baedalmate_logo)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setLargeIcon(notificationImage)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
    }

    suspend fun getCurrentToken() = suspendCoroutine<String> { continuation ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(TAG, "Fetching FCM registration token failed", task.exception)
                continuation.resume("")
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            Log.d(TAG, token)
            continuation.resume(token)
        })
    }

    suspend fun registerFcmToken() {
        BaedalMateApplication.applicationContext().tokenDataStore.storeValue(
            stringPreferencesKey("fcmToken"),
            getCurrentToken()
        )
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }

    fun unregisterFcmToken() {
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
    }

    fun subscribeTopicNotice() {
        Firebase.messaging.subscribeToTopic("notice")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) Log.d(TAG, "NOTICE 수신")
            }
    }

    fun unsubscribeTopicNotice() {
        Firebase.messaging.unsubscribeFromTopic("notice")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) Log.d(TAG, "NOTICE 수신 거부")
            }
    }

    companion object {
        private const val TAG = "FirebaseMessagingService"
        private const val IMAGE_URL_PREFIX = "http://3.35.27.107:8080/images/"
    }

    internal class MyWorker(appContext: Context, workerParams: WorkerParameters) :
        Worker(appContext, workerParams) {
        override fun doWork(): Result {
            // TODO(developer): add long running task here.
            return Result.success()
        }
    }
}