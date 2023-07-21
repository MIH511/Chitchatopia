package com.chating.chitchatopia.view.profile_setting

import android.net.Uri
import com.chating.chitchatopia.models.User
import com.chating.chitchatopia.utilities.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileSettingPresenter (var presenter: ProfileSettingView) {
    private var documentReference: DocumentReference? = null
    private var db: FirebaseFirestore? = null

     fun changeDefaultProfilePicture(avatarNumber: String?, myData: User?) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val storageRef = Firebase.storage.reference
            val imagesRef = storageRef.child("images/${myData?.userID}/${myData?.user_name}_profile_image.png")

            db= FirebaseFirestore.getInstance()
            imagesRef.putFile(Uri.parse("android.resource://com.chating.chitchatopia/drawable/avatar$avatarNumber"))
            imagesRef.downloadUrl.addOnSuccessListener {
                documentReference = db?.collection(Constants.KEY_COLLECTION_USERS)?.document(myData?.userID!!)
                documentReference?.update(Constants.KEY_PROFILE_IMAGE, it)
                presenter.onSuccessSetting("image changed")
                presenter.hideLoading()
                presenter.startActivity()

            }
        }
    }
}