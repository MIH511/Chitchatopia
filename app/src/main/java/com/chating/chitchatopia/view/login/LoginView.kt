package com.chating.chitchatopia.view.login

import com.chating.chitchatopia.models.User

interface LoginView {

    fun showLoading()
    fun hideLoading()
    fun onErrorLogin(message: String?)
    fun onSuccessLogin(message: String?)
    fun startActivity(user: User)
}