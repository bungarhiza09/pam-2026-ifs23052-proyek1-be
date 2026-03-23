package org.delcom.services

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.delcom.data.*
import org.delcom.helpers.*
import org.delcom.repositories.IUserRepository
import java.io.File
import java.util.*

class UserService(
    private val userRepo: IUserRepository
) {

    // ===== GET ME =====
    suspend fun getMe(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)

        call.respond(
            DataResponse(
                success = true,
                data = mapOf("user" to user)
            )
        )
    }

    // ===== UPDATE PROFILE =====
    suspend fun putMe(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)

        val request = call.receive<AuthRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("username", "Username tidak boleh kosong")
        validator.validate()

        val existUser = userRepo.getUserByUsername(request.username)
        if (existUser != null && existUser.id != user.id) {
            throw AppException(409, "Username sudah digunakan")
        }

        val isUpdated = userRepo.updateUserProfile(
            user.id,
            request.bio,
            request.photo
        )

        if (!isUpdated) {
            throw AppException(400, "Gagal update profile")
        }

        call.respond(
            DataResponse(
                success = true,
                data = mapOf("message" to "Profile berhasil diupdate")
            )
        )
    }

    // ===== UPDATE PHOTO =====
    suspend fun putMyPhoto(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)

        var newPhoto: String? = null

        val multipart = call.receiveMultipart()
        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {

                val ext = part.originalFileName
                    ?.substringAfterLast('.', "")
                    ?.let { if (it.isNotEmpty()) ".$it" else "" }
                    ?: ""

                val fileName = UUID.randomUUID().toString() + ext
                val filePath = "uploads/users/$fileName"

                withContext(Dispatchers.IO) {
                    val file = File(filePath)
                    file.parentFile.mkdirs()

                    part.provider().copyAndClose(file.writeChannel())
                    newPhoto = filePath
                }
            }
            part.dispose()
        }

        if (newPhoto == null) {
            throw AppException(400, "Photo tidak ditemukan")
        }

        val oldPhoto = user.photo

        val isUpdated = userRepo.updateUserProfile(
            user.id,
            null,
            newPhoto
        )

        if (!isUpdated) {
            throw AppException(400, "Gagal update photo")
        }

        oldPhoto?.let {
            val file = File(it)
            if (file.exists()) file.delete()
        }

        call.respond(
            DataResponse(
                success = true,
                data = mapOf("message" to "Foto berhasil diupdate")
            )
        )
    }

    suspend fun getMyPosts(call: ApplicationCall) {
        call.respond(
            DataResponse(
                success = true,
                data = mapOf("posts" to emptyList<Any>())
            )
        )
    }

    // ===== UPDATE PASSWORD =====
    suspend fun putMyPassword(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)

        val request = call.receive<AuthRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("password", "Password lama wajib diisi")
        validator.required("newPassword", "Password baru wajib diisi")
        validator.validate()

        val isUpdated = userRepo.updatePassword(
            user.id,
            request.password,
            request.newPassword
        )

        if (!isUpdated) {
            throw AppException(400, "Password lama salah")
        }

        call.respond(
            DataResponse(
                success = true,
                data = mapOf("message" to "Password berhasil diupdate")
            )
        )
    }

    // ===== GET PHOTO =====
    suspend fun getPhoto(call: ApplicationCall) {
        val id = call.parameters["id"]
            ?: throw AppException(400, "ID tidak valid")

        val user = userRepo.getUserById(id)
            ?: throw AppException(404, "User tidak ditemukan")

        val photo = user.photo
            ?: throw AppException(404, "User belum punya foto")

        val file = File(photo)
        if (!file.exists()) {
            throw AppException(404, "File tidak ditemukan")
        }

        call.respondFile(file)
    }
}