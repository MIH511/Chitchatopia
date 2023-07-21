package com.chating.chitchatopia.utilities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

open class BaseActivity : AppCompatActivity() {

    private var documentReference:DocumentReference=FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS).document(FirebaseAuth.getInstance().currentUser!!.uid)
    var value:String?=null
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val fireStore=FirebaseFirestore.getInstance()
        documentReference=fireStore.collection(Constants.KEY_COLLECTION_USERS).document(FirebaseAuth.getInstance().currentUser!!.uid)
    }

    override fun onResume() {
        super.onResume()
       value="1"
        documentReference.update(Constants.KEY_KEY_AVAILABILITY,value)
    }

    override fun onPause() {
        super.onPause()
        value="0"
        documentReference.update(Constants.KEY_KEY_AVAILABILITY,value)

    }
}