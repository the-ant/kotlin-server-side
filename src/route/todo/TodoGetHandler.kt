package com.theant.route.todo

import com.theant.repository.todo.TodoRepository
import com.theant.repository.user.UserRepository
import com.theant.response.BaseDataResponse
import com.theant.route.user.getUserFromSessionsOrDb
import com.theant.route.user.userNotExist
import com.theant.service.JwtService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@KtorExperimentalLocationsAPI
fun Route.getTodos(
        jwtService: JwtService,
        userRepository: UserRepository,
        todoRepository: TodoRepository
) {
    authenticate("jwt") {
        get<TodoRoute> {
            val user = call.getUserFromSessionsOrDb(jwtService, userRepository) ?: return@get call.userNotExist()
            try {
                val todos = todoRepository.getTodos(user.id)
                call.respond(HttpStatusCode.OK, BaseDataResponse(HttpStatusCode.OK, todos))
            } catch (e: Exception) {
                application.log.error("Failed to get Todos", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting Todos")
            }
        }
    }
}
