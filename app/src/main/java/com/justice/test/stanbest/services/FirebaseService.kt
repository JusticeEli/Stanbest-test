package com.justice.test.stanbest.services


import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class FirebaseService @Inject constructor(
) {
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val isLoggedIn: Boolean
        get() {

            return firebaseAuth.currentUser != null
        }


}