package org.delcom.services

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.*
import org.delcom.entities.Comment
import org.delcom.helpers.ServiceHelper
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.ICommentRepository
import org.delcom.repositories.IUserRepository

class CommentService(
    private val commentRepository: ICommentRepository,
    private val userRepository: IUserRepository
) {

    // ===== CREATE =====
    suspend fun post(call: ApplicationCall) {

        val user = ServiceHelper.getAuthUser(call, userRepository)

        val request = call.receive<Map<String, String>>()

        val validator = ValidatorHelper(request)
        validator.required("postId", "Post ID tidak boleh kosong")
        validator.required("content", "Komentar tidak boleh kosong")
        validator.validate()

        val comment = Comment(
            userId = user.id,
            postId = request["postId"]!!,
            content = request["content"]!!
        )

        val result = commentRepository.createComment(comment)

        call.respond(
            DataResponse(
                success = true,
                data = mapOf("comment" to result)
            )
        )
    }

    // ===== GET BY POST =====
    suspend fun getByPost(call: ApplicationCall) {

        val postId = call.parameters["postId"]
            ?: throw AppException(400, "Post ID tidak valid")

        val comments = commentRepository.getCommentsByPost(postId)

        call.respond(
            DataResponse(
                success = true,
                data = mapOf("comments" to comments)
            )
        )
    }

    // ===== UPDATE =====
    suspend fun put(call: ApplicationCall) {

        val user = ServiceHelper.getAuthUser(call, userRepository)

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val request = call.receive<Map<String, String>>()

        val validator = ValidatorHelper(request)
        validator.required("content", "Komentar tidak boleh kosong")
        validator.validate()

        // ⚠️ idealnya cek owner di repo
        val isUpdated = commentRepository.updateComment(
            id,
            request["content"]!!
        )

        if (!isUpdated) {
            throw AppException(400, "Gagal update komentar")
        }

        call.respond(
            DataResponse(
                success = true,
                data = mapOf("message" to "Komentar berhasil diupdate")
            )
        )
    }

    // ===== DELETE =====
    suspend fun delete(call: ApplicationCall) {

        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val isDeleted = commentRepository.deleteComment(id)

        if (!isDeleted) {
            throw AppException(400, "Gagal hapus komentar")
        }

        call.respond(
            DataResponse(
                success = true,
                data = mapOf("message" to "Komentar berhasil dihapus")
            )
        )
    }
}