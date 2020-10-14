package com.theant.route.user

import com.theant.API_VERSION
import com.theant.error.ApiError
import com.theant.error.ErrorCode
import com.theant.error.auth.UserAlreadyExistsException
import com.theant.repository.user.UserRepository
import com.theant.response.BaseDataResponse
import com.theant.response.auth.SignupResponse
import com.theant.response.auth.toUserResponse
import com.theant.service.JwtService
import com.theant.service.auth.MySession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USERS_CREATE = "$USERS/create"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USERS_CREATE)
class UserCreateRoute

@KtorExperimentalLocationsAPI
fun Route.users(
    userRepository: UserRepository,
    jwtService: JwtService,
    hasFunction: (String) -> String
) {
    createUser(userRepository, jwtService, hasFunction)
}

@KtorExperimentalLocationsAPI
fun Route.createUser(
    userRepository: UserRepository,
    jwtService: JwtService,
    hasFunction: (String) -> String
) {
    post<UserCreateRoute> {
        val signupParams = call.receive<Parameters>()
        val userName = signupParams["userName"]
        val password = signupParams["password"]
        val displayName = signupParams["displayName"]

        if (userName.isNullOrBlank() || password.isNullOrBlank() || displayName.isNullOrBlank()) {
            val errors = arrayListOf<ApiError.Error>()
            if (userName.isNullOrBlank())
                errors.add(ApiError.Error(ErrorCode.MISSING_USERNAME, "Missing User Name Field!"))
            if (password.isNullOrBlank())
                errors.add(ApiError.Error(ErrorCode.MISSING_PASSWORD, "Missing Password Field!"))
            if (displayName.isNullOrBlank())
                errors.add(ApiError.Error(ErrorCode.MISSING_DISPLAY_NAME, "Missing Display Name Field!"))

            val message = ApiError(HttpStatusCode.BadRequest, "Missing Fields!", errors)
            return@post call.respond(HttpStatusCode.BadRequest, message)
        }

        val hashedPassword = hasFunction(password)

        try {
            userRepository.addUser(userName, displayName, hashedPassword)?.let { user ->
                call.apply {
                    sessions.set(MySession(user.id))
                    val response = BaseDataResponse(
                        HttpStatusCode.Created,
                        SignupResponse(jwtService.generateToken(user), user.toUserResponse())
                    )
                    respond(HttpStatusCode.Created, response)
                }
            }
        } catch (e: Exception) {
            when (e) {
                is UserAlreadyExistsException -> {
                    application.log.error("UserAlreadyExistsException: ", e.message)
                    call.handleUserAlreadyExistsException(e)
                }
                else -> {
                    application.log.error("Failed to create user!", e)
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ApiError(HttpStatusCode.BadRequest, "Problems creating User!")
                    )
                }
            }
        }
    }
}

private suspend fun ApplicationCall.handleUserAlreadyExistsException(e: UserAlreadyExistsException) {
    val error = ApiError(
        HttpStatusCode.BadRequest,
        "User already exists!",
        listOf(ApiError.Error(ErrorCode.USER_ALREADY_EXISTS, "User Name already exists!"))
    )
    respond(HttpStatusCode.BadRequest, error)
}
