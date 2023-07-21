package com.chating.chitchatopia.view.incoming_call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.chating.chitchatopia.network.ApiClient
import com.chating.chitchatopia.network.ApiService
import com.chating.chitchatopia.utilities.Constants
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class IncomingPresenter(val view:IncomingView, val context:Context) {
    fun sendInvitationResponse(type: String, receiverToken: String?, meetingType: String?) {

        try {
            val tokens=JSONArray()
            tokens.put(receiverToken)

            val body= JSONObject()
            val data= JSONObject()

            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION_RESPONSE)
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,type)

            body.put(Constants.REMOTE_MSG_DATA,data)
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens)

            sendRemoteMessage(body.toString(),type,meetingType);
        }catch (e:Exception){
            view.onError(e.message)
        }
    }

    private fun sendRemoteMessage(remoteMessageBody: String, type: String, meetingType: String?) {

        ApiClient.retrofit().create(ApiService::class.java).sendRemoteMessage(Constants.getRemoteMessageHeaders(),remoteMessageBody)
            .enqueue(object :Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED, ignoreCase = true)) {

                        try {
                            val serverUrl = URL("https://meet.jit.si")
                            val builder = JitsiMeetConferenceOptions.Builder()
                            builder.setServerURL(serverUrl)
                            builder.setFeatureFlag("call-integration.enabled", false)


                            if (meetingType.equals("audio", ignoreCase = true)) {
                                builder.setVideoMuted(true)
                            }
                            view.launchMeetingActivity(builder)

                        } catch (e: Exception) {
                            view.onError(e.message)
                            view.finishActivity()
                        }
                    } else {
                        view.onSuccess("invitation rejected")
                        view.finishActivity()
                    }
                    }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }

    public val invitationResponseReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val type=intent!!.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE)
            if (type==Constants.REMOTE_MSG_INVITATION_CANCELLED){
                view.onSuccess("invitation cancelled")
                view.finishActivity()
            }
        }

    }
}