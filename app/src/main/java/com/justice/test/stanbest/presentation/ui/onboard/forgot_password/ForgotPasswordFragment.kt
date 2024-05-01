package com.justice.test.stanbest.presentation.ui.onboard.forgot_password
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.justice.test.stanbest.R
import com.justice.test.stanbest.databinding.FragmentForgotPasswordBinding
import com.justice.test.stanbest.presentation.ui.onboard.login.LoginViewModel
import com.justice.test.stanbest.utils.Resource
import com.justice.test.stanbest.utils.showAlerterError
import com.justice.test.stanbest.utils.showAlerterSuccess
import com.justice.test.stanbest.utils.showProgress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {


    private val TAG = "ForgotPasswordFragment"
    private val viewModel: LoginViewModel by viewModels()


    private lateinit var binding: FragmentForgotPasswordBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentForgotPasswordBinding.bind(view)
        viewModel.setCurrentState(LoginViewModel.ViewModelStates.ForgotPassword)
        subScribeToObservers()
        setOnClickListener()
        initTextViewsText()
    }

    private fun initTextViewsText() {
        Log.d(TAG, "initTextViewsText: ")
        val spannableString = SpannableString("Don't have an account? Create Account")

        val start = "Don't have an account? ".length
        val end = start + "Create Account".length
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.teal_200))
        spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        binding.tvDontHaveAnAccount.text = spannableString
    }

    private fun setOnClickListener() {
        binding.btnSend.setOnClickListener {
            sendClicked()
        }
        binding.tvLogin.setOnClickListener {
            findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment())
        }
        binding.tvDontHaveAnAccount.setOnClickListener {
            findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToCreateAccountFragment())
        }
    }

    private fun sendClicked() {
        Log.d(TAG, "sendClicked: ")
        val forgotPasswordData = ForgotPasswordData(email = binding.edtEmail.text.toString())
        viewModel.setEvent(LoginViewModel.Event.ForgotPassword(forgotPasswordData))

    }

    data class ForgotPasswordData(val email: String)

    private fun subScribeToObservers() {

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch {
                viewModel.forgotPasswordStatus.collect {
                    Log.d(TAG, "subScribeToObservers: checkInstructorSetupStatus:${it.status.name}")
                    when (it.status) {
                        Resource.Status.LOADING -> {
                            showProgress(true)
                        }
                        Resource.Status.SUCCESS -> {
                            showProgress(false)
                            forgotPasswordSuccess()

                        }
                        Resource.Status.ERROR -> {
                            showProgress(false)
                            showErrorMessage(it.exception!!.message!!)
                        }

                    }
                }
            }
        }
    }

    private fun forgotPasswordSuccess() {
        showSuccessMessage("Password reset link has been send to you email.")
    }


    private fun showErrorMessage(message: String) {
        showAlerterError(message)

    }

    private fun showSuccessMessage(message: String) {
        showAlerterSuccess(message)

    }

    private fun showProgress(visible: Boolean) {
        binding.progressBar.showProgress(binding.btnSend, visible)
    }

}