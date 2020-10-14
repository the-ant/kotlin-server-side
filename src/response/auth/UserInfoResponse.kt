package com.theant.response.auth

import com.theant.model.User

data class UserInfoResponse(
    val id: Int,
    val userName: String,
    val displayName: String
)

fun User.toUserInfoResponse() = UserInfoResponse(id, userName, displayName)
