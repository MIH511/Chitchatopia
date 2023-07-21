package com.chating.chitchatopia.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chating.chitchatopia.models.Message
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.R
import com.chating.chitchatopia.databinding.DeleteMessageLayoutBinding
import com.chating.chitchatopia.databinding.ItemContainerReceiveMessageBinding
import com.chating.chitchatopia.databinding.ItemContainerSentMessageBinding
import com.chating.chitchatopia.view.conversation.ConversationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso


class RecyclerViewMessagesAdapter(
    var context: Context,
    messages: ArrayList<Message>?,
    var receiverDetails: User,
    val conversationView:ConversationView
) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    lateinit var messages: ArrayList<Message>
    private val ITEM_SENDER=1
    private val ITEM_RECEIVER=2
    init {
        if (messages!=null){
            this.messages=messages
        }
    }
    override fun getItemViewType(position: Int): Int {
        val messages=messages[position]
        if(FirebaseAuth.getInstance().currentUser?.uid==messages.senderId){
            return ITEM_SENDER
        }else{
            return ITEM_RECEIVER
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if(viewType==ITEM_SENDER){
            val view=LayoutInflater.from(context).inflate(R.layout.item_container_sent_message,parent,false)
            SentMessageViewHolder(view)
        }
        else{
            val view=LayoutInflater.from(context).inflate(R.layout.item_container_receive_message,parent,false)
            ReceiveMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder.javaClass==SentMessageViewHolder::class.java){
            val viewHolder= holder as SentMessageViewHolder
            if(messages[position].message!=null){

                viewHolder.binding.isImage=false
                viewHolder.binding.textMessage.text = messages[position].message

                viewHolder.binding.textDateTime.text = messages[position].dateTime
                viewHolder.itemView.setOnLongClickListener {

                    val view=LayoutInflater.from(context).inflate(R.layout.delete_message_layout,null)
                    val binding:DeleteMessageLayoutBinding= DeleteMessageLayoutBinding.bind(view)
                    val dialog:AlertDialog=AlertDialog.Builder(context)
                        .setTitle("delete message")
                        .setView(binding.root)
                        .create()
                    binding.deleteForEveryoneTx.setOnClickListener {
                        messages[position].message="this message is deleted"
                        val deletedMessage=messages[position].message
                        conversationView.deleteMessage(deletedMessage,messages[position].messageId!!,position)
                        dialog.dismiss()
                    }
                    binding.cancelTx.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()
                    false
                }
            }
            if(messages[position].image!!.length>1){
                viewHolder.binding.isImage=true
                holder.binding.imageSent.setImageBitmap(getUserImage(messages[position].image!!))
                viewHolder.binding.textDateTimeWithPhoto.text = messages[position].dateTime

            }

        }else{
            val viewHolder= holder as ReceiveMessageViewHolder
            if(messages[position].message!=null){
                viewHolder.binding.isImage=false
                viewHolder.binding.textMessage.text = messages[position].message
                viewHolder.binding.textDateTime.text = messages[position].dateTime

            }
            if(messages[position].image!!.length>1){
                viewHolder.binding.isImage=true
                holder.binding.imageReceived.setImageBitmap(getUserImage(messages[position].image!!))
                viewHolder.binding.textDateTimeWithPhoto.text = messages[position].dateTime


            }
            Picasso.get().load(receiverDetails.profileImage).into(viewHolder.binding.imageProfile)
        }
    }

    fun getUserImage(encodedImage: String): Bitmap {
        val bytes = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
    override fun getItemCount(): Int {
        return messages.size
    }

    inner class SentMessageViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var binding:ItemContainerSentMessageBinding= ItemContainerSentMessageBinding.bind(itemView)
    }

    inner class ReceiveMessageViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var binding:ItemContainerReceiveMessageBinding= ItemContainerReceiveMessageBinding.bind(itemView)
    }
}