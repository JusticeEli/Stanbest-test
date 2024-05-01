package com.justice.test.stanbest.utils

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import es.dmoral.toasty.Toasty

fun Context.showToastInfo(message: String) {
    Toasty.success(this, message, Toast.LENGTH_SHORT, true).show()

}

fun Fragment.showErrorMessage(message: String) {
    Toasty.error(requireContext(), message, Toast.LENGTH_SHORT, true).show()

}

fun Fragment.showAlerterSuccess(message: String) {
    Toasty.success(requireContext(), message, Toast.LENGTH_SHORT, true).show()
}

fun Fragment.showAlerterError(message: String) {
    Toasty.error(requireContext(), message, Toast.LENGTH_SHORT, true).show()

}
