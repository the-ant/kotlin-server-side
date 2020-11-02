package com.theant

import com.fasterxml.jackson.databind.SerializationFeature
import com.theant.factory.DataFactory
import com.theant.repository.todo.TodoRepositoryImpl
import com.theant.repository.user.UserRepositoryImpl
import com.theant.repository.video.VideoRepositoryImpl
import com.theant.response.auth.SignupResponse
import com.theant.route.todo.todos
import com.theant.route.user.users
import com.theant.route.video.videos
import com.theant.service.JwtService
import com.theant.service.MySession
import com.theant.service.auth.hash
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
@KtorExperimentalAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations) {
    }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    DataFactory.initial()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }
    val userRepository = UserRepositoryImpl()
    val todoRepository = TodoRepositoryImpl()
    val videoRepository = VideoRepositoryImpl()

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Kotlin Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim(jwtService.CLAIM_NAME)
                val userId = claim.asInt()
                userRepository.findUser(userId)
            }
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        users(userRepository, jwtService, hashFunction)

        todos(jwtService, userRepository, todoRepository)

        videos(videoRepository)

        get("/") {
            call.respondText { "Hello World!!!!!!!!!!" }
        }
    }
}
