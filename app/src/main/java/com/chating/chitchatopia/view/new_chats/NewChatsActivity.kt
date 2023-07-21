package com.chating.chitchatopia.view.new_chats

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.R
import com.chating.chitchatopia.adapters.RecyclerViewNewChatAdapter
import com.chating.chitchatopia.databinding.ActivityNewChatsBinding
import com.chating.chitchatopia.utilities.BaseActivity
import com.chating.chitchatopia.utilities.Constants
import com.chating.chitchatopia.view.conversation.ConversationActivity

class NewChatsActivity : BaseActivity() , NewChatsView {
    lateinit var bindingNewCHatActivityBinding:ActivityNewChatsBinding
    lateinit var recyclerViewNewChatAdapter: RecyclerViewNewChatAdapter
    lateinit var presenter: NewChatPresenter
    lateinit var currentUserDetails:User
    var allUser:ArrayList<User>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingNewCHatActivityBinding=DataBindingUtil.setContentView(this,R.layout.activity_new_chats)

        doInitialization()
    }

    private fun doInitialization() {
        showLoading()
        allUser=ArrayList()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this,R.color.dark_blue)
        currentUserDetails=intent.getSerializableExtra(Constants.KEY_CURRENT_USER_DETAILS) as User
        presenter=NewChatPresenter(this)
        recyclerViewNewChatAdapter=RecyclerViewNewChatAdapter(this,allUser!!,this)
        presenter.getUserDetails(allUser!!)
        bindingNewCHatActivityBinding.recyclerViewNewChats.adapter=recyclerViewNewChatAdapter
        bindingNewCHatActivityBinding.searchForUser.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                presenter.filterList(newText)
                return true
            }

        })
    }



    override fun showLoading() {
        bindingNewCHatActivityBinding.isLoading= true
    }

    override fun hideLoading() {
        bindingNewCHatActivityBinding.isLoading= false
    }

    override fun showError(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showAllUser(allUser: ArrayList<User>) {
        recyclerViewNewChatAdapter.notifyDataSetChanged()
        hideLoading()

    }

    override fun setOnUserListener(user: User) {
        intent= Intent(this,ConversationActivity::class.java)
        intent.putExtra("userDetails",user)
        intent.putExtra("currentUserDetails",currentUserDetails)
        startActivity(intent)
    }

    override fun showFilteredUsers(filteredList: java.util.ArrayList<User>) {
        recyclerViewNewChatAdapter.setFilteredList(filteredList)
    }
}