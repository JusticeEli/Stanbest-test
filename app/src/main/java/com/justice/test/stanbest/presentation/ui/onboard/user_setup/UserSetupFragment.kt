package com.justice.test.stanbest.presentation.ui.onboard.user_setup

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.justice.test.stanbest.R
import com.justice.test.stanbest.databinding.FragmentUserSetupBinding
import com.justice.test.stanbest.presentation.ui.onboard.login.Gender
import com.justice.test.stanbest.presentation.ui.onboard.login.UserEntity
import com.justice.test.stanbest.utils.*

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class UserSetupFragment : Fragment(R.layout.fragment_user_setup) {

    private val TAG = "InstructorSetupFragment"

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var binding: FragmentUserSetupBinding
    private val viewModel: UserSetupViewModel by viewModels()
    private val navArgs by navArgs<UserSetupFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserSetupBinding.bind(view)
        setOnClickListeners()
        setupUser(email = navArgs.email)
        subscribeToObservers()


    }

    private fun setupUser(email: String) {
        val userString = sharedPref.getString(email, null);
        if (userString != null)
            goToProfile(email)
    }

    private fun goToProfile(email: String) {
        Log.d(TAG, "goToProfile: ")
        findNavController().navigate(
            UserSetupFragmentDirections.actionUserSetupFragmentToUserProfileFragment(
                email
            )
        )
    }


    private fun setDefaults(user: UserEntity) {
        Log.d(TAG, "setDefaults: user:$user")
        binding.edtFirstName.setText(user.firstName)
        binding.edtLastName.setText(user.lastName)
        binding.edtAge.setText(user.age)
        binding.edtTown.setText(user.town)

        when (user.gender) {
            Gender.MALE -> binding.maleRadioBtn.isChecked = true
            Gender.FEMALE -> binding.femaleRadioBtn.isChecked = true
            else -> binding.maleRadioBtn.isChecked = true
        }


    }


    private fun setOnClickListeners() {
        binding.btnCompleteRegistration.setOnClickListener {
            btnCompleteRegistrationClicked()
        }
    }

    private fun btnCompleteRegistrationClicked() {
        Log.d(TAG, "btnCompleteRegistrationClicked: ")
        binding.apply {
            val firstName = edtFirstName.text.toString().trim()
            val lastName = edtLastName.text.toString().trim()
            val town = edtTown.text.toString().trim()
            val age = edtAge.text.toString().trim()
            val gender = if (radioGroup.checkedRadioButtonId == R.id.maleRadioBtn) {
                "MALE"
            } else {
                "FEMALE"
            }


            viewModel.setEvent(
                UserSetupViewModel.Event.UpdateInstructor(
                    UserEntity(
                        email = navArgs.email,
                        firstName = firstName,
                        lastName = lastName,
                        age = if (age.isBlank()) -1 else age.toInt(),
                        gender = Gender.valueOf(gender),
                        town = town
                    )
                )
            )


        }
    }


    private fun subscribeToObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch {
                viewModel.updateInstructorStatus.collect {
                    Log.d(TAG, "subscribeToObservers: updateInstructorStatus:${it.status.name}")
                    when (it.status) {
                        Resource.Status.LOADING -> {
                            showProgress(true)
                        }
                        Resource.Status.ERROR -> {
                            showProgress(false)
                            showErrorMessage(it.exception!!.message!!)
                        }
                        Resource.Status.SUCCESS -> {
                            showAlerterSuccess("User Updated!")
                            goToProfile(navArgs.email)
                        }
                    }
                }
            }

        }
    }


    private fun showProgress(visible: Boolean) {
        Log.d(TAG, "showProgress: visible:$visible")
        binding.progressBar.showProgress(binding.btnCompleteRegistration, visible)
    }

    private fun showErrorMessage(message: String) {
        showAlerterError(message)
    }

    private fun goBack() {
        findNavController().popBackStack()
    }


}