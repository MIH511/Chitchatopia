package com.chating.chitchatopia.view.conversation

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.chating.chitchatopia.models.Message
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.R
import com.chating.chitchatopia.adapters.RecyclerViewMessagesAdapter
import com.chating.chitchatopia.databinding.ActivityConversationBinding
import com.chating.chitchatopia.utilities.BaseActivity
import com.chating.chitchatopia.utilities.Constants

import com.chating.chitchatopia.view.outgoing_call.OutgoingInvitationActivity
import com.squareup.picasso.Picasso

class ConversationActivity : BaseActivity(), ConversationView {

    lateinit var conversationBinding:ActivityConversationBinding
    lateinit var presenter : ConversationPresenter
    var adapter:RecyclerViewMessagesAdapter?=null
    var messages:ArrayList<Message>?=null
    var messageReceiver:User?=null
    var currentUserDetails:User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        conversationBinding=DataBindingUtil.setContentView(this,R.layout.activity_conversation)
        doInitialization()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun doInitialization() {
        showLoading()
        messages= ArrayList()
        messageReceiver = intent.getSerializableExtra(Constants.KEY_RECEIVER_USER_DETAILS) as User
        if (messageReceiver!!.fcmToken==null || messageReceiver!!.fcmToken.trim().isEmpty()){
            conversationBinding.userStatueConversationTx.visibility= View.GONE
        }
        else if (messageReceiver!!.fcmToken !=null && messageReceiver!!.fcmToken.trim().isNotEmpty()){
            conversationBinding.userStatueConversationTx.visibility= View.VISIBLE
        }
        currentUserDetails = intent.getSerializableExtra(Constants.KEY_CURRENT_USER_DETAILS) as User
        presenter=ConversationPresenter(this, currentUserDetails = currentUserDetails!!, receiverUserDetails = messageReceiver!!)
        conversationBinding.userNameConversationTx.text = messageReceiver!!.user_name
        Picasso.get().load(messageReceiver!!.profileImage).into(conversationBinding.profileImageConversationImageView)


        adapter=RecyclerViewMessagesAdapter(this,messages, receiverDetails = messageReceiver!!,this)
        conversationBinding.messagesRecyclerView.adapter=adapter

        presenter.getOldMessages(messages!!,messageReceiver!!,currentUserDetails!!)
        presenter.typingStatusListener(messageReceiver!!,currentUserDetails!!)

        conversationBinding.messageBodyConversationEtx.addTextChangedListener {
            conversationBinding.isSendingText = it?.length!! >0
            if(it.toString().trim().isEmpty()){
                presenter.checkTypingStatus(Constants.KEY_TYPE_TO)
            }else{
                presenter.checkTypingStatus(messageReceiver!!.userID)
            }
        }

        setListeners()

        conversationBinding.sendImageConversationImageView.setOnClickListener {
            val intent=Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(intent,25)
        }


    }

    private fun setListeners() {
        conversationBinding.sendMessageConversationImageView.setOnClickListener {
            conversationBinding.isSendingText=false

            presenter.sendTheMessage(conversationBinding.messageBodyConversationEtx.text.toString(),messageReceiver!!,currentUserDetails!!,
                "")
            conversationBinding.messageBodyConversationEtx.setText("")

        }

        conversationBinding.callConversationImageView.setOnClickListener {
            presenter.initiateCallChatting(messageReceiver)
        }

        conversationBinding.videoCallConversationImageView.setOnClickListener {
            presenter.initiateVideoCallChatting(messageReceiver)
        }
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("CutPasteId")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==25){
            if (data!=null &&data.data!=null){
                val selectedImage=data.data
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.send_image_layout)

                val image = dialog.findViewById<ImageView>(R.id.preview_image)
                Picasso.get().load(selectedImage).into(image)
                dialog.show()
                val confirm = dialog.findViewById<Button>(R.id.confirm_sending_image_bt)
                confirm.setOnClickListener {
                    val inputStream= contentResolver.openInputStream(selectedImage!!)
                    val bitmap=BitmapFactory.decodeStream(inputStream)
                    val encodedImage=presenter.encodedImage(bitmap)
                    presenter.sendTheMessage("",messageReceiver!!,currentUserDetails!!,encodedImage)
                }
                val cancel = dialog.findViewById<Button>(R.id.cancel_sending_image_bt)
                cancel.setOnClickListener {
                    dialog.dismiss()
                }

            }
        }
    }

    override fun showLoading() {
        conversationBinding.isLoading=true
    }

    override fun hideLoading() {
        conversationBinding.isLoading=false
    }

    override fun onError(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(message: String?) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setOldMessages(messages: ArrayList<Message>?, count: Int) {
        if(count==0){
            adapter!!.notifyDataSetChanged()
        }else{
            adapter!!.notifyItemRangeChanged(messages!!.size, messages.size)
            conversationBinding.messagesRecyclerView.smoothScrollToPosition(messages.size-1)
        }


    }

    override fun deleteMessage(message: String?,messageId:String,position:Int) {
        presenter.messageDeleted(messageId,message!!,position)
    }

    override fun setConversationId(id: String?) {
    }

    override fun startActivity(type:String) {
        val intent=Intent(this, OutgoingInvitationActivity::class.java)
        intent.putExtra(Constants.KEY_RECEIVER_USER_DETAILS,messageReceiver)
        intent.putExtra(Constants.KEY_CURRENT_USER_DETAILS,currentUserDetails)
        intent.putExtra(Constants.REMOTE_MSG_TYPE,type)
        startActivity(intent)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun updateMessages(messages: ArrayList<Message>) {
        adapter!!.notifyDataSetChanged()
        conversationBinding.messagesRecyclerView.smoothScrollToPosition(messages.size-1)
    }

    override fun notifyIsOnline(isReceiverAvailable: Boolean) {
        if (isReceiverAvailable) conversationBinding.isTheUserOnline=true
        if (!isReceiverAvailable) conversationBinding.isTheUserOnline=false
    }

    override fun setTypingStatus(typing: Boolean) {
        if (typing){
            conversationBinding.userStatueConversationTx.text="Typing..."
        }else{
            conversationBinding.userStatueConversationTx.text="online"
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.listenerAvailabilityOfReceiver()
//        presenter.typingStatusListener()
    }

    override fun onPause() {
        super.onPause()
        presenter.checkTypingStatus("NoOne")
    }

}