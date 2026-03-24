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
    var kategori: String,

    @Contextual
    val createdAt: Instant = Clock.System.now(),

    @Contextual
    var updatedAt: Instant = Clock.System.now()
)