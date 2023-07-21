package com.chating.chitchatopia.view.conversation

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Base64
import com.chating.chitchatopia.models.Message
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.network.ApiClient
import com.chating.chitchatopia.network.ApiService
import com.chating.chitchatopia.utilities.Constants
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Objects

class ConversationPresenter (val view:ConversationView,var currentUserDetails:User,var receiverUserDetails:User){


    private var db: FirebaseFirestore= FirebaseFirestore.getInstance()
    var conversationId:String?=null
    var allMessages:ArrayList<Message>?=ArrayList()
    var isReceiverAvailable=false

    @SuppressLint("SimpleDateFormat")
    fun sendTheMessage(
        text: String,
        receiverData: User,
        currentUserDetails: User,
        image: String,
    ){

        this.receiverUserDetails=receiverData
        this.currentUserDetails=currentUserDetails
        val message = hashMapOf<String, Any>()
        if(text.trim().isEmpty() &&image!=null){
            message[Constants.KEY_SENDER_ID] = currentUserDetails.userID
            message[Constants.KEY_RECEIVER_ID] = receiverData.userID
            message[Constants.KEY_SENDER_NAME] = currentUserDetails.user_name
            message[Constants.KEY_RECEIVER_NAME] = receiverData.user_name
            message[Constants.KEY_MESSAGE_CONTENT] = ""
            message[Constants.KEY_IMAGE_CONTENT] = image
            message[Constants.KEY_TIMESTAMP] =getCurrentDate()
        }
        else if (text!=null && image.trim().isEmpty()){
            message[Constants.KEY_SENDER_ID] = currentUserDetails.userID
            message[Constants.KEY_RECEIVER_ID] = receiverData.userID
            message[Constants.KEY_SENDER_NAME] = currentUserDetails.user_name
            message[Constants.KEY_RECEIVER_NAME] = receiverData.user_name
            message[Constants.KEY_MESSAGE_CONTENT] = text
            message[Constants.KEY_IMAGE_CONTENT] = ""
            message[Constants.KEY_TIMESTAMP] =getCurrentDate()
        }

        db.collection(Constants.KEY_COLLECTION_CHAT).add(message)

        ////////////////check if there is a previous conversation between them//////////////////////////////
        if (conversationId!=null){
            if(text!=null){
            updateConversationText(text,conversationId!!)
            }
            if (image.length>1) {
                updateConversationImage(image,conversationId!!)
            }

        ///////////////if no previous conversation between them will create a conversation document////////////////////////
        }else{
            val conversation = hashMapOf<String,Any>()
            conversation[Constants.KEY_SENDER_ID]=currentUserDetails.userID
            conversation[Constants.KEY_SENDER_NAME]=currentUserDetails.user_name
            conversation[Constants.KEY_SENDER_IMAGE]=currentUserDetails.profileImage
            conversation[Constants.KEY_RECEIVER_ID]=receiverData.userID
            conversation[Constants.KEY_RECEIVER_FCM_TOKEN]=receiverData.fcmToken
            conversation[Constants.KEY_RECEIVER_NAME]=receiverData.user_name
            conversation[Constants.KEY_RECEIVER_IMAGE]=receiverData.profileImage
            conversation[Constants.KEY_LAST_MESSAGE]=text
            conversation[Constants.KEY_LAST_IMAGE]=image
            conversation[Constants.KEY_TIMESTAMP]=getCurrentDate()
            addConversation(conversation)
        }

        if (!isReceiverAvailable){
            try {
                val tokens= JSONArray()
                tokens.put(receiverData.fcmToken)

                val body= JSONObject()
                val data= JSONObject()

                data.put(Constants.KEY_USER_ID,currentUserDetails.userID)
                data.put(Constants.KEY_NAME,currentUserDetails.user_name)
                data.put(Constants.KEY_FCM_TOKEN,currentUserDetails.fcmToken)
                data.put(Constants.KEY_EMAIL,currentUserDetails.email)
                data.put(Constants.KEY_PROFILE_IMAGE,currentUserDetails.profileImage)

                data.put(Constants.KEY_USER_ID+"Receiver",receiverData.userID)
                data.put(Constants.KEY_NAME+"Receiver",receiverData.user_name)
                data.put(Constants.KEY_FCM_TOKEN+"Receiver",receiverData.fcmToken)
                data.put(Constants.KEY_EMAIL+"Receiver",receiverData.email)
                data.put(Constants.KEY_PROFILE_IMAGE+"Receiver",receiverData.profileImage)
                data.put(Constants.KEY_MESSAGE_CONTENT,text)
                data.put(Constants.REMOTE_MSG_TYPE,Constants.KEY_SEND_NOTIFICATION)
                body.put(Constants.REMOTE_MSG_DATA,data)
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens)

                sendNotification(body.toString())
            }catch (e:Exception){
                view.onError(e.message)
            }
        }

    }

    private fun sendNotification(messageBody: String) {

        ApiClient.retrofit().create(ApiService::class.java)
            .sendRemoteMessage(Constants.getRemoteMessageHeaders(),messageBody)
            .enqueue(object :Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful){
                        try {
                            if (response.body()!=null){
                                val responseJson= JSONObject(response.body()!!)
                                val result= responseJson.getJSONArray("results")

                                if (responseJson.getInt("failure")==1){
                                    val error=result.get(0) as JSONObject
                                    view.onError(error.getString("error"))
                                    return
                                }
                            }
                        }catch (e:Exception){
                            e.printStackTrace()
                            view.onError(e.message)
                        }
                     view.onSuccess("Notification sent successfully")

                    }else{
                        view.onError(response.code().toString())
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    view.onError(t.message)
                }

            })


    }

    fun encodedImage(bitmap: Bitmap): String {
        val previewWidth = 150
        val previewHeight = bitmap.height * previewWidth / bitmap.width
        val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false)
        val byteArrayOutputStream = ByteArrayOutputStream()
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun addConversation(conversation: HashMap<String, Any>, ) {
        db.collection(Constants.KEY_COLLECTION_CONVERSATION).add(conversation)
            .addOnSuccessListener {
                conversationId=it.id
                view.setConversationId(conversationId!!)
            }

    }

     private fun updateConversationText(text: String, conversationId: String) {
        val documentRef= db.collection(Constants.KEY_COLLECTION_CONVERSATION).document(conversationId)
        documentRef.update(
            Constants.KEY_LAST_MESSAGE,text,
            Constants.KEY_TIMESTAMP, getCurrentDate()
        )

    }
    private fun updateConversationImage(image: String, conversationId: String) {
        val documentRef= db.collection(Constants.KEY_COLLECTION_CONVERSATION).document(conversationId)
        documentRef.update(
            Constants.KEY_LAST_IMAGE,image,
            Constants.KEY_TIMESTAMP, getCurrentDate()
        )

    }




    fun getOldMessages(
        messages: ArrayList<Message>,
        messageReceiver: User,
        currentUserDetails: User
    ) {
        db.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID,currentUserDetails.userID)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,messageReceiver.userID)
            .addSnapshotListener { value, error ->

                if (error!=null){
                    return@addSnapshotListener
                }
                if(value!=null){
                    val count=messages.size

                    for(documentChange in value.documentChanges){

                        if(documentChange.type==DocumentChange.Type.ADDED){
                            val message=Message()
                            message.senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                            message.receiverId = documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                            message.message = documentChange.document.getString(Constants.KEY_MESSAGE_CONTENT)
                            message.image = documentChange.document.getString(Constants.KEY_IMAGE_CONTENT)
                            message.messageId = documentChange.document.id
                            message.dateTime = documentChange.document.getString(Constants.KEY_TIMESTAMP).toString()
                            messages.add(message)
                            allMessages!!.add(message)
                        }
                    }
                    messages.sortWith { o1, o2 -> o1.dateTime!!.compareTo(o2.dateTime!!) }
                    view.setOldMessages(messages,count)
                }
                if(conversationId==null){
                    checkForConversation(currentUserDetails,messageReceiver)
                }
            }

        db.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID,messageReceiver.userID)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,currentUserDetails.userID)
            .addSnapshotListener { value, error ->

                if (error!=null){
                    return@addSnapshotListener
                }
                if(value!=null){
                    val count=messages.size
                    for(documentChange in value.documentChanges){
                        if(documentChange.type==DocumentChange.Type.ADDED){
                            val message=Message()
                            message.senderId=documentChange.document.getString(Constants.KEY_SENDER_ID)
                            message.receiverId=documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                            message.message=documentChange.document.getString(Constants.KEY_MESSAGE_CONTENT)
                            message.image=documentChange.document.getString(Constants.KEY_IMAGE_CONTENT)
                            message.messageId = documentChange.document.id
                            message.dateTime=documentChange.document.getString(Constants.KEY_TIMESTAMP).toString()
                            messages.add(message)
                            allMessages!!.add(message)
                        }
                        else if (documentChange.type==DocumentChange.Type.MODIFIED){
                            var i=0
                            for (message in messages){
                                if (message.messageId==documentChange.document.id){
                                    messages[i].message=documentChange.document.getString(Constants.KEY_MESSAGE_CONTENT).toString()
                                    break
                                }
                                i++
                            }
                        }
                    }
                    messages.sortWith { o1, o2 -> o1.dateTime!!.compareTo(o2.dateTime!!) }
                    if(messages.size==count && messages.size!=0 &&count!=0){
                        view.updateMessages(messages)
                    }else{
                        view.setOldMessages(messages,count)
                        view.hideLoading()
                    }
                }
                if(conversationId==null){
                    checkForConversation(currentUserDetails,messageReceiver)
                }
            }
    }

    private fun checkForConversation(currentUserDetails: User, messageReceiver: User) {

        checkForConversationRemotely(currentUserDetails.userID, messageReceiver.userID)
        checkForConversationRemotely(messageReceiver.userID,currentUserDetails.userID)
    }

    private fun checkForConversationRemotely(currentUserId: String, userID: String) {
        db.collection(Constants.KEY_COLLECTION_CONVERSATION)
            .whereEqualTo(Constants.KEY_SENDER_ID,currentUserId)
            .whereEqualTo(Constants.KEY_RECEIVER_ID,userID)
            .get().addOnCompleteListener {

                if (it.isSuccessful && it.result !=null && it.result.size()>0){
                   val documentSnapshot=it.result.documents[0]
                    conversationId=documentSnapshot.id
                    view.setConversationId(conversationId!!)
                }
            }
    }

    fun messageDeleted(messageId:String, message:String,position:Int){
        FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_CHAT)
            .document(messageId)
            .update(Constants.KEY_MESSAGE_CONTENT,message)
        updateConversationText(message,conversationId!!)
        allMessages!![position].message=message
        view.updateMessages(allMessages!!)
    }

    fun initiateCallChatting(messageReceiver: User?) {

        if (messageReceiver!!.fcmToken==null || messageReceiver.fcmToken.trim().isEmpty()){

            view.onError("the user not available for now")
        }else{

            view.startActivity("audio")
        }
    }

    fun initiateVideoCallChatting(messageReceiver: User?) {
        if (messageReceiver!!.fcmToken==null || messageReceiver.fcmToken.trim().isEmpty()){

            view.onError("the user not available for now")
        }else{
            view.startActivity("video")
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {

        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a")
        return dateFormat.format(currentDateTime)
    }

    fun listenerAvailabilityOfReceiver() {

        db.collection(Constants.KEY_COLLECTION_USERS)
            .document(receiverUserDetails.userID)
            .addSnapshotListener { value, error ->

                if(error!=null){
                    return@addSnapshotListener
                }
                if(value!=null){

                    if(value.get(Constants.KEY_KEY_AVAILABILITY)!=null){
                        val availability:String=Objects.requireNonNull(value.get(Constants.KEY_KEY_AVAILABILITY)) as String
                        isReceiverAvailable= availability == "1"

                    }else{
                        view.onError("error")
                    }
                }

                view.notifyIsOnline(isReceiverAvailable)
            }
    }

    fun checkTypingStatus(typing:String){
        db.collection(Constants.KEY_COLLECTION_USERS)
            .document(currentUserDetails.userID)
            .update(Constants.KEY_TYPE_TO,typing).addOnSuccessListener {
            }
    }

    fun typingStatusListener(messageReceiver: User, currentUserDetails: User) {
        db.collection(Constants.KEY_COLLECTION_USERS)
            .document(messageReceiver.userID)
            .addSnapshotListener{value, error ->

                if (value!=null){
                    if (value.getString(Constants.KEY_TYPE_TO).toString() ==currentUserDetails.userID){
                        view.setTypingStatus(true)
                    }else{
                        view.setTypingStatus(false)
                    }
                }
                if (error!=null){
                    view.onError(error.message)
                }
            }
    }

}