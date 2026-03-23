package org.delcom.repositories

import org.delcom.entities.RefreshToken

interface IRefreshTokenRepository {

    suspend fun createToken(token: RefreshToken): RefreshToken

    suspend fun getToken(refreshToken: String): RefreshToken?

    suspend fun deleteToken(refreshToken: String)

}