package com.chating.chitchatopia.view.login

import com.chating.chitchatopia.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore




class LoginPresenter(private var loginView: LoginView) {


    fun login(email: String, password: String) {

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener {

                loginView.onSuccessLogin("success")
                getCurrentUserDetails(it.user!!.uid)

            }.addOnFailureListener {
                loginView.hideLoading()
            }

    }


    private fun getCurrentUserDetails(uid: String) {

        val ref=FirebaseFirestore.getInstance().collection("users").document(uid)

        ref.addSnapshotListener{ value, error ->
            if (value!=null){
                val currentUserDetails= value.toObject(User::class.java)
                loginView.hideLoading()
                if (currentUserDetails != null) {
                    loginView.startActivity(currentUserDetails)
                }
            }
            if (error!=null) loginView.onErrorLogin(error.message)
        }


    }

}