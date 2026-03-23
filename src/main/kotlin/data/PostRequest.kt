package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.delcom.entities.Post

@Serializable
data class PostRequest(
    var title: String = "",
    var imageUrl: String = "",
    var description: String = ""
) {

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "imageUrl" to imageUrl,
            "description" to description
        )
    }

    fun toEntity(userId: String): Post {
        return Post(
            userId = userId,
            title = title,
            imageUrl = imageUrl,
            description = description,
            createdAt = Clock.System.now(),
            updatedAt = Clock.System.now()
        )
    }
}