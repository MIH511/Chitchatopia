package com.chating.chitchatopia.view.profile_setting

interface ProfileSettingView {
    fun showLoading()
    fun hideLoading()
    fun onErrorSetting(message: String?)
    fun onSuccessSetting(message: String?)
    fun startActivity()
}