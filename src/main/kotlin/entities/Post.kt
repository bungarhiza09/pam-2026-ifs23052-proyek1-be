package org.delcom.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Post(
    var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var title: String,
    var description: String,
    var imageUrl: String? = null,
    var createdAt: String = Clock.System.now().toString(),
    var updatedAt: String = Clock.System.now().toString()
)