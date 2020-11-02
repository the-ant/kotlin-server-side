package com.theant.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Videos : Table() {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val title: Column<String> = varchar("title", 256).uniqueIndex()
    val speaker: Column<String> = varchar("speaker", 256)
    val url : Column<String> = varchar("url", 256)
}
