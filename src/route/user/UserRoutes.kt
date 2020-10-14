package com.theant.route.user

import com.theant.API_VERSION
import com.theant.repository.user.UserRepository
import com.theant.service.JwtService
import io.ktor.locations.*
import io.ktor.routing.*

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
fun Route.users(
    userRepository: UserRepository,
    jwtService: JwtService,
    hasFunction: (String) -> String
) {
    postUserRegister(userRepository, jwtService, hasFunction)

}
