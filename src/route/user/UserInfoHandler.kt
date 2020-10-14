package com.theant.route.user

import com.theant.error.ApiError
import com.theant.error.ErrorCode
import com.theant.repository.user.UserRepository
import com.theant.response.BaseDataResponse
import com.theant.response.auth.toUserInfoResponse
import com.theant.service.MySession
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*


@KtorExperimentalLocationsAPI
fun Route.getUserInfo(userRepository: UserRepository) {
    authenticate("jwt") {
        get<UserInfoRoute> {
            val user = call.sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
            if (user == null) {
                val message = ApiError(
                    HttpStatusCode.Unauthorized,
                    "User cannot found!",
                    listOf(ApiError.Error(ErrorCode.USER_NOT_EXIST, "User not exist!"))
                )
                return@get call.respond(HttpStatusCode.Unauthorized, message)
            }

            try {
                call.respond(HttpStatusCode.OK, BaseDataResponse(HttpStatusCode.OK, user.toUserInfoResponse()))
            } catch (e: Exception) {
                application.log.error("Failed to get user info", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting User Info!")
            }
        }
    }
}
