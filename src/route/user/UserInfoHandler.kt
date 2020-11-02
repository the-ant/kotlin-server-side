package com.theant.route.user

import com.auth0.jwt.JWT
import com.theant.error.ApiError
import com.theant.error.ErrorCode
import com.theant.repository.user.UserRepository
import com.theant.response.BaseDataResponse
import com.theant.response.auth.toUserInfoResponse
import com.theant.service.JwtService
import com.theant.service.MySession
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*


@KtorExperimentalLocationsAPI
fun Route.getUserInfo(jwtService: JwtService, userRepository: UserRepository) {
    authenticate("jwt") {
        get<UserInfoRoute> {
            val user = call.getUserFromSessionsOrDb(jwtService, userRepository)
            if (user != null) {
                return@get call.respond(
                        HttpStatusCode.OK,
                        BaseDataResponse(
                                HttpStatusCode.OK,
                                user.toUserInfoResponse()
                        )
                )
            }
            return@get call.userNotExist()
        }
    }
}
