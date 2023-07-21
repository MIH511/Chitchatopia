package com.chating.chitchatopia.utilities

object Constants {
    const val KEY_COLLECTION_USERS = "users"
    const val KEY_COLLECTION_CONVERSATION = "conversations"
    const val KEY_COLLECTION_CHAT = "chat"
    const val KEY_NAME = "firstName"
    const val KEY_SENDER_NAME = "senderName"
    const val KEY_SENDER_IMAGE = "senderImage"
    const val KEY_SENDER_ID = "senderId"
    const val KEY_RECEIVER_ID = "ReceiverId"
    const val KEY_RECEIVER_NAME = "ReceiverName"
    const val KEY_RECEIVER_IMAGE = "ReceiverImage"
    const val KEY_LAST_MESSAGE = "lastMessageText"
    const val KEY_MESSAGE_CONTENT = "the_message"
    const val KEY_IMAGE_CONTENT = "image"
    const val KEY_LAST_IMAGE = "lastMessageImage"
    const val KEY_TIMESTAMP = "timestamp"
    const val KEY_CURRENT_USER_DETAILS = "currentUserDetails"
    const val KEY_RECEIVER_USER_DETAILS = "receiverDetails"
    const val KEY_EMAIL = "email"
    const val KEY_PASSWORD = "password"
    const val KEY_PROFILE_IMAGE = "profileImage"
    const val KEY_FCM_TOKEN = "fcmToken"
    const val KEY_KEY_AVAILABILITY= "availability"
    const val KEY_TYPE_TO = "typingTo"
    const val KEY_NO_ONE = "NoOne"
    const val KEY_USER_ID = "userId"
    const val KEY_RECEIVER_FCM_TOKEN = "receiverFcmToken"
    const val KEY_SEND_NOTIFICATION = "sendNotification"

    private const val REMOTE_MSG_AUTHORIZATION = "Authorization"
    private const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"

    const val REMOTE_MSG_TYPE = "type"
    const val REMOTE_MSG_INVITATION = "invitation"
    const val REMOTE_MSG_MEETING_TYPE = "meetingType"
    const val REMOTE_MSG_INVITER_TOKEN = "inviterToken"
    const val REMOTE_MSG_DATA = "data"
    const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"

    const val REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse"

    const val REMOTE_MSG_INVITATION_ACCEPTED = "accepted"
    const val REMOTE_MSG_INVITATION_REJECTED = "rejected"
    const val REMOTE_MSG_INVITATION_CANCELLED = "cancelled"

    const val REMOTE_MSG_MEETING_ROOM = "meetingRoom"

    fun getRemoteMessageHeaders(): HashMap<String, String> {
        val headers= java.util.HashMap<String, String>()
        headers[REMOTE_MSG_AUTHORIZATION] = "key="
        headers[REMOTE_MSG_CONTENT_TYPE] = "application/json"
        return headers
    }

}