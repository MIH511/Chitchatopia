package com.chating.chitchatopia.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chating.chitchatopia.models.Message
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.R
import com.chating.chitchatopia.databinding.ItemContainerRecentConversationBinding
import com.chating.chitchatopia.view.home.HomeView
import com.squareup.picasso.Picasso

class RecyclerViewHomeAdapter( var context: Context,messages:ArrayList<Message>?,var view:HomeView) : RecyclerView.Adapter<RecyclerViewHomeAdapter.ViewHolder>() {

    lateinit var messages:ArrayList<Message>

    init {
        if(messages!=null){
            this.messages=messages
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view=LayoutInflater.from(context).inflate(R.layout.item_container_recent_conversation,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(messages[position].conversationImage).into(holder.binding.userImageRecentContainer)
        holder.binding.userNameRecentContainer.text = messages[position].conversationName
        holder.binding.lastMessageRecentContainer.text = messages[position].message
        if(messages.size==position){
            holder.binding.isLastItem=true
        }
        holder.itemView.setOnClickListener{
            val user= User()
            user.userID=messages[position].conversationId!!
            user.user_name=messages[position].conversationName!!
            user.profileImage=messages[position].conversationImage!!
            view.startActivity(user)
        }
    }

    class ViewHolder (itemView: View):RecyclerView.ViewHolder(itemView){
        var binding=ItemContainerRecentConversationBinding.bind(itemView)
    }
}