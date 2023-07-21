package com.chating.chitchatopia.view.signup

import com.chating.chitchatopia.models.User

interface SignUpView {

    fun showLoading()
    fun hideLoading()
    fun onErrorSignup(message: String?)
    fun onSuccessSignup(message: String?)
    fun startActivity(user: String, newUser: User)
}