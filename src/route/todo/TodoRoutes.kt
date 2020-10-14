package com.theant.route.todo

import com.theant.API_VERSION
import com.theant.repository.todo.TodoRepository
import com.theant.repository.user.UserRepository
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
    userRepository: UserRepository,
    todoRepository: TodoRepository
) {
    postTodoCreate(userRepository, todoRepository)

    putTodoUpdate(userRepository, todoRepository)
}