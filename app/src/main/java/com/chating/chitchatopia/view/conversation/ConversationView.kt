package com.chating.chitchatopia.view.conversation

import com.chating.chitchatopia.models.Message

interface ConversationView {
    fun showLoading()
    fun hideLoading()
    fun onError(message: String?)
    fun onSuccess(message: String?)
    fun setOldMessages(messages: ArrayList<Message>?, count: Int)
    fun deleteMessage(message: String?,messageId:String,position:Int)
    fun setConversationId(id:String?)
    fun startActivity(type:String)
    fun updateMessages(messages: ArrayList<Message>)
    fun notifyIsOnline(isReceiverAvailable: Boolean)
    fun setTypingStatus(typing: Boolean)

}