package org.delcom.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.delcom.data.*
import org.delcom.entities.RefreshToken
import org.delcom.helpers.JWTConstants
import org.delcom.helpers.ValidatorHelper
import org.delcom.helpers.hashPassword
import org.delcom.helpers.verifyPassword
import org.delcom.repositories.IRefreshTokenRepository
import org.delcom.repositories.IUserRepository
import java.util.*

class AuthService(
    private val jwtSecret: String,
    private val userRepository: IUserRepository,
    private val refreshTokenRepository: IRefreshTokenRepository
) {

    suspend fun postRegister(call: ApplicationCall) {

        val request = call.receive<AuthRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("username", "Username tidak boleh kosong")
        validator.required("password", "Password tidak boleh kosong")
        validator.validate()

        val existUser = userRepository.getUserByUsername(request.username)

        if (existUser != null) {
            throw AppException(409, "Akun dengan username ini sudah terdaftar!")
        }

        request.password = hashPassword(request.password)

        val user = userRepository.createUser(request.toEntity())

        val response = DataResponse(
            success = true,
            data = mapOf(
                "message" to "Berhasil melakukan pendaftaran",
                "userId" to user.id
            )
        )

        call.respond(response)
    }

    suspend fun postLogin(call: ApplicationCall) {

        val request = call.receive<AuthRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("username", "Username tidak boleh kosong")
        validator.required("password", "Password tidak boleh kosong")
        validator.validate()

        val existUser = userRepository.getUserByUsername(request.username)
            ?: throw AppException(404, "Kredensial yang digunakan tidak valid!")

        val validPassword = verifyPassword(request.password, existUser.password)

        if (!validPassword) {
            throw AppException(404, "Kredensial yang digunakan tidak valid!")
        }

        val authToken = JWT.create()
            .withAudience(JWTConstants.AUDIENCE)
            .withIssuer(JWTConstants.ISSUER)
            .withClaim("userId", existUser.id)
            .withExpiresAt(Date(System.currentTimeMillis() + 60 * 60 * 1000))
            .sign(Algorithm.HMAC256(jwtSecret))

        val refreshToken = UUID.randomUUID().toString()

        refreshTokenRepository.createToken(
            RefreshToken(
                userId = existUser.id,
                refreshToken = refreshToken,
                authToken = authToken
            )
        )

        val response = DataResponse(
            success = true,
            data = mapOf(
                "message" to "Berhasil melakukan login",
                "authToken" to authToken,
                "refreshToken" to refreshToken
            )
        )

        call.respond(response)
    }

    suspend fun postRefreshToken(call: ApplicationCall) {

        val request = call.receive<RefreshTokenRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("refreshToken", "Refresh Token tidak boleh kosong")
        validator.required("authToken", "Auth Token tidak boleh kosong")
        validator.validate()

        val existToken = refreshTokenRepository.getToken(request.refreshToken)

        refreshTokenRepository.deleteToken(request.refreshToken)

        if (existToken == null) {
            throw AppException(401, "Token tidak valid!")
        }

        val user = userRepository.getUserById(existToken.userId)
            ?: throw AppException(404, "User tidak ditemukan")

        val authToken = JWT.create()
            .withAudience(JWTConstants.AUDIENCE)
            .withIssuer(JWTConstants.ISSUER)
            .withClaim("userId", user.id)
            .withExpiresAt(Date(System.currentTimeMillis() + 60 * 60 * 1000))
            .sign(Algorithm.HMAC256(jwtSecret))

        val refreshToken = UUID.randomUUID().toString()

        refreshTokenRepository.createToken(
            RefreshToken(
                userId = user.id,
                refreshToken = refreshToken,
                authToken = authToken
            )
        )

        val response = DataResponse(
            success = true,
            data = mapOf(
                "message" to "Berhasil melakukan refresh token",
                "authToken" to authToken,
                "refreshToken" to refreshToken
            )
        )

        call.respond(response)
    }

    suspend fun postLogout(call: ApplicationCall) {

        val request = call.receive<RefreshTokenRequest>()

        val validator = ValidatorHelper(request.toMap())
        validator.required("authToken", "Auth Token tidak boleh kosong")
        validator.validate()

        val decodedJWT = JWT.require(
            Algorithm.HMAC256(jwtSecret)
        ).build().verify(request.authToken)

        val userId = decodedJWT
            .getClaim("userId")
            .asString()
            ?: throw AppException(401, "Token tidak valid")

        refreshTokenRepository.deleteToken(request.refreshToken)

        val response = DataResponse(
            success = true,
            data = mapOf(
                "message" to "Berhasil logout"
            )
        )

        call.respond(response)
    }
}