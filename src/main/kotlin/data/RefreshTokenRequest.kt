package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String,
    val authToken: String
) {
    fun toMap(): Map<String, String?> {
        return mapOf(
            "refreshToken" to refreshToken,
            "authToken" to authToken
        )
    }
}