package com.theant.response.auth

import com.theant.model.User


data class SignupResponse(
    val token: String,
    val user: UserResponse
) {

    data class UserResponse(
        val id: Int,
        val userName: String,
        val displayName: String
    )

}

fun User.toUserResponse() = SignupResponse.UserResponse(id, userName, displayName)
