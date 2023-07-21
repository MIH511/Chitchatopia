package com.chating.chitchatopia.view.outgoing_call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.network.ApiClient
import com.chating.chitchatopia.network.ApiService
import com.chating.chitchatopia.utilities.Constants
import com.google.firebase.messaging.FirebaseMessaging
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.net.URL
import java.util.UUID
import kotlin.Exception

class OutgoingPresenter(var view:OutgoingView) {

    var inviterToken:String?=null
    var meetingType:String?=null
    var meetingRoom:String?=null

    fun getInviterToken(user:User,meetingType:String,currentUser:User){
        this.meetingType=meetingType
        FirebaseMessaging.getInstance().token.addOnSuccessListener {

            if (it!=null){
                inviterToken=it
            }

            initiateMeeting(meetingType,user.fcmToken,currentUser)
        }
    }

    private fun initiateMeeting(meetingType: String, fcmTokenReceiver: String, currentUser: User) {

        try {
            val tokens=JSONArray()
            tokens.put(fcmTokenReceiver)
            val body=JSONObject()
            val data=JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION)
            data.put(Constants.REMOTE_MSG_MEETING_TYPE,meetingType)
            data.put(Constants.KEY_NAME,currentUser.user_name)
            data.put(Constants.KEY_EMAIL,currentUser.email)
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN,inviterToken)

            meetingRoom= currentUser.userID+" "+ UUID.randomUUID().toString().substring(0,5)
            data.put(Constants.REMOTE_MSG_MEETING_ROOM,meetingRoom)
            body.put(Constants.REMOTE_MSG_DATA,data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens)
            sendRemoteMessage(body.toString(),Constants.REMOTE_MSG_INVITATION)
        }catch (e:Exception){
            view.onError(e.message)
            view.finishActivity()
        }
    }

    fun cancelInvitation(receiverFcmToken: String) {
        val tokens= JSONArray()

        try {
            tokens.put(receiverFcmToken)

            val body= JSONObject()
            val data= JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,Constants.REMOTE_MSG_INVITATION_CANCELLED)

            body.put(Constants.REMOTE_MSG_DATA,data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens)

            sendRemoteMessage(body.toString(),Constants.REMOTE_MSG_INVITATION_RESPONSE)
        }catch (e: Exception){
            view.onError(e.message)
        }
    }

    private fun sendRemoteMessage(remoteMessageBody: String, type: String) {
        ApiClient.retrofit().create(ApiService::class.java).sendRemoteMessage(Constants.getRemoteMessageHeaders(),remoteMessageBody)
            .enqueue(object :retrofit2.Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful){
                        if (type==Constants.REMOTE_MSG_INVITATION){
                            view.onSuccess("Invitation sent successfully")
                        }else if(type==Constants.REMOTE_MSG_INVITATION_RESPONSE){
                            view.onSuccess("Invitation cancelled")
                            view.finishActivity()
                        }
                    }
                    else{
                        view.onError(response.message())
                        view.finishActivity()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    view.onError(t.message)
                }

            })

    }

    val invitationResponseReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)

            if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED, ignoreCase = true)) {
                view.onSuccess("invitation accepted")
                try {
                    val serverUrl = URL("https://meet.jit.si")

                    val builder = JitsiMeetConferenceOptions.Builder()
                    builder.setServerURL(serverUrl)
                    builder.setFeatureFlag("call-integration.enabled", false)
                    builder.setRoom(meetingRoom)
                    if (meetingType.equals("audio", ignoreCase = true)) {
                        builder.setVideoMuted(true)
                    }

                    view.launchMeetingActivity(builder)

                } catch (e: Exception) {
                    view.onError(e.message)
                    view.finishActivity()
                }
            } else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED, ignoreCase = true)) {
                view.onSuccess("invitation rejected")
                view.finishActivity()
            }
        }
    }
}