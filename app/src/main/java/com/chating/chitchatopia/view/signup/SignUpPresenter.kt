package com.chating.chitchatopia.view.signup

import android.net.Uri
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.utilities.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SignUpPresenter (private var view: SignUpView) {

    private var documentReference:DocumentReference?=null
    private val db:FirebaseFirestore?=null
    fun signUpProcess(newUser:User) {

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(newUser.email,newUser.password).addOnSuccessListener {

                view.onSuccessSignup("Success")
                newUser.userID=it.user!!.uid
                addUserImage(newUser,Firebase.auth.currentUser!!)
            }.addOnFailureListener {
                view.onErrorSignup(it.message)
                view.hideLoading()
            }

    }

    private fun addUserImage(newUser: User, currentUser: FirebaseUser){
        val storageRef = Firebase.storage.reference
        val imagesRef = storageRef.child("images/${currentUser.uid}/${newUser.user_name}_profile_image.png")

        imagesRef.putFile(Uri.parse("android.resource://com.chating.chitchatopia/drawable/avatar1")).addOnSuccessListener {

            view.onSuccessSignup("image uploaded")
            imagesRef.downloadUrl.addOnSuccessListener {
                documentReference=db?.collection("users")?.document(currentUser.uid)
                encryptPassword(password = newUser.password)
                val users = hashMapOf<String,Any>()
                users[Constants.KEY_EMAIL] = newUser.email
                users["user_name"] = newUser.user_name
                users[Constants.KEY_PASSWORD] = newUser.password
                users[Constants.KEY_USER_ID] = currentUser.uid
                users[Constants.KEY_TYPE_TO] = Constants.KEY_NO_ONE
                users[Constants.KEY_PROFILE_IMAGE] = it
                Firebase.firestore.collection(Constants.KEY_COLLECTION_USERS).document(currentUser.uid).set(users).addOnSuccessListener {
                    view.onSuccessSignup("data saved")
                    view.hideLoading()
                    view.startActivity(currentUser.uid,newUser)
                }.addOnFailureListener {
                    view.onErrorSignup(it.message)
                }
            }

        }

    }


    private fun encryptPassword(password: String): String {
        val saltWithPassword=password+"Myapp"
        var hashedPassword:ByteArray
        try {
            // Create a MessageDigest instance for SHA-256
            val md = MessageDigest.getInstance("SHA-256")

            // Convert the password string to a byte array and compute the hash
            hashedPassword = md.digest(saltWithPassword.toByteArray())

            // Convert the hashed password back to a string and return it
            val sb = StringBuilder()
            for (b in hashedPassword) {
                sb.append(String.format("%02x", b))
            }
            return sb.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null.toString()
    }
}