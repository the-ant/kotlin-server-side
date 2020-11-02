package com.theant.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.theant.model.User
import io.ktor.application.*
import java.util.*


class JwtService {

    private val issuer = "todoServer"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    val CLAIM_NAME = "id"

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: User): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim(CLAIM_NAME, user.id)
        .withExpiresAt(expiresAt())
        .sign(algorithm)

    private fun expiresAt() = Date(System.currentTimeMillis() + 3_600_000 * 24) // 24 hours

    fun toClaimValue(appCall: ApplicationCall, authorization: String): Int {
        return try {
            appCall.application.log.debug("Authorization = $authorization")
            val tokenSplit = authorization.split(" ")
            val token = tokenSplit[1]
            appCall.application.log.debug("Authorization - token = $token")
            val jwt = JWT.decode(token)
            val userId = jwt.getClaim(CLAIM_NAME).asInt()
            appCall.application.log.debug("Authorization - userId = $userId")
            userId
        } catch (e: Exception) {
            appCall.application.log.error("toClaimValue - error = $e")
            -1
        }
    }

}
