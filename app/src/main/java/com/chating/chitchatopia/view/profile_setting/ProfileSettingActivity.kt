package com.chating.chitchatopia.view.profile_setting

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.R
import com.chating.chitchatopia.databinding.ActivityProfileSettingBinding
import com.chating.chitchatopia.utilities.Constants
import com.chating.chitchatopia.view.home.MainActivity


class ProfileSettingActivity : AppCompatActivity(),ProfileSettingView {
    private lateinit var bindingProfileSettingBinding: ActivityProfileSettingBinding

    var avatarNumber:String?=null
    var profileSettingPresenter:ProfileSettingPresenter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingProfileSettingBinding=DataBindingUtil.setContentView(this,R.layout.activity_profile_setting)

        doInitialization()
    }

    private fun doInitialization() {
        val myData = intent.getSerializableExtra(Constants.KEY_CURRENT_USER_DETAILS) as? User
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this,R.color.dark_blue)
        profileSettingPresenter=ProfileSettingPresenter(this)
        bindingProfileSettingBinding.avatar1.setOnClickListener {
            avatarNumber="1"
            bindingProfileSettingBinding.avatar1.setImageResource(R.drawable.selected_avatar1)
            bindingProfileSettingBinding.avatar2.setImageResource(R.drawable.avatar2)
            bindingProfileSettingBinding.avatar13.setImageResource(R.drawable.avatar13)
            bindingProfileSettingBinding.avatar14.setImageResource(R.drawable.avatar14)
            bindingProfileSettingBinding.avatar5.setImageResource(R.drawable.avatar5)
            bindingProfileSettingBinding.avatar6.setImageResource(R.drawable.avatar6)
            bindingProfileSettingBinding.avatar7.setImageResource(R.drawable.avatar7)
            bindingProfileSettingBinding.isAvatarSelected=true
        }
        bindingProfileSettingBinding.avatar2.setOnClickListener {
            avatarNumber="2"
            bindingProfileSettingBinding.avatar2.setImageResource(R.drawable.selected_avatar2)
            bindingProfileSettingBinding.avatar1.setImageResource(R.drawable.avatar1)
            bindingProfileSettingBinding.avatar13.setImageResource(R.drawable.avatar13)
            bindingProfileSettingBinding.avatar14.setImageResource(R.drawable.avatar14)
            bindingProfileSettingBinding.avatar5.setImageResource(R.drawable.avatar5)
            bindingProfileSettingBinding.avatar6.setImageResource(R.drawable.avatar6)
            bindingProfileSettingBinding.avatar7.setImageResource(R.drawable.avatar7)

            bindingProfileSettingBinding.isAvatarSelected=true
        }
        bindingProfileSettingBinding.avatar13.setOnClickListener {
            avatarNumber="13"
            bindingProfileSettingBinding.avatar13.setImageResource(R.drawable.selected_avatar13)
            bindingProfileSettingBinding.avatar2.setImageResource(R.drawable.avatar2)
            bindingProfileSettingBinding.avatar1.setImageResource(R.drawable.avatar1)
            bindingProfileSettingBinding.avatar14.setImageResource(R.drawable.avatar14)
            bindingProfileSettingBinding.avatar5.setImageResource(R.drawable.avatar5)
            bindingProfileSettingBinding.avatar6.setImageResource(R.drawable.avatar6)
            bindingProfileSettingBinding.avatar7.setImageResource(R.drawable.avatar7)

            bindingProfileSettingBinding.isAvatarSelected=true
        }
        bindingProfileSettingBinding.avatar14.setOnClickListener {
            avatarNumber="14"
            bindingProfileSettingBinding.avatar14.setImageResource(R.drawable.selected_avatar14)
            bindingProfileSettingBinding.avatar2.setImageResource(R.drawable.avatar2)
            bindingProfileSettingBinding.avatar1.setImageResource(R.drawable.avatar1)
            bindingProfileSettingBinding.avatar13.setImageResource(R.drawable.avatar13)
            bindingProfileSettingBinding.avatar5.setImageResource(R.drawable.avatar5)
            bindingProfileSettingBinding.avatar6.setImageResource(R.drawable.avatar6)
            bindingProfileSettingBinding.avatar7.setImageResource(R.drawable.avatar7)

            bindingProfileSettingBinding.isAvatarSelected=true
        }
        bindingProfileSettingBinding.avatar5.setOnClickListener {
            avatarNumber="5"
            bindingProfileSettingBinding.avatar5.setImageResource(R.drawable.selected_avatar5)
            bindingProfileSettingBinding.avatar2.setImageResource(R.drawable.avatar2)
            bindingProfileSettingBinding.avatar1.setImageResource(R.drawable.avatar1)
            bindingProfileSettingBinding.avatar14.setImageResource(R.drawable.avatar14)
            bindingProfileSettingBinding.avatar13.setImageResource(R.drawable.avatar13)
            bindingProfileSettingBinding.avatar6.setImageResource(R.drawable.avatar6)
            bindingProfileSettingBinding.avatar7.setImageResource(R.drawable.avatar7)

            bindingProfileSettingBinding.isAvatarSelected=true
        }
        bindingProfileSettingBinding.avatar6.setOnClickListener {
            avatarNumber="6"
            bindingProfileSettingBinding.avatar6.setImageResource(R.drawable.selected_avatar6)
            bindingProfileSettingBinding.avatar2.setImageResource(R.drawable.avatar2)
            bindingProfileSettingBinding.avatar1.setImageResource(R.drawable.avatar1)
            bindingProfileSettingBinding.avatar14.setImageResource(R.drawable.avatar14)
            bindingProfileSettingBinding.avatar5.setImageResource(R.drawable.avatar5)
            bindingProfileSettingBinding.avatar13.setImageResource(R.drawable.avatar13)
            bindingProfileSettingBinding.avatar7.setImageResource(R.drawable.avatar7)
            bindingProfileSettingBinding.isAvatarSelected=true
        }
        bindingProfileSettingBinding.avatar7.setOnClickListener {
            avatarNumber="7"
            bindingProfileSettingBinding.avatar7.setImageResource(R.drawable.selected_avatar7)
            bindingProfileSettingBinding.avatar2.setImageResource(R.drawable.avatar2)
            bindingProfileSettingBinding.avatar1.setImageResource(R.drawable.avatar1)
            bindingProfileSettingBinding.avatar14.setImageResource(R.drawable.avatar14)
            bindingProfileSettingBinding.avatar5.setImageResource(R.drawable.avatar5)
            bindingProfileSettingBinding.avatar6.setImageResource(R.drawable.avatar6)
            bindingProfileSettingBinding.avatar13.setImageResource(R.drawable.avatar13)

            bindingProfileSettingBinding.isAvatarSelected=true
        }

        bindingProfileSettingBinding.chooseAvatarBt.setOnClickListener {

            bindingProfileSettingBinding.isLoading=true
            profileSettingPresenter!!.changeDefaultProfilePicture(avatarNumber,myData)

        }

    }


    override fun showLoading() {
        bindingProfileSettingBinding.isLoading=true
    }

    override fun hideLoading() {
        bindingProfileSettingBinding.isLoading=false
    }

    override fun onErrorSetting(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun onSuccessSetting(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun startActivity() {

        val intent=Intent(this,MainActivity::class.java)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}