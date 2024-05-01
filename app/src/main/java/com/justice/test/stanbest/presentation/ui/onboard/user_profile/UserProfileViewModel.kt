package com.justice.test.stanbest.presentation.ui.onboard.user_profile


import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justice.test.stanbest.presentation.ui.onboard.login.UserEntity
import com.justice.test.stanbest.repositories.MainRepository
import com.justice.test.stanbest.utils.Resource
import com.justice.test.stanbest.utils.user
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InstructorProfileViewModel @Inject constructor(
    val sharedPref: SharedPreferences,
    val repo: MainRepository
) :
    ViewModel() {

    private val TAG = "MainViewModel"


    fun setEvent(event: Event) = viewModelScope.launch {
        when (event) {
            is Event.FetchInstructor -> {
                fetchInstructor(instructorId = event.instructorId)
            }
            is Event.DeleteAccount -> {
                deleteAccount(email = event.email)
            }
        }
    }

    private val _deleteAccountStatus = Channel<Resource<Unit>>()
    val deleteAccountStatus = _deleteAccountStatus.receiveAsFlow()
    private suspend fun deleteAccount(email: String) {
        sharedPref.edit().clear().apply()
        repo.logout().collect {
            _deleteAccountStatus.send(it)
        }
    }

    private val _fetchInstructorStatus = Channel<Resource<UserEntity>>()
    val fetchInstructorStatus = _fetchInstructorStatus.receiveAsFlow()
    private suspend fun fetchInstructor(instructorId: String) {
        Log.d(TAG, "fetchInstructor: instructorId:$instructorId")
        val userString = sharedPref.getString(instructorId, null)!!

        _fetchInstructorStatus.send(Resource.success(userString.user))

    }


    sealed class Event {
        data class FetchInstructor(val instructorId: String) : Event()
        data class DeleteAccount(val email: String) : Event()
    }
}