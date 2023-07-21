package com.chating.chitchatopia.view.incoming_call

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.chating.chitchatopia.R
import com.chating.chitchatopia.databinding.ActivityIncomimgCallsBinding
import com.chating.chitchatopia.utilities.Constants
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

class IncomingCallsActivity : AppCompatActivity(),IncomingView {

    var activityIncomimgCallsBinding:ActivityIncomimgCallsBinding?=null
    var presenter:IncomingPresenter?=null
    var meetingType:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityIncomimgCallsBinding=DataBindingUtil.setContentView(this,R.layout.activity_incomimg_calls)
        doInitialization()
        setListeners()
    }

    private fun setListeners() {
        activityIncomimgCallsBinding!!.imageAcceptInvitation.setOnClickListener {
            presenter!!.sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED, intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN),meetingType)
        }

        activityIncomimgCallsBinding!!.imageDeclineInvitation.setOnClickListener {
            presenter!!.sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED,intent.getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN),meetingType)
        }
    }

    private fun doInitialization() {
        presenter= IncomingPresenter(this,this)
        meetingType=intent.getSerializableExtra(Constants.REMOTE_MSG_MEETING_TYPE) as String
        val Name=intent.getSerializableExtra(Constants.KEY_NAME) as String
        val email=intent.getSerializableExtra(Constants.KEY_EMAIL) as String


        if(meetingType!=null&&meetingType=="video"){
            activityIncomimgCallsBinding!!.imageMeetingType.setImageResource(R.drawable.video_call_incoming)
        }else {
            activityIncomimgCallsBinding!!.imageMeetingType.setImageResource(R.drawable.video_call_incoming)
        }
        activityIncomimgCallsBinding!!.textFirstChar.text = Name.substring(0,1)
        activityIncomimgCallsBinding!!.textUserName.text = String.format("%s %s",Name)
        activityIncomimgCallsBinding!!.textEmail.text = email

    }

    override fun onError(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun finishActivity() {
        finish()
    }

    override fun launchMeetingActivity(builder: JitsiMeetConferenceOptions.Builder) {
        builder.setRoom(intent.getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))
        JitsiMeetActivity.launch(this, builder.build())
        finish()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(presenter!!.invitationResponseReceiver,
            IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(presenter!!.invitationResponseReceiver)
    }
}