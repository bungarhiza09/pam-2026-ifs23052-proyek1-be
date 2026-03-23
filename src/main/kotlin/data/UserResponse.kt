package org.delcom.data

import org.delcom.entities.User
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(

    val id: String,
    val username: String,
    val bio: String?,
    val profileImage: String?

){
    companion object {

        fun fromEntity(user: User): UserResponse {

            return UserResponse(
                id = user.id,
                username = user.username,
                bio = user.bio,
                profileImage = user.photo
            )
        }

    }
}