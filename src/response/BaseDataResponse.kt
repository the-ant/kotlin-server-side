package com.theant.response

import io.ktor.http.*

data class BaseDataResponse<T>(
    val status: HttpStatusCode,
    val data: T
)
