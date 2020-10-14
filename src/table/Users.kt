package com.theant.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val userId: Column<Int> = integer("id").autoIncrement().primaryKey()
    val userName: Column<String> = varchar("user_name", 128).uniqueIndex()
    val displayName: Column<String> = varchar("display_name", 256)
    val passwordHash : Column<String> = varchar("password_hash", 64)
}
