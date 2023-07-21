package com.chating.chitchatopia.view.new_chats

import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.utilities.Constants
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale


class NewChatPresenter(var view: NewChatsView) {

    lateinit var user: User
    var allUsers1: ArrayList<User>?=null

    fun getUserDetails(allUsers: ArrayList<User>) {
        allUsers1= ArrayList()
        user=User()
        val ref=FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)

        ref.get().addOnCompleteListener{task ->

            val querySnapshot = task.result

            for (dc: DocumentSnapshot in querySnapshot.documents) {
                allUsers.add(dc.toObject(User::class.java)!!)
            }
            allUsers1=allUsers
            view.showAllUser(allUsers)

        }


    }

    fun filterList(query: String?) {

        if(query!=null){
            if (allUsers1!=null){
                val filteredList=ArrayList<User>()
                for (user in allUsers1!!){
                    if (user.user_name.lowercase(Locale.ROOT).contains(query)){
                        filteredList.add(user)
                    }
                }
                if (filteredList.isEmpty()){
                    view.showError("No user Found")
                }
                else{
                    view.showFilteredUsers(filteredList)
                }
            }
        }

    }

}