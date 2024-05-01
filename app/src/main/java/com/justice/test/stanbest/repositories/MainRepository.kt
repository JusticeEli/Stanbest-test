package com.justice.test.stanbest.repositories

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.justice.test.stanbest.presentation.ui.onboard.forgot_password.ForgotPasswordFragment
import com.justice.test.stanbest.presentation.ui.onboard.login.LoginFragment
import com.justice.test.stanbest.services.FirebaseService
import com.justice.test.stanbest.utils.Resource
import com.justice.test.stanbest.utils.safeOffer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MainRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val firebaseService: FirebaseService,
){

    val userIsLoggedIn: Boolean
        get() = firebaseService.isLoggedIn
    val currentUserEmail: String?
        get() = firebaseService.firebaseAuth.currentUser?.email


    suspend fun deleteUser() = callbackFlow<Resource<Unit>> {

        safeOffer(Resource.loading(""))
        firebaseService.firebaseAuth.currentUser!!.delete().addOnSuccessListener {
            safeOffer(Resource.success(Unit))

        }.addOnFailureListener {
            safeOffer(Resource.error(it))

        }

        awaitClose { }
    }
    suspend fun logout() = callbackFlow<Resource<Unit>> {

        safeOffer(Resource.loading(""))
        AuthUI.getInstance().signOut(context).addOnSuccessListener {
            safeOffer(Resource.success(Unit))

        }.addOnFailureListener {
            safeOffer(Resource.error(it))

        }

        awaitClose { }
    }

    fun login(loginData: LoginFragment.LoginData) = callbackFlow<Resource<Unit>> {
        safeOffer(Resource.loading())
        firebaseService.firebaseAuth.signInWithEmailAndPassword(loginData.email, loginData.password)
            .addOnSuccessListener {
                safeOffer(Resource.success(Unit))
            }.addOnFailureListener {
                safeOffer(Resource.error(it))
            }
        awaitClose { }
    }

    fun register(loginData: LoginFragment.LoginData) = callbackFlow<Resource<Unit>> {
        safeOffer(Resource.loading())
        firebaseService.firebaseAuth.createUserWithEmailAndPassword(
            loginData.email,
            loginData.password
        ).addOnSuccessListener {
            safeOffer(Resource.success(Unit))
        }.addOnFailureListener {
            safeOffer(Resource.error(it))
        }
        awaitClose { }
    }

    fun forgotPassword(forgotPasswordData: ForgotPasswordFragment.ForgotPasswordData) =
        callbackFlow<Resource<Unit>> {
            safeOffer(Resource.loading())
            firebaseService.firebaseAuth.sendPasswordResetEmail(
                forgotPasswordData.email
            ).addOnSuccessListener {
                safeOffer(Resource.success(Unit))
            }.addOnFailureListener {
                safeOffer(Resource.error(it))
            }
            awaitClose { }
        }

}