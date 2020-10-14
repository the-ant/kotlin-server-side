package com.theant.model

import io.ktor.auth.*
import java.io.Serializable

data class User(
    val id: Int,
    val userName: String,
    val passwordHash: String,
    val displayName: String
): Serializable, Principal
