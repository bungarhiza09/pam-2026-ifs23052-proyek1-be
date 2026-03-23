package org.delcom.data

import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(
    val id: Int,
    val postId: Int,
    val username: String,
    val content: String,
    val createdAt: String
)