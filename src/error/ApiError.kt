package com.theant.error

import io.ktor.http.*

data class ApiError(
    val status: HttpStatusCode,
    val message: String,
    val errors: List<Error> = listOf()
) {

    data class Error(
        val code: Int,
        val cause: String
    )

}
