package org.delcom.repositories

import org.delcom.dao.UserDAO
import org.delcom.entities.User
import org.delcom.helpers.suspendTransaction
import org.delcom.helpers.userDAOToModel
import org.delcom.tables.UserTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.util.UUID

class UserRepository : IUserRepository {

    override suspend fun createUser(user: User): User = suspendTransaction {

        val dao = UserDAO.new {
            name = user.name
            username = user.username
            password = user.password
            bio = user.bio
            photo = user.photo
        }

        userDAOToModel(dao)
    }

    override suspend fun getUserById(id: String): User? = suspendTransaction {
        UserDAO.findById(UUID.fromString(id))?.let { userDAOToModel(it) }
    }

    override suspend fun getUserByUsername(username: String): User? = suspendTransaction {

        UserDAO.find { UserTable.username eq username }
            .firstOrNull()
            ?.let { userDAOToModel(it) }

    }

    override suspend fun updateUserProfile(
        id: String,
        bio: String?,
        photo: String?
    ): Boolean = suspendTransaction {

        val user = UserDAO.findById(UUID.fromString(id)) ?: return@suspendTransaction false

        user.bio = bio
        user.photo = photo

        true
    }

    override suspend fun updatePassword(
        id: String,
        oldPassword: String,
        newPassword: String
    ): Boolean = suspendTransaction {

        val user = UserDAO.findById(UUID.fromString(id))
            ?: return@suspendTransaction false

        if (user.password != oldPassword) {
            return@suspendTransaction false
        }

        user.password = newPassword
        true
    }

}