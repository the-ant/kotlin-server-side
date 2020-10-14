package com.theant.repository.todo

import com.theant.model.Todo

interface TodoRepository {

    suspend fun addTodo(userId: Int, todo: String, done: Boolean): Todo?

    suspend fun getTodos(userId: Int): List<Todo>

    suspend fun finTodo(todoId: Int): Todo?

    suspend fun updateTodo(todoId: Int, todo: String, done: Boolean)

}
