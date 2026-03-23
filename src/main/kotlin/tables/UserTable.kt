package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime

object UserTable : UUIDTable("users") {

    val name = varchar("name", 100)

    val username = varchar("username", 50).uniqueIndex()

    val password = varchar("password", 255)

    val photo = text("photo").nullable()

    val bio = text("bio").nullable()

    val createdAt = datetime("created_at")

    val updatedAt = datetime("updated_at").nullable()
}