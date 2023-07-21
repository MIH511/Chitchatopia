package com.chating.chitchatopia.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.R
import com.chating.chitchatopia.view.new_chats.NewChatsView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class RecyclerViewNewChatAdapter (var context: Context,var allUser: ArrayList<User>,var view:NewChatsView):RecyclerView.Adapter<RecyclerViewNewChatAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.new_chats_container,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allUser.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(allUser[position].userID!=FirebaseAuth.getInstance().currentUser?.uid){
        Picasso.get().load(allUser[position].profileImage).into(holder.userImage)
        holder.userName.text= allUser[position].user_name
        holder.itemView.setOnClickListener {
            YoYo.with(Techniques.Tada).duration(700).repeat(1).playOn(holder.itemView)
            view.setOnUserListener(user = allUser[position])
        }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(filteredList: ArrayList<User>) {

        this.allUser=filteredList
        notifyDataSetChanged()
    }


    class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {

        val userImage=itemView.findViewById<ImageView>(R.id.user_image_new_chat_Container)
        val userName=itemView.findViewById<TextView>(R.id.user_name_new_chat_Container)
    }
}