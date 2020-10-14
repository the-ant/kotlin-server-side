package com.theant.repository.user

import com.theant.model.User

interface UserRepository {

    suspend fun addUser(
        userName: String,
        displayName: String,
        passwordHash: String
    ): User?

    suspend fun findUser(userId: Int): User?

    suspend fun findUserByUserName(userName: String): User?

}
