package com.chating.chitchatopia.view.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.chating.chitchatopia.models.Message
import com.chating.chitchatopia.R
import com.chating.chitchatopia.databinding.ActivityMainBinding
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.adapters.RecyclerViewHomeAdapter
import com.chating.chitchatopia.utilities.BaseActivity
import com.chating.chitchatopia.utilities.Constants
import com.chating.chitchatopia.view.conversation.ConversationActivity
import com.chating.chitchatopia.view.login.LoginActivity
import com.chating.chitchatopia.view.new_chats.NewChatsActivity
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MainActivity() : BaseActivity(), HomeView {


    lateinit var bindingMainActivityBinding:ActivityMainBinding
    lateinit var presenter:HomePresenter
    lateinit var currentUser: User
    lateinit var homeAdapter: RecyclerViewHomeAdapter
    var conversation:ArrayList<Message>?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMainActivityBinding=DataBindingUtil.setContentView(this,R.layout.activity_main)

        doInitialization()
    }

    private fun doInitialization() {
        conversation= ArrayList()
        showLoading()
        bindingMainActivityBinding.isUserReady=false
        presenter= HomePresenter(this)

        presenter.getCurrentUserDetails()

        homeAdapter= RecyclerViewHomeAdapter(this,conversation,this)
        bindingMainActivityBinding.recyclerViewHome.adapter=homeAdapter
        bindingMainActivityBinding.addNewChatImageView.setOnClickListener {
            val intent=Intent(this,NewChatsActivity::class.java)
            intent.putExtra(Constants.KEY_CURRENT_USER_DETAILS,currentUser)
            startActivity(intent)
        }
        bindingMainActivityBinding.logoutImageView.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent=Intent(this,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }



    override fun showLoading() {
        bindingMainActivityBinding.isLoading=true
    }

    override fun hideLoading() {
        bindingMainActivityBinding.isLoading=false
    }

    override fun onError(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun setCurrentUserDetails(user: User) {
        currentUser=user
        Picasso.get().load(user.profileImage).into(bindingMainActivityBinding.profileImageView)
        bindingMainActivityBinding.userNameHomePage.text = user.user_name
        bindingMainActivityBinding.isUserReady=true
        presenter.getRecentConversationUserSent(user,conversation!!)
        presenter.getRecentConversationUserReceived(user,conversation!!)
        hideLoading()
    }


    override fun startActivity(user: User) {
        val intent=Intent(this,ConversationActivity::class.java)
        intent.putExtra(Constants.KEY_RECEIVER_USER_DETAILS,user)
        intent.putExtra(Constants.KEY_CURRENT_USER_DETAILS,currentUser)
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setRecentConversations(conversation: ArrayList<Message>) {
        homeAdapter.notifyDataSetChanged()
        bindingMainActivityBinding.recyclerViewHome.smoothScrollToPosition(0)

    }
}