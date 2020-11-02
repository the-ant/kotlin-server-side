package com.theant.model

import io.ktor.auth.*
import java.io.Serializable

data class Video(
        val id: Int,
        val title: String,
        val speaker:String,
        val url: String
): Serializable, Principal
