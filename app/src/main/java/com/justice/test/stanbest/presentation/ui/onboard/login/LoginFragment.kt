package com.justice.test.stanbest.presentation.ui.onboard.login

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.justice.test.stanbest.R
import com.justice.test.stanbest.databinding.FragmentLoginBinding
import com.justice.test.stanbest.utils.Resource
import com.justice.test.stanbest.utils.showAlerterError
import com.justice.test.stanbest.utils.showProgress
import com.justice.test.stanbest.utils.stringify

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private val TAG = "LoginFragment"

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {


    private val viewModel: LoginViewModel by viewModels()


    @Inject
    lateinit var sharedPref: SharedPreferences

    private lateinit var binding: FragmentLoginBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)
        viewModel.setCurrentState(LoginViewModel.ViewModelStates.Login)
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
        binding.btnLogin.setOnClickListener {
            btnLoginClicked()
        }

        binding.tvForgotPassword.setOnClickListener {
            forgotPasswordClicked()
        }
        binding.tvDontHaveAnAccount.setOnClickListener {
            registerNowClicked()
        }
    }

    private fun registerNowClicked() {
        Log.d(TAG, "registerNowClicked: ")
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToCreateAccountFragment())
    }

    private fun forgotPasswordClicked() {
        Log.d(TAG, "forgotPasswordClicked: ")

        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
    }

    private fun btnLoginClicked() {

        val loginData = LoginData(
            email = binding.edtEmail.text.toString(),
            password = binding.edtPassword.text.toString()
        )
        viewModel.setEvent(LoginViewModel.Event.Login(loginData = loginData))
    }

    data class LoginData(val email: String, val password: String)

    private fun subScribeToObservers() {

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch {
                viewModel.checkInstructorSetupStatus.collect {
                    Log.d(TAG, "subScribeToObservers: checkInstructorSetupStatus:${it.status.name}")
                    when (it.status) {
                        Resource.Status.LOADING -> {
                            showProgress(true)
                        }
                        Resource.Status.SUCCESS -> {
                            showProgress(false)
                            checkInstructorSetupSuccess(it.data!!)

                        }
                        Resource.Status.ERROR -> {
                            showProgress(false)
                            showErrorMessage(it.exception!!.message!!)
                        }

                    }
                }
            }
            launch {
                viewModel.loginStatus.collect {
                    Log.d(TAG, "subScribeToObservers: logoutStatus:${it.status.name}")
                    when (it.status) {
                        Resource.Status.LOADING -> {
                            showProgress(true)
                        }
                        Resource.Status.ERROR -> {
                            Log.e(TAG, "subScribeToObservers: ", it.exception)
                            showProgress(false)
                            showErrorMessage(it.exception!!.message!!)
                        }
                        Resource.Status.SUCCESS -> {
                            goToProfile()
                        }

                    }
                }
            }
        }
    }

    private fun goToProfile() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToUserSetupFragment(
                viewModel.repo.currentUserEmail!!
            )
        )
    }


    private fun checkInstructorSetupSuccess(event: LoginViewModel.Event) {
        Log.d(TAG, "checkInstructorSetupSuccess: event:$event")
        when (event) {

            is LoginViewModel.Event.InstructorHasNotSetupHisPersonalInfo -> {
                goToInstructorSetup(event.instructor)
            }
            is LoginViewModel.Event.GoToMainScreen -> {
                goToMainScreen()
            }

        }
    }

    private fun goToMainScreen() {
        Log.d(TAG, "goToMainScreen: ")
    }


    private fun goToInstructorSetup(user: UserEntity) {
        Log.d(TAG, "goToInstructorSetup: ")
        val userString = user.stringify
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToUserSetupFragment(
                userString
            )
        )
    }


    private fun showErrorMessage(message: String) {
        showAlerterError(message)

    }

    private fun showProgress(visible: Boolean) {
        binding.progressBar.showProgress(binding.btnLogin, visible)
    }


}
