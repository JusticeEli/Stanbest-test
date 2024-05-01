package com.justice.test.stanbest.presentation.ui.onboard.user_setup


import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justice.test.stanbest.presentation.ui.onboard.login.UserEntity

import com.justice.test.stanbest.presentation.ui.onboard.user_setup.UserSetupFragment.*
import com.justice.test.stanbest.utils.Resource
import com.justice.test.stanbest.utils.stringify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSetupViewModel @Inject constructor(
    private val sharedPref: SharedPreferences
) : ViewModel() {

    private val TAG = "InstructorSetupViewMode"

    fun setEvent(event: Event) {
        viewModelScope.launch {
            when (event) {
                is Event.UpdateInstructor -> {
                    updateInstructor(
                        event.instructorData
                    )
                }


            }
        }
    }

    private val _updateInstructorStatus =
        Channel<Resource<Unit>>()
    val updateInstructorStatus = _updateInstructorStatus.receiveAsFlow()
    val ERROR_MESSAGE_SOME_EMPTY_FIELDS = "Some fields are empty"
    private suspend fun updateInstructor(
        instructorDataRaw: UserEntity
    ) {
        val instructorData = instructorDataRaw.copy(
            firstName = instructorDataRaw.firstName.trim(),
            lastName = instructorDataRaw.lastName.trim(),
            age = instructorDataRaw.age,
            town = instructorDataRaw.town.trim(),
        )
        if (fieldsAreEmpty(instructorData)) {
            _updateInstructorStatus.send(
                Resource.error(
                    Exception(
                        ERROR_MESSAGE_SOME_EMPTY_FIELDS
                    )
                )
            )
            return

        }
      sharedPref.edit().putString(instructorData.email,instructorData.stringify).apply()
        _updateInstructorStatus.send(Resource.success(Unit))

    }

    private fun fieldsAreEmpty(instructorData: UserEntity): Boolean {
        return instructorData.firstName.isBlank() || instructorData.lastName.isBlank() || instructorData.age==-1 || instructorData.town.isBlank()
    }

    private val _fetchInstructorStatus =
        MutableStateFlow<Resource<UserEntity>>(Resource.nothing())
    val fetchInstructorStatus = _fetchInstructorStatus.asStateFlow()


    sealed class Event {
        data class UpdateInstructor(
            val instructorData: UserEntity
        ) : Event()


    }
}