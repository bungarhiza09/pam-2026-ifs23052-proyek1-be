package org.delcom.repositories

import org.delcom.dao.RefreshTokenDAO
import org.delcom.entities.RefreshToken
import org.delcom.helpers.refreshTokenDAOToModel
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.RefreshTokenTable
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID
import org.delcom.tables.UserTable
class RefreshTokenRepository : IRefreshTokenRepository {

    override suspend fun createToken(token: RefreshToken): RefreshToken = suspendTransaction {

        val dao = RefreshTokenDAO.new {
            userId = UUID.fromString(token.userId)
            refreshToken = token.refreshToken
            authToken = token.authToken
        }

        refreshTokenDAOToModel(dao)
    }

    override suspend fun getToken(refreshToken: String): RefreshToken? = suspendTransaction {

        RefreshTokenDAO.find { RefreshTokenTable.refreshToken eq refreshToken }
            .firstOrNull()
            ?.let { refreshTokenDAOToModel(it) }

    }

    override suspend fun deleteToken(refreshToken: String) = suspendTransaction {

        RefreshTokenDAO.find { RefreshTokenTable.refreshToken eq refreshToken }
            .forEach { it.delete() }

    }
}