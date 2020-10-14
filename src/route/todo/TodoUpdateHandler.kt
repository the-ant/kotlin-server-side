package com.theant.route.todo

import com.theant.error.ApiError
import com.theant.error.ErrorCode
import com.theant.model.Todo
import com.theant.repository.todo.TodoRepository
import com.theant.repository.user.UserRepository
import com.theant.response.BaseDataResponse
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
fun Route.putTodoUpdate(
    userRepository: UserRepository,
    todoRepository: TodoRepository
) {
    authenticate("jwt") {
        put<TodoUpdateRoute> {
            val user = call.sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
            if (user == null) {
                val message = ApiError(
                    HttpStatusCode.Unauthorized,
                    "User cannot found!",
                    listOf(ApiError.Error(ErrorCode.USER_NOT_EXIST, "User not exist!"))
                )
                return@put call.respond(HttpStatusCode.Unauthorized, message)
            }

            val todoId = call.parameters["id"]
            val params = call.receive<Parameters>()
            val todoTitle = params["todo"] ?: ""
            val done = params["done"]?.toBoolean() ?: false

            if (todoId.isNullOrBlank()) {
                val message = ApiError(
                    HttpStatusCode.BadRequest,
                    "Missing Fields!",
                    listOf(ApiError.Error(ErrorCode.MISSING_TODO, "You are querying on null or empty todo id!"))
                )
                return@put call.respond(HttpStatusCode.BadRequest, message)
            }

            val todo = todoRepository.findTodo(todoId.toInt())
            if (todo == null) {
                val message = ApiError(
                    HttpStatusCode.BadRequest,
                    "Cannot find TODO!",
                    listOf(ApiError.Error(ErrorCode.TODO_NOT_EXIST, "Cannot find TODO with id is $todoId!"))
                )
                return@put call.respond(HttpStatusCode.BadRequest, message)
            }

            try {
                todoRepository.updateTodo(todoId.toInt(), todoTitle, done)
                val response = BaseDataResponse(HttpStatusCode.OK, Todo(todoId.toInt(), user.id, todoTitle, done))
                call.respond(HttpStatusCode.OK, response)
            } catch (e: Exception) {
                application.log.error("Failed to update todo!", e)
                call.respond(HttpStatusCode.BadRequest, "Problems updating Todo!")
            }
        }
    }
}

