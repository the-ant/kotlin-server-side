package com.theant.model

data class Todo(
    val id: Int,
    val userId: Int,
    val todo: String,
    val done: Boolean
)
