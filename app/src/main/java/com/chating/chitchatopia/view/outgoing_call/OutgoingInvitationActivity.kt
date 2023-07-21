package com.chating.chitchatopia.view.outgoing_call

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.R
import com.chating.chitchatopia.databinding.ActivityOutgoingInvitationBinding
import com.chating.chitchatopia.utilities.Constants
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

class OutgoingInvitationActivity : AppCompatActivity(),OutgoingView {
    lateinit var binding:ActivityOutgoingInvitationBinding
    var receiverUser: User?=null
    var currentUserDetails: User?=null
    var type: String?=null
    var presenter:OutgoingPresenter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_outgoing_invitation)
        setListeners()
        doInitialization()
    }

    private fun doInitialization() {
        receiverUser = intent.getSerializableExtra(Constants.KEY_RECEIVER_USER_DETAILS) as User
        currentUserDetails = intent.getSerializableExtra(Constants.KEY_CURRENT_USER_DETAILS) as User
        type = intent.getSerializableExtra(Constants.REMOTE_MSG_TYPE) as String
        presenter= OutgoingPresenter(this)
        if (type != null) {
            if (type.equals("video", ignoreCase = true)) {
                binding.imageMeetingTypeOutComing.setImageResource(R.drawable.video_call_incoming)
            } else {
                binding.imageMeetingTypeOutComing.setImageResource(R.drawable.video_call_incoming)
            }
        }
        presenter!!.getInviterToken(receiverUser!!,type!!,currentUserDetails!!)
    }

    private fun setListeners() {
        binding.imageDeclineInvitation.setOnClickListener {
            presenter!!.cancelInvitation(receiverUser!!.fcmToken)
        }
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
        JitsiMeetActivity.launch(this, builder.build())
        finish()
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            presenter!!.invitationResponseReceiver, IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(presenter!!.invitationResponseReceiver)
    }
}