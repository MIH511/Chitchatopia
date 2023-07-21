package com.chating.chitchatopia.view.home

import com.chating.chitchatopia.models.Message
import com.chating.chitchatopia.models.User


interface HomeView {

    fun showLoading()
    fun hideLoading()
    fun onError(message: String?)
    fun onSuccess(message: String?)
    fun setCurrentUserDetails(user: User)
    fun startActivity(user: User)
    fun setRecentConversations(conversation: ArrayList<Message>)
}