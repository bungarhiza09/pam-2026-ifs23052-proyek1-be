package org.delcom.helpers

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.ZoneOffset
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.Clock

import org.delcom.dao.UserDAO
import org.delcom.dao.RefreshTokenDAO
import org.delcom.dao.PostDAO
import org.delcom.dao.LikeDAO
import org.delcom.dao.CommentDAO
import org.delcom.entities.User
import org.delcom.entities.RefreshToken
import org.delcom.entities.Post
import org.delcom.entities.Like
import org.delcom.entities.Comment


suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)



fun userDAOToModel(dao: UserDAO) = User(
    id = dao.id.value.toString(),
    name = dao.username, // atau dao.name kalau ada di DAO
    username = dao.username,
    password = dao.password,
    photo = dao.photo,
    bio = dao.bio,

    createdAt = dao.createdAt
        .toInstant(ZoneOffset.UTC)
        .toKotlinInstant(),

    updatedAt = dao.updatedAt
        ?.toInstant(ZoneOffset.UTC)
        ?.toKotlinInstant()
        ?: Clock.System.now()
)



fun refreshTokenDAOToModel(dao: RefreshTokenDAO) = RefreshToken(
    id = dao.id.value.toString(),
    userId = dao.userId.value.toString(),
    refreshToken = dao.refreshToken,
    authToken = dao.authToken,
    createdAt = dao.createdAt.toInstant(java.time.ZoneOffset.UTC).toKotlinInstant()
)



fun postDAOToModel(dao: PostDAO) = Post(
    id = dao.id.value.toString(),
    userId = dao.userId.value.toString(),
    title = dao.title,
    description = dao.description,
    imageUrl = dao.image,

    createdAt = dao.createdAt
        .toInstant(ZoneOffset.UTC)
        .toKotlinInstant(),

    updatedAt = dao.updatedAt
        ?.toInstant(ZoneOffset.UTC)
        ?.toKotlinInstant()
        ?: dao.createdAt
            .toInstant(ZoneOffset.UTC)
            .toKotlinInstant()
)



fun likeDAOToModel(dao: LikeDAO) = Like(
    id = dao.id.value.toString(),
    postId = dao.postId.value.toString(),
    userId = dao.userId.value.toString()
)

fun commentDAOToModel(dao: CommentDAO) = Comment(
    id = dao.id.value.toString(),
    postId = dao.postId.value.toString(),
    userId = dao.userId.value.toString(),
    content = dao.content,
    createdAt = dao.createdAt.toInstant(java.time.ZoneOffset.UTC).toKotlinInstant()
)