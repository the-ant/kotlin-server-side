package com.theant.repository.user

import com.theant.error.auth.UserAlreadyExistsException
import com.theant.factory.DataFactory.dbQuery
import com.theant.model.User
import com.theant.table.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement

class UserRepositoryImpl: UserRepository {

    override suspend fun addUser(
        userName: String,
        displayName: String,
        passwordHash: String
    ): User? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            val existedUser = Users.select { Users.userName.eq(userName) }
                .singleOrNull()

            if (existedUser == null) {
                statement = Users.insert { user ->
                    user[Users.userName] = userName
                    user[Users.displayName] = displayName
                    user[Users.passwordHash] = passwordHash
                }
            } else {
                throw UserAlreadyExistsException(existedUser.toUser()!!)
            }
        }
        return statement?.resultedValues?.get(0).toUser()
    }

    override suspend fun findUser(userId: Int): User? = dbQuery {
        Users.select { Users.userId.eq(userId) }
            .map { it.toUser() }
            .singleOrNull()
    }

    override suspend fun findUserByUserName(userName: String): User? = dbQuery {
        Users.select { Users.userName.eq(userName) }
            .map { it.toUser() }
            .singleOrNull()
    }

    private fun ResultRow?.toUser(): User? {
        val row = this ?: return null
        return User(
            id = row[Users.userId],
            userName = row[Users.userName],
            displayName = row[Users.displayName],
            passwordHash = row[Users.passwordHash]
        )
    }

}
