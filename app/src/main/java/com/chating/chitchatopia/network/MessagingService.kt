package com.chating.chitchatopia.network

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.utilities.Constants
import com.chating.chitchatopia.view.conversation.ConversationActivity
import com.chating.chitchatopia.view.incoming_call.IncomingCallsActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MessagingService():FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @SuppressLint("UnspecifiedImmutableFlag", "ObsoleteSdkInt")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val type= message.data[Constants.REMOTE_MSG_TYPE]

        if(type!=null){
            when (type) {
                "invitation" -> {

                    val intent=Intent(applicationContext,IncomingCallsActivity::class.java)
                    intent.putExtra("meetingType",message.data["meetingType"])
                    intent.putExtra("firstName",message.data["firstName"])
                    intent.putExtra("email",message.data["email"])
                    intent.putExtra("inviterToken",message.data["inviterToken"])
                    intent.putExtra("meetingRoom",message.data["meetingRoom"])
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                "invitationResponse" -> {
                    val intent=Intent("invitationResponse")
                    intent.putExtra("invitationResponse",message.data["invitationResponse"])
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                }
                "sendNotification" -> {

                    val notificationId=Random.nextInt()
                    val channelId="chat_message"

                    val currentUser: User=User()
                    val receiverUser: User=User()
                    currentUser.user_name=message.data[Constants.KEY_NAME]!!
                    currentUser.email=message.data[Constants.KEY_EMAIL]!!
                    currentUser.fcmToken=message.data[Constants.KEY_FCM_TOKEN]!!
                    currentUser.userID=message.data[Constants.KEY_USER_ID]!!
                    currentUser.profileImage=message.data[Constants.KEY_PROFILE_IMAGE]!!

                    receiverUser.user_name=message.data[Constants.KEY_NAME+"Receiver"]!!
                    receiverUser.email=message.data[Constants.KEY_EMAIL+"Receiver"]!!
                    receiverUser.fcmToken=message.data[Constants.KEY_FCM_TOKEN+"Receiver"]!!
                    receiverUser.userID=message.data[Constants.KEY_USER_ID+"Receiver"]!!
                    receiverUser.profileImage=message.data[Constants.KEY_PROFILE_IMAGE+"Receiver"]!!
                    val intent=Intent(this,ConversationActivity::class.java)
                    intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra(Constants.KEY_CURRENT_USER_DETAILS,receiverUser)
                    intent.putExtra(Constants.KEY_RECEIVER_USER_DETAILS,currentUser)
                    val pendingIntent= PendingIntent.getActivity(this,0,intent,0)
                    val builder:NotificationCompat.Builder=NotificationCompat.Builder(this,channelId)
                    builder.setSmallIcon(org.jitsi.meet.sdk.R.drawable.ic_notification)
                    builder.setContentTitle(message.data[Constants.KEY_NAME])
                    builder.setContentText(message.data[Constants.KEY_MESSAGE_CONTENT])
                    builder.setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message.data[Constants.KEY_MESSAGE_CONTENT])
                    )
                    builder.priority = NotificationCompat.PRIORITY_DEFAULT
                    builder.setContentIntent(pendingIntent)
                    builder.setAutoCancel(true)

                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                        val channelName:CharSequence="chat_message"
                        val channelDescription="this notification channel is used for chat message notification"
                        val importance= NotificationManager.IMPORTANCE_DEFAULT
                        val channel= NotificationChannel(channelId,channelName,importance)
                        channel.description=channelDescription
                        val notificationManager=getSystemService(NotificationManager::class.java)
                        notificationManager.createNotificationChannel(channel);
                    }
                    val notificationManagerCompat=NotificationManagerCompat.from(this);
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    notificationManagerCompat.notify(notificationId,builder.build());
                }
            }
        }

    }
}