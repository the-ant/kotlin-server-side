package com.theant.repository.todo

import com.theant.error.todo.TodoNotExistException
import com.theant.factory.DataFactory.dbQuery
import com.theant.model.Todo
import com.theant.table.Todos
import io.ktor.application.*
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update

class TodoRepositoryImpl : TodoRepository {

    override suspend fun addTodo(userId: Int, todo: String, done: Boolean): Todo? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Todos.insert {
                it[Todos.userId] = userId
                it[Todos.todo] = todo
                it[Todos.done] = done
            }
        }
        return statement?.resultedValues?.get(0).toTodo()
    }

    override suspend fun getTodos(userId: Int): List<Todo> = dbQuery {
        Todos.select { Todos.userId.eq(userId) }
            .mapNotNull { it.toTodo() }
    }

    override suspend fun findTodo(todoId: Int): Todo? = dbQuery {
        Todos.select { Todos.id.eq(todoId) }
            .mapNotNull { it.toTodo() }
            .singleOrNull()
    }

    override suspend fun updateTodo(todoId: Int, todo: String, done: Boolean): Unit = dbQuery {
        val todoObj = Todos.select { Todos.id.eq(todoId) }
            .mapNotNull { it.toTodo() }
            .singleOrNull()
        if (todoObj != null) {
            Todos.update({ Todos.id.eq(todoId) }) {
                it[Todos.todo] = todo
                it[Todos.done] = done
            }
        } else {
            throw TodoNotExistException(todoId)
        }
    }

    private fun ResultRow?.toTodo(): Todo? {
        val row = this ?: return null
        return Todo(
            id = row[Todos.id],
            userId = row[Todos.userId],
            todo = row[Todos.todo],
            done = row[Todos.done]
        )
    }

}
