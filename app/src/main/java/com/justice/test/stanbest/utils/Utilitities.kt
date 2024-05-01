package com.justice.test.stanbest.utils

import android.content.SharedPreferences
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.justice.test.stanbest.R
import com.justice.test.stanbest.presentation.ui.onboard.login.UserEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.SendChannel

val UserEntity.stringify: String
    get() = Gson().toJson(this)




fun ProgressBar.showProgress(button: Button, visible: Boolean) {

    if (visible) {
        button.setTextColor(ContextCompat.getColor(button.context, R.color.teal_200))

    } else {
        button.setTextColor(ContextCompat.getColor(button.context, R.color.black))

    }
    this.isVisible = visible

}
var String.user:UserEntity
    get() {

        val userData = Gson().fromJson(this, UserEntity::class.java)
        return userData
    }
    set(value) {
        user = value
    }



@ExperimentalCoroutinesApi
fun <E> SendChannel<E>.safeOffer(value: E): Boolean {
    if (isClosedForSend) return false
    return try {
        trySend(value)
        true
    } catch (e: CancellationException) {
        false
    }
}
val KEY_INSTRUCTOR_DATA="KEY_INSTRUCTOR_DATA"
var SharedPreferences.user: UserEntity?
    get() {
        val userString = this.getString(KEY_INSTRUCTOR_DATA, null)
        val userData =
            Gson().fromJson(userString, UserEntity::class.java)
        return userData
    }
    set(value) {
        val userString = Gson().toJson(value)
        this.edit().putString(KEY_INSTRUCTOR_DATA, userString).apply()
    }



lateinit var dialog: AlertDialog
fun FragmentActivity.showProgress(visible: Boolean, message: String = "loading....") {
    if (!visible) {
        if (::dialog.isInitialized) {
            dialog.cancel()
        }

        return
    }
    if (visible && ::dialog.isInitialized && dialog.isShowing) {
        return

    }
    val builder = AlertDialog.Builder(this, R.style.TransparentAlertDialog)
    builder.setCancelable(false)
    val inflater = layoutInflater
    val dialogView = inflater.inflate(R.layout.progress_bar_container, null)
    builder.setView(dialogView)
    dialog = builder.create()



    dialog.show()
}