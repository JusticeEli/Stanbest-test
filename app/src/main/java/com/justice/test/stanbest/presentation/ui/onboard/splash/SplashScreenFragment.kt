package com.justice.test.stanbest.presentation.ui.onboard.splash


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.justice.test.stanbest.R
import com.justice.test.stanbest.databinding.FragmentSplashScreenBinding
import com.justice.test.stanbest.presentation.ui.onboard.login.LoginViewModel
import com.justice.test.stanbest.presentation.ui.onboard.login.LoginViewModel.*
import com.justice.test.stanbest.presentation.ui.onboard.login.UserEntity
import com.justice.test.stanbest.utils.Resource
import com.justice.test.stanbest.utils.showToastInfo
import com.justice.test.stanbest.utils.stringify

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {

    private val TAG = "SplashScreenFragment"
    private val viewModel: LoginViewModel by viewModels()


    private lateinit var binding: FragmentSplashScreenBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashScreenBinding.bind(view)

        subScribeToObservers()
        checkIfUserIsLoggedIn()

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
                            Log.e(TAG, "subScribeToObservers: ", it.exception)
                            showToastInfo(it.exception!!.message!!)
                        }

                    }
                }
            }
            launch {
                viewModel.checkIfUserIsLoggedInStatus.collect {
                    when (it) {
                        is Event.UserLoggedIn -> {
                            Log.d(TAG, "subScribeToObservers: user logged in")
                            goToUserProfile(it.email)
                        }
                        is Event.UserNotLoggedIn -> {
                            Log.d(TAG, "subScribeToObservers: user not logged in")
                            goToLoginCreateAccount()
                        }


                    }
                }
            }

        }
    }

    private fun goToUserProfile(email: String) {
        findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToUserSetupFragment(email))
    }

    private fun showToastInfo(message: String) {
        requireContext().showToastInfo(message)
    }


    private fun checkInstructorSetupSuccess(event: Event) {
        Log.d(TAG, "checkInstructorSetupSuccess: ")
        when (event) {
            is Event.InstructorHasNotSetupHisPersonalInfo -> {
                goToInstructorSetup(event.instructor)
            }
            is Event.GoToMainScreen -> {
                goToMainScreen()
            }

        }
    }




    private fun goToInstructorSetup(user: UserEntity) {
        Log.d(TAG, "goToInstructorSetup: ")
        val userString = user.stringify
        findNavController().navigate(
            SplashScreenFragmentDirections.actionSplashScreenFragmentToUserSetupFragment(
                 userString
            )
        )
    }


    private fun showProgress(visible: Boolean) {
        Log.d(TAG, "showProgress: visible:$visible")
    }

    private fun goToLoginCreateAccount() {
        Log.d(TAG, "goToLoginScreen: ")
        findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToCreateAccountFragment())
    }

    private fun checkIfUserIsLoggedIn() {
        Log.d(TAG, "checkIfUserIsLoggedIn: ")
        viewModel.setEvent(Event.CheckIfUserIsLoggedIn)
    }


    private fun goToMainScreen() {
        Log.d(TAG, "goToMainScreen: ")

    }


}