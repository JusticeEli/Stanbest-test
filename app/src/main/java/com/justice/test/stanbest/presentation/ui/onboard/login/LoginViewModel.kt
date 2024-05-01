package com.justice.test.stanbest.presentation.ui.onboard.login


import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.justice.test.stanbest.presentation.ui.onboard.forgot_password.ForgotPasswordFragment
import com.justice.test.stanbest.repositories.MainRepository
import com.justice.test.stanbest.utils.Resource
import com.justice.test.stanbest.utils.user
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
     val repo: MainRepository,
    private val sharedPref: SharedPreferences
) : ViewModel() {

    private val TAG = "LoginViewModel"

    val viewModelStates = MutableStateFlow<ViewModelStates>(ViewModelStates.Splash)
    fun setCurrentState(currentState: ViewModelStates) {
        viewModelStates.value = currentState
    }

    fun setEvent(event: Event) {
        viewModelScope.launch {
            when (event) {
                is Event.CheckIfInstructorIsSetup -> {
                    checkIfInstructorIsSetup(event.loginData)
                }
                is Event.CreateAccount -> {
                    createAccount(event.loginData)
                }
                is Event.Login -> {
                    login(event.loginData)
                }

                is Event.CheckIfUserIsLoggedIn -> {
                    checkIfUserIsLoggedIn()
                }
                is Event.ForgotPassword -> {
                    forgotPassword(event.forgotPasswordData)
                }

            }
        }
    }

    private val _forgotPasswordStatus = Channel<Resource<Unit>>()
    val forgotPasswordStatus = _forgotPasswordStatus.receiveAsFlow()
    private suspend fun forgotPassword(forgotPasswordDataRaw: ForgotPasswordFragment.ForgotPasswordData) {
        Log.d(TAG, "forgotPassword: forgotPasswordData:$forgotPasswordDataRaw")

        val forgotPasswordData = forgotPasswordDataRaw.copy(
            email = forgotPasswordDataRaw.email.trim(),
        )
        if (fieldsAreEmptyForgotPassword(forgotPasswordData)) {
            _forgotPasswordStatus.send(Resource.error(Exception("Please fill all fields")))
            return
        }

        repo.forgotPassword(forgotPasswordData).collect {
            Log.d(TAG, "login: status:${it.status.name}")
            _forgotPasswordStatus.send(it)

        }
    }

    private fun fieldsAreEmptyForgotPassword(forgotPasswordData: ForgotPasswordFragment.ForgotPasswordData): Boolean {
        return forgotPasswordData.email.isBlank()
    }

    private val _checkIfUserIsLoggedInStatus = Channel<Event>()
    val checkIfUserIsLoggedInStatus = _checkIfUserIsLoggedInStatus.receiveAsFlow()
    private val SPLASH_SCREEN_DELAY = 2000L
    private suspend fun checkIfUserIsLoggedIn() {
        delay(SPLASH_SCREEN_DELAY)
        if (repo.userIsLoggedIn) {
            _checkIfUserIsLoggedInStatus.send(Event.UserLoggedIn(repo.currentUserEmail!!))

        } else {
            _checkIfUserIsLoggedInStatus.send(Event.UserNotLoggedIn)

        }

    }

    private val _loginStatus = Channel<Resource<Unit>>()
    val loginStatus = _loginStatus.receiveAsFlow()
    private suspend fun login(loginDataRaw: LoginFragment.LoginData) {
        Log.d(TAG, "login: ")
        val loginData = loginDataRaw.copy(
            email = loginDataRaw.email.trim(),
            password = loginDataRaw.password.trim()
        )
        if (fieldsAreEmpty(loginData)) {
            _loginStatus.send(Resource.error(Exception("Please fill all fields")))
            return
        }

        Log.d(TAG, "login: loginData:$loginData")
        repo.login(loginData).collect {
            Log.d(TAG, "login: status:${it.status.name}")

            when (it.status) {
                Resource.Status.LOADING -> {
                    _loginStatus.send(it)

                }
                Resource.Status.SUCCESS -> {
                  _loginStatus.send(it)

                }
                Resource.Status.ERROR -> {

                    when (it.exception) {
                        is FirebaseAuthInvalidUserException -> {
                            Log.e(TAG, "login: ", it.exception)
                            _loginStatus.send(Resource.error(Exception("Please Create Account First")))
                        }
                        else -> {
                            _loginStatus.send(it)

                        }
                    }

                }

            }

        }

    }

    private suspend fun loginSuccess(loginData: LoginFragment.LoginData) {

        Log.d(TAG, "loginSuccess: ")
        checkIfInstructorIsSetup(loginData)
    }


    private val _registerStatus = Channel<Resource<Unit>>()
    val registerStatus = _registerStatus.receiveAsFlow()
    private suspend fun register(
        loginData: LoginFragment.LoginData,
    ) {
        Log.d(TAG, "register: loginData:$loginData")
        repo.register(loginData).collect {
            _registerStatus.send(it)

        }
    }


    private fun fieldsAreEmpty(loginData: LoginFragment.LoginData) =
        loginData.email.isBlank() || loginData.password.isBlank()


    private val _checkInstructorSetupStatus = Channel<Resource<Event>>()
    val checkInstructorSetupStatus = _checkInstructorSetupStatus.receiveAsFlow()

    private suspend fun checkIfInstructorIsSetup(loginDataRaw: LoginFragment.LoginData) {
        Log.d(TAG, "checkInstructorSetup: instructor:${sharedPref.user}")

        //..
        val loginData = loginDataRaw.copy(
            email = loginDataRaw.email.trim(),
            password = loginDataRaw.password.trim()
        )
        if (fieldsAreEmpty(loginData)) {
            _checkInstructorSetupStatus.send(Resource.error(Exception("Please fill all fields")))
            return
        }


    }


    private suspend fun createOneInstructor(loginData: LoginFragment.LoginData) {


    }


    private val _createAccountStatus = Channel<Resource<Event>>()
    val createAccountStatus = _createAccountStatus.receiveAsFlow()
    private suspend fun createAccount(loginDataRaw: LoginFragment.LoginData) {
        Log.d(TAG, "createAccount: instructor:${sharedPref.user}")

        //..
        val loginData = loginDataRaw.copy(
            email = loginDataRaw.email.trim(),
            password = loginDataRaw.password.trim()
        )
        if (fieldsAreEmpty(loginData)) {
            _createAccountStatus.send(Resource.error(Exception("Please fill all fields")))
            return
        }

        register(loginData = loginData)

    }


    sealed class Event {
        object CheckIfUserIsLoggedIn : Event()

        object GoToMainScreen : Event()
        data class UserLoggedIn(val email:String) : Event()
        object UserNotLoggedIn : Event()
        data class InstructorHasNotSetupHisPersonalInfo(val instructor: UserEntity) :
            Event()

        data class CreateAccount(val loginData: LoginFragment.LoginData) : Event()
        data class CheckIfInstructorIsSetup(val loginData: LoginFragment.LoginData) : Event()
        data class Login(val loginData: LoginFragment.LoginData) : Event()
        data class ForgotPassword(val forgotPasswordData: ForgotPasswordFragment.ForgotPasswordData) :
            Event()

    }

    sealed class ViewModelStates {
        object Login : ViewModelStates()
        object Register : ViewModelStates()
        object Splash : ViewModelStates()
        object ForgotPassword : ViewModelStates()
    }
}