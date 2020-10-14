package com.theant.service.auth

import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val algorithm = "HmacSHA1"

@KtorExperimentalAPI
val hasKey = hex(System.getenv("SECRET_KEY"))

@KtorExperimentalAPI
val hmacKey = SecretKeySpec(hasKey, algorithm)

@KtorExperimentalAPI
fun hash(content: String): String {
    val hmac = Mac.getInstance(algorithm).apply { init(hmacKey) }
    return hex(hmac.doFinal(content.toByteArray(Charsets.UTF_8)))
}
