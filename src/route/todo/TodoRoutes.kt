package com.theant.route.todo

import com.theant.API_VERSION
import com.theant.repository.todo.TodoRepository
import com.theant.repository.user.UserRepository
import com.theant.service.JwtService
import io.ktor.locations.*
import io.ktor.routing.*

const val TODOS = "$API_VERSION/todos"
const val TODOS_UPDATE = "$TODOS/{id}"

@KtorExperimentalLocationsAPI
@Location(TODOS)
class TodoRoute

@KtorExperimentalLocationsAPI
@Location(TODOS_UPDATE)
data class TodoUpdateRoute(val id: Int)

@KtorExperimentalLocationsAPI
fun Route.todos(
        jwtService: JwtService,
        userRepository: UserRepository,
        todoRepository: TodoRepository
) {
    postTodoCreate(jwtService, userRepository, todoRepository)

    putTodoUpdate(jwtService, userRepository, todoRepository)

    getTodos(jwtService, userRepository, todoRepository)
}