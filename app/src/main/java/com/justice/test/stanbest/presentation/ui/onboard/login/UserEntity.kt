package com.justice.test.stanbest.presentation.ui.onboard.login

data class UserEntity(
    val email: String,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val town: String,
    val gender: Gender
)

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}