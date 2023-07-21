package com.chating.chitchatopia.view.outgoing_call

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

interface OutgoingView {

    fun onError(message: String?)
    fun onSuccess(message: String?)
    fun finishActivity()
    fun launchMeetingActivity(builder: JitsiMeetConferenceOptions.Builder)
}
