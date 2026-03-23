package org.delcom.repositories

import org.delcom.entities.User

interface IUserRepository {

    suspend fun createUser(user: User): User

    suspend fun getUserById(id: String): User?

    suspend fun getUserByUsername(username: String): User?

    suspend fun updateUserProfile(
        id: String,
        bio: String?,
        photo: String?
    ): Boolean
    suspend fun updatePassword(
        id: String,
        oldPassword: String,
        newPassword: String
    ): Boolean

}