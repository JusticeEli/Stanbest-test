package com.justice.test.stanbest.presentation.ui.onboard.create_account


import android.content.SharedPreferences
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
import com.justice.test.stanbest.databinding.FragmentCreateAccountBinding
import com.justice.test.stanbest.presentation.ui.onboard.login.LoginFragment
import com.justice.test.stanbest.presentation.ui.onboard.login.LoginViewModel
import com.justice.test.stanbest.presentation.ui.onboard.login.UserEntity
import com.justice.test.stanbest.utils.Resource
import com.justice.test.stanbest.utils.showErrorMessage
import com.justice.test.stanbest.utils.showProgress
import com.justice.test.stanbest.utils.stringify
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CreateAccountFragment : Fragment(R.layout.fragment_create_account) {

    private val TAG = "CreateAccountFragment"
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var binding: FragmentCreateAccountBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCreateAccountBinding.bind(view)
        viewModel.setCurrentState(LoginViewModel.ViewModelStates.Register)

        subScribeToObservers()
        setOnClickListener()
        initTextViewsText()
    }

    private fun initTextViewsText() {
        Log.d(TAG, "initTextViewsText: ")
        val spannableString = SpannableString("Already have an account? Login")

        val start = "Already have an account? ".length
        val end = start + "Login".length
        val colorSpan =
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.holo_blue_bright
                )
            )
        spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        binding.tvLogin.text = spannableString
    }

    private fun setOnClickListener() {
        binding.btnCreateAccount.setOnClickListener {
            btnCreateAccountClicked()
        }

        binding.tvLogin.setOnClickListener {
            loginClicked()
        }

    }

    private fun loginClicked() {

        Log.d(TAG, "loginClicked: ")
        findNavController().navigate(CreateAccountFragmentDirections.actionCreateAccountFragmentToLoginFragment())

    }

    private fun forgotPasswordClicked() {
        Log.d(TAG, "forgotPasswordClicked: ")
        findNavController().navigate(CreateAccountFragmentDirections.actionCreateAccountFragmentToForgotPasswordFragment())
    }

    private fun btnCreateAccountClicked() {
        Log.d(TAG, "btnCreateAccountClicked: ")
        val loginData = LoginFragment.LoginData(
            email = binding.edtEmail.text.toString(),
            password = binding.edtPassword.text.toString()
        )
        viewModel.setEvent(LoginViewModel.Event.CreateAccount(loginData = loginData))
    }


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
                            Log.e(
                                TAG,
                                "subScribeToObservers: checkInstructorSetupStatus ",
                                it.exception
                            )
                            showErrorMessage(it.exception!!.message!!)
                        }

                    }
                }
            }
            launch {
                viewModel.createAccountStatus.collect {
                    when (it.status) {
                        Resource.Status.ERROR -> {
                            showErrorMessage(it.exception!!.message!!)
                        }
                    }
                }
            }
            launch {
                viewModel.registerStatus.collect {
                    Log.d(TAG, "subScribeToObservers: logoutStatus:${it.status.name}")
                    when (it.status) {
                        Resource.Status.LOADING -> {
                            showProgress(true)
                        }

                        Resource.Status.ERROR -> {
                            showProgress(false)
                            showErrorMessage(it.exception!!.message!!)
                            Log.e(TAG, "subScribeToObservers: register", it.exception)
                        }
                        Resource.Status.SUCCESS -> {
                            showProgress(false)
                            registerSuccess()
                        }
                    }
                }
            }
        }
    }

    private fun registerSuccess() {
        findNavController().navigate(
            CreateAccountFragmentDirections.actionCreateAccountFragmentToUserSetupFragment(
                binding.edtEmail.text.toString()
            )
        )
    }


    private fun showProgress(visible: Boolean) {
        binding.progressBar.showProgress(binding.btnCreateAccount, visible)
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

    }


}