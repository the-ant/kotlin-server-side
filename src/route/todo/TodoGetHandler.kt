package com.theant.route.todo

import com.theant.error.ApiError
import com.theant.error.ErrorCode
import com.theant.repository.todo.TodoRepository
import com.theant.repository.user.UserRepository
import com.theant.response.BaseDataResponse
import com.theant.service.MySession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

@KtorExperimentalLocationsAPI
fun Route.getTodos(
    userRepository: UserRepository,
    todoRepository: TodoRepository
) {
    get<TodoRoute> {
        val user = call.sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
        if (user == null) {
            val message = ApiError(
                HttpStatusCode.Unauthorized,
                "Your session has expired!",
                listOf(ApiError.Error(ErrorCode.USER_TOKEN_EXPIRED, "Your token has expired!"))
            )
            return@get call.respond(HttpStatusCode.Unauthorized, message)
        }

        try {
            val todos = todoRepository.getTodos(user.id)
            call.respond(HttpStatusCode.OK, BaseDataResponse(HttpStatusCode.OK, todos))
        } catch (e: Exception) {
            application.log.error("Failed to get Todos", e)
            call.respond(HttpStatusCode.BadRequest, "Problems getting Todos")
        }
    }
}
