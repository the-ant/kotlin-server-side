package com.theant.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Todos : Table() {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val userId: Column<Int> = integer("user_id").references(Users.userId)
    val todo: Column<String> = varchar("todo", 512)
    val done: Column<Boolean> = bool("done")
}
