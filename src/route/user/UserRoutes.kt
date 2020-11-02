package com.theant.route.user

import com.theant.API_VERSION
import com.theant.error.ApiError
import com.theant.error.ErrorCode
import com.theant.model.User
import com.theant.repository.user.UserRepository
import com.theant.service.JwtService
import com.theant.service.MySession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USERS_CREATE = "$USERS/register"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USERS_CREATE)
class UserRegisterRoute

@KtorExperimentalLocationsAPI
@Location(USERS)
class UserInfoRoute

@KtorExperimentalLocationsAPI
fun Route.users(
        userRepository: UserRepository,
        jwtService: JwtService,
        hasFunction: (String) -> String
) {
    postUserRegister(userRepository, jwtService, hasFunction)

    postUserLogin(userRepository, jwtService, hasFunction)

    getUserInfo(jwtService, userRepository)
}

suspend fun ApplicationCall.userNotExist() {
    val message = ApiError(
            HttpStatusCode.Unauthorized,
            "User cannot found!",
            listOf(ApiError.Error(ErrorCode.USER_NOT_EXIST, "User not exist!"))
    )
    return respond(HttpStatusCode.Unauthorized, message)
}

suspend fun ApplicationCall.authenticate(
        jwtService: JwtService,
        userRepository: UserRepository
): User? {
    try {
        val authorization = request.headers["Authorization"] ?: ""
        if (authorization.isNotEmpty()) {
            val tokenSplit = authorization.split(" ")
            if (tokenSplit.isNotEmpty()) {
                val userId = jwtService.toClaimValue(this, authorization)
                return userRepository.findUser(userId)
            }
        }
        return null
    } catch (e: Exception) {
        application.log.error("authenticate - error: $e")
        return null
    }
}

suspend fun ApplicationCall.getUserFromSessionsOrDb(
        jwtService: JwtService,
        userRepository: UserRepository
): User? {
    val userInSessions = sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
    application.log.error("Get user from sessions: $userInSessions")
    return authenticate(jwtService, userRepository)
}
