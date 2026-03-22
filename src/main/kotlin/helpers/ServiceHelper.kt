package org.olahraga.helpers

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.olahraga.data.AppException
import org.olahraga.entities.User
import org.olahraga.repositories.IUserRepository

object ServiceHelper {
    suspend fun getAuthUser(call: ApplicationCall, userRepository: IUserRepository): User {
        val principal = call.principal<JWTPrincipal>()
            ?: throw AppException(401, "Unauthorized")

        val userId = principal
            .payload
            .getClaim("userId")
            .asString()
            ?: throw AppException(401, "Token tidak valid")

        val user = userRepository.getById(userId)
            ?: throw AppException(401, "User tidak valid")

        return user
    }
}
