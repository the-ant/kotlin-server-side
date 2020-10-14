package com.theant.route.user

import com.theant.error.ApiError
import com.theant.error.ErrorCode
import com.theant.error.auth.UserAlreadyExistsException
import com.theant.repository.user.UserRepository
import com.theant.response.BaseDataResponse
import com.theant.response.auth.SignupResponse
import com.theant.response.auth.toUserResponse
import com.theant.service.JwtService
import com.theant.service.MySession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*


@KtorExperimentalLocationsAPI
fun Route.postUserLogin(
    userRepository: UserRepository,
    jwtService: JwtService,
    hasFunction: (String) -> String
) {
    post<UserLoginRoute> {
        val signupParams = call.receive<Parameters>()
        val userName = signupParams["userName"]
        val password = signupParams["password"]

        if (userName.isNullOrBlank() || password.isNullOrBlank()) {
            val errors = arrayListOf<ApiError.Error>()
            if (userName.isNullOrBlank())
                errors.add(ApiError.Error(ErrorCode.MISSING_USERNAME, "Missing User Name Field!"))
            if (password.isNullOrBlank())
                errors.add(ApiError.Error(ErrorCode.MISSING_PASSWORD, "Missing Password Field!"))

            val message = ApiError(HttpStatusCode.BadRequest, "Missing Fields!", errors)
            return@post call.respond(HttpStatusCode.BadRequest, message)
        }

        val hashedPassword = hasFunction(password)

        try {
            userRepository.findUserByUserName(userName)?.let { user ->
                if (hashedPassword == user.passwordHash) {
                    call.apply {
                        sessions.set(MySession(user.id))
                        val response = BaseDataResponse(
                            HttpStatusCode.OK,
                            SignupResponse(jwtService.generateToken(user), user.toUserResponse())
                        )
                        respond(HttpStatusCode.OK, response)
                    }
                } else {
                    val message = ApiError(
                        HttpStatusCode.BadRequest,
                        "Login Failed!",
                        listOf(ApiError.Error(ErrorCode.USER_WRONG_PASSWORD, "Your password is invalid!"))
                    )
                    call.respond(HttpStatusCode.BadRequest, message)
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