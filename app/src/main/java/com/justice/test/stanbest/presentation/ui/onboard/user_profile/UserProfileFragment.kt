package com.justice.test.stanbest.presentation.ui.onboard.user_profile


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.firebase.ui.auth.data.model.User
import com.justice.test.stanbest.R
import com.justice.test.stanbest.databinding.FragmentUserProfileBinding
import com.justice.test.stanbest.presentation.ui.onboard.login.UserEntity
import com.justice.test.stanbest.presentation.ui.onboard.user_profile.dialogs.EditInstructorCheckBoxFragment
import com.justice.test.stanbest.presentation.ui.onboard.user_profile.dialogs.EditInstructorTextFieldFragment
import com.justice.test.stanbest.presentation.ui.onboard.user_setup.UserSetupFragmentArgs
import com.justice.test.stanbest.presentation.ui.onboard.user_setup.UserSetupViewModel
import com.justice.test.stanbest.utils.Resource
import com.justice.test.stanbest.utils.showAlerterError
import com.justice.test.stanbest.utils.showProgress

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {
    private val TAG = "InstructorProfileFragme"

    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var binding: FragmentUserProfileBinding
    private val viewModel: InstructorProfileViewModel by viewModels()
    private val instructorSetupViewModel: UserSetupViewModel by viewModels()
    private val navArgs by navArgs<UserProfileFragmentArgs>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserProfileBinding.bind(view)
        subScribeToObservers()
        viewModel.setEvent(InstructorProfileViewModel.Event.FetchInstructor(instructorId = navArgs.email))
    }


    private fun subScribeToObservers() {
        Log.d(TAG, "subScribeToObservers: ")
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            launch {
                viewModel.fetchInstructorStatus.collect {
                    Log.d(TAG, "subScribeToObservers: status:${it.status.name}")
                    when (it.status) {
                        Resource.Status.LOADING -> {
                            showProgress(true)
                        }

                        Resource.Status.SUCCESS -> {
                            setDefault(it.data!!)
                            setOnClickListeners(it.data!!)
                            showProgress(false)
                        }
                        Resource.Status.ERROR -> {
                            showAlerterError(message = it.message!!)
                            showProgress(false)
                        }
                    }
                }
            }
            launch {
                instructorSetupViewModel.fetchInstructorStatus.collect {
                    Log.d(TAG, "subScribeToObservers: status:${it.status.name}")
                    when (it.status) {
                        Resource.Status.LOADING -> {
                            showProgress(true)
                        }

                        Resource.Status.SUCCESS -> {
                            setDefault(it.data!!)
                            setOnClickListeners(it.data!!)
                            showProgress(false)
                        }
                        Resource.Status.ERROR -> {
                            showAlerterError(message = it.message!!)
                            showProgress(false)
                        }
                    }
                }
            }
            launch {
                viewModel.deleteAccountStatus.collect {
                    Log.d(TAG, "subScribeToObservers: status:${it.status.name}")
                    when (it.status) {
                        Resource.Status.LOADING -> {
                            showProgress(true)
                        }

                        Resource.Status.SUCCESS -> {
                            requireActivity().finish()
                            showProgress(false)
                        }
                        Resource.Status.ERROR -> {
                            showAlerterError(message = it.message!!)
                            showProgress(false)
                        }
                    }
                }
            }


        }
    }

    private fun showProgress(visible: Boolean) {
        Log.d(TAG, "showProgress: ")
        requireActivity().showProgress(visible)
    }


    private fun setDefault(instructor: UserEntity) {
        Log.d(TAG, "setDefault: instructor:$instructor")

        binding.tvEmail.text = instructor.email
        binding.tvFirstName.text = instructor.firstName
        binding.tvLastName.text = instructor.lastName
        binding.tvAge.text = instructor.age.toString()
        binding.tvTown.text = instructor.town
        binding.tvGender.text = instructor.gender?.name


    }

    private fun setOnClickListeners(instructor: UserEntity) {
        binding.btnDeleteAccount.setOnClickListener {
            deleteAccount()
        }
        binding.ivEmail.setOnClickListener {
            val dialog = EditInstructorTextFieldFragment(
                editValue = EditInstructorTextFieldFragment.EditValue(
                    title = "Email",
                    value = instructor.email
                ), onSave = { result ->

                    val instructorData = getInstructorData(instructor).copy(email = result)
                    updateInstructor(instructorData)

                }
            )
            dialog.show(requireActivity().supportFragmentManager, null)

        }



        binding.ivFirstName.setOnClickListener {
            val dialog = EditInstructorTextFieldFragment(
                editValue = EditInstructorTextFieldFragment.EditValue(
                    title = "First Name",
                    value = instructor.firstName
                ), onSave = { result ->

                    val instructorData = getInstructorData(instructor).copy(firstName = result)
                    updateInstructor(instructorData)

                }
            )
            dialog.show(requireActivity().supportFragmentManager, null)

        }



        binding.ivLastName.setOnClickListener {
            val dialog = EditInstructorTextFieldFragment(
                editValue = EditInstructorTextFieldFragment.EditValue(
                    title = "Last Name",
                    value = instructor.lastName
                ), onSave = { result ->

                    val instructorData = getInstructorData(instructor).copy(lastName = result)
                    updateInstructor(instructorData)

                }
            )
            dialog.show(requireActivity().supportFragmentManager, null)

        }
        binding.ivTown.setOnClickListener {
            val dialog = EditInstructorTextFieldFragment(
                editValue = EditInstructorTextFieldFragment.EditValue(
                    title = "Town",
                    value = instructor.town
                ), onSave = { result ->

                    val instructorData = getInstructorData(instructor).copy(town = result)
                    updateInstructor(instructorData)

                }
            )
            dialog.show(requireActivity().supportFragmentManager, null)

        }


        binding.ivAge.setOnClickListener {
            val dialog = EditInstructorTextFieldFragment(
                editValue = EditInstructorTextFieldFragment.EditValue(
                    title = "Age",
                    value = instructor.age.toString()
                ), onSave = { result ->

                    val instructorData =
                        getInstructorData(instructor).copy(age = if (result.isBlank()) -1 else result.toInt())
                    updateInstructor(instructorData)

                }
            )
            dialog.show(requireActivity().supportFragmentManager, null)

        }
        binding.ivGender.setOnClickListener {
            val dialog = EditInstructorCheckBoxFragment(currentGender = instructor.gender,
                onSave = { gender ->

                    val instructorData = getInstructorData(instructor).copy(gender = gender)
                    updateInstructor(instructorData)

                }
            )
            dialog.show(requireActivity().supportFragmentManager, null)

        }


    }

    private fun deleteAccount() {
        viewModel.setEvent(InstructorProfileViewModel.Event.DeleteAccount(navArgs.email))
    }

    private fun getInstructorData(instructor: UserEntity): UserEntity {
        val instructorData = instructor.copy(

            firstName = instructor.firstName!!,
            lastName = instructor.lastName!!,
            age = instructor.age!!,
            gender = instructor.gender!!,
            email = instructor.email
        )
        return instructorData
    }

    private fun updateInstructor(instructorData: UserEntity) {
        Log.d(TAG, "updateInstructor: ")
        setDefault(instructorData)
        instructorSetupViewModel.setEvent(
            UserSetupViewModel.Event.UpdateInstructor(
                instructorData
            )
        )


    }
}

