package com.theant.route.todo

import com.theant.error.ApiError
import com.theant.error.ErrorCode
import com.theant.repository.todo.TodoRepository
import com.theant.repository.user.UserRepository
import com.theant.response.BaseDataResponse
import com.theant.route.user.getUserFromSessionsOrDb
import com.theant.route.user.userNotExist
import com.theant.service.JwtService
import com.theant.service.MySession
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*


@KtorExperimentalLocationsAPI
fun Route.postTodoCreate(
        jwtService: JwtService,
        userRepository: UserRepository,
        todoRepository: TodoRepository
) {
    authenticate("jwt") {
        post<TodoRoute> {
            val user = call.getUserFromSessionsOrDb(jwtService, userRepository) ?: return@post call.userNotExist()

            val params = call.receive<Parameters>()
            val todo = params["todo"]
            val done = params["done"] ?: "false"

            if (todo.isNullOrBlank()) {
                val message = ApiError(
                        HttpStatusCode.BadRequest,
                        "Missing Fields!",
                        listOf(ApiError.Error(ErrorCode.MISSING_TODO, "Missing TODO title Field!"))
                )
                return@post call.respond(HttpStatusCode.BadRequest, message)
            }

            try {
                val currentTodo = todoRepository.addTodo(user.id, todo, done.toBoolean())
                currentTodo?.id?.let {
                    val response = BaseDataResponse(HttpStatusCode.OK, currentTodo)
                    call.respond(HttpStatusCode.OK, response)
                }
            } catch (e: Exception) {
                application.log.error("Failed to add todo", e)
                call.respond(HttpStatusCode.BadRequest, "Problems creating Todo")
            }
        }
    }
}
