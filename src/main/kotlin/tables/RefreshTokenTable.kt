package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime

object RefreshTokenTable : UUIDTable("refresh_tokens") {

    val userId = reference(
        "user_id",
        UserTable,
        onDelete = ReferenceOption.CASCADE
    )

    val refreshToken = text("refresh_token")

    val authToken = text("auth_token")

    val createdAt = datetime("created_at")
}