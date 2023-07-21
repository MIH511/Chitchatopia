package com.chating.chitchatopia.view.home

import android.annotation.SuppressLint
import com.chating.chitchatopia.models.Message
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.utilities.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging

class HomePresenter (var view: HomeView) {

    var database:FirebaseFirestore=FirebaseFirestore.getInstance()
    @SuppressLint("LogNotTimber")
    fun getCurrentUserDetails() {

        val uid= FirebaseAuth.getInstance().currentUser?.uid
        val ref=FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS).document(uid!!)

        ref.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val currentUserDetails = documentSnapshot.toObject(User::class.java)
                    if (currentUserDetails != null) {
                        getToken(currentUserDetails)
                    }
                } else {
                    view.onError("Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                view.onError(exception.message)
            }

    }
    @SuppressLint("LogNotTimber")
    private fun getToken(currentUserDetails: User) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            addTokenToDatabase(it,currentUserDetails)
            addTypingStatusToDatabase(currentUserDetails)
        }
    }

    private fun addTokenToDatabase(token: String?,currentUser: User) {

        val runnable= Runnable {
            FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS).document(currentUser.userID)
                .update(Constants.KEY_FCM_TOKEN,token)
                .addOnSuccessListener {
                    currentUser.fcmToken=token!!
                    view.setCurrentUserDetails(currentUser)

                }
        }
        runnable.run()
    }
    private fun addTypingStatusToDatabase(currentUser: User) {

        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS)
            .document(currentUser.userID)
            .update("typingTo", "NoOne")
    }

    fun getRecentConversationUserSent(currentUser: User, conversation: ArrayList<Message>) {

        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
            .whereEqualTo(Constants.KEY_SENDER_ID,currentUser.userID)
            .addSnapshotListener(object :EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error!=null){
                        return
                    }
                    if(value!=null){

                        for (documentSnapShot in value.documentChanges){
                            if (documentSnapShot.type==DocumentChange.Type.ADDED){
                                val message =Message()
                                message.senderId=documentSnapShot.document.getString(Constants.KEY_SENDER_ID)
                                message.receiverId=documentSnapShot.document.getString(Constants.KEY_RECEIVER_ID)
                                if (documentSnapShot.document.getString(Constants.KEY_SENDER_ID)==currentUser.userID){
                                    message.conversationName=documentSnapShot.document.getString(Constants.KEY_RECEIVER_NAME)
                                    message.conversationId=documentSnapShot.document.getString(Constants.KEY_RECEIVER_ID)
                                    message.conversationImage=documentSnapShot.document.getString(Constants.KEY_RECEIVER_IMAGE)
                                }else{

                                    message.conversationName=documentSnapShot.document.getString(Constants.KEY_SENDER_NAME)
                                    message.conversationId=documentSnapShot.document.getString(Constants.KEY_SENDER_ID)
                                    message.conversationImage=documentSnapShot.document.getString(Constants.KEY_SENDER_IMAGE)
                                }

                                message.message=documentSnapShot.document.getString(Constants.KEY_LAST_MESSAGE)
                                message.dateTime=documentSnapShot.document.getString(Constants.KEY_TIMESTAMP).toString()
                                conversation.add(message)
                            }
                            else if (documentSnapShot.type==DocumentChange.Type.MODIFIED){

                                for (messagee:Message in conversation.iterator()){
                                    val senderId=documentSnapShot.document.getString(Constants.KEY_SENDER_ID)
                                    val receiverId=documentSnapShot.document.getString(Constants.KEY_RECEIVER_ID)

                                    if (messagee.senderId.equals(senderId) && messagee.receiverId.equals(receiverId)){
                                        messagee.message=documentSnapShot.document.getString(Constants.KEY_LAST_MESSAGE)
                                        messagee.dateTime=documentSnapShot.document.getString(Constants.KEY_TIMESTAMP).toString()
                                        break
                                    }
                                }
                            }
                        }

                        view.setRecentConversations(conversation)

                    }
                }

            })
    }

    fun getRecentConversationUserReceived(currentUser: User, conversation: ArrayList<Message>) {

        database.collection(Constants.KEY_COLLECTION_CONVERSATION)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,currentUser.userID)
            .addSnapshotListener(object :EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error!=null){
                        return
                    }
                    if(value!=null){

                        for (documentSnapShot in value.documentChanges){
                            if (documentSnapShot.type==DocumentChange.Type.ADDED){
                                val message =Message()
                                message.senderId=documentSnapShot.document.getString(Constants.KEY_SENDER_ID)
                                message.receiverId=documentSnapShot.document.getString(Constants.KEY_RECEIVER_ID)
                                if (documentSnapShot.document.getString(Constants.KEY_SENDER_ID)==currentUser.userID){
                                    message.conversationName=documentSnapShot.document.getString(Constants.KEY_RECEIVER_NAME)
                                    message.conversationId=documentSnapShot.document.getString(Constants.KEY_RECEIVER_ID)
                                    message.conversationImage=documentSnapShot.document.getString(Constants.KEY_RECEIVER_IMAGE)
                                }else{

                                    message.conversationName=documentSnapShot.document.getString(Constants.KEY_SENDER_NAME)
                                    message.conversationId=documentSnapShot.document.getString(Constants.KEY_SENDER_ID)
                                    message.conversationImage=documentSnapShot.document.getString(Constants.KEY_SENDER_IMAGE)
                                }

                                message.message=documentSnapShot.document.getString(Constants.KEY_LAST_MESSAGE)
                                message.dateTime=documentSnapShot.document.getString(Constants.KEY_TIMESTAMP)
                                conversation.add(message)
                            }
                            else if (documentSnapShot.type==DocumentChange.Type.MODIFIED){

                                for (i:Message in conversation.iterator()){
                                    val senderId=documentSnapShot.document.getString(Constants.KEY_SENDER_ID)
                                    val receiverId=documentSnapShot.document.getString(Constants.KEY_RECEIVER_ID)

                                    if (i.senderId.equals(senderId) && i.receiverId.equals(receiverId)){
                                        i.message=documentSnapShot.document.getString(Constants.KEY_LAST_IMAGE)
                                        i.dateTime=documentSnapShot.document.getString(Constants.KEY_TIMESTAMP)
                                        break
                                    }
                                }
                            }
                        }
                        view.setRecentConversations(conversation)
                    }
                }

            })

    }

}