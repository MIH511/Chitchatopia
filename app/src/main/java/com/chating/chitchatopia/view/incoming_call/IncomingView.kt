package com.chating.chitchatopia.view.incoming_call

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

interface IncomingView {

    fun onError(message: String?)
    fun onSuccess(message: String?)
    fun finishActivity()
    fun launchMeetingActivity(builder: JitsiMeetConferenceOptions.Builder)
}