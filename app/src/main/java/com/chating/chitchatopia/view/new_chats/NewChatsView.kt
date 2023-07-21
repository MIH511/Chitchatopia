package com.chating.chitchatopia.view.new_chats

import com.chating.chitchatopia.models.User


interface NewChatsView {

    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
    fun showAllUser(allUser:ArrayList<User>)

    fun setOnUserListener(user: User)
    fun showFilteredUsers(filteredList: java.util.ArrayList<User>)
}