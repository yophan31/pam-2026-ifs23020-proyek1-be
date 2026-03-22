package org.olahraga.helpers

import kotlinx.coroutines.Dispatchers
import org.olahraga.dao.RefreshTokenDAO
import org.olahraga.dao.SportEventDAO
import org.olahraga.dao.UserDAO
import org.olahraga.entities.RefreshToken
import org.olahraga.entities.SportEvent
import org.olahraga.entities.User
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun userDAOToModel(dao: UserDAO) = User(
    id        = dao.id.value.toString(),
    name      = dao.name,
    username  = dao.username,
    password  = dao.password,
    photo     = dao.photo,
    about     = dao.about,
    createdAt = dao.createdAt,
    updatedAt = dao.updatedAt
)

fun refreshTokenDAOToModel(dao: RefreshTokenDAO) = RefreshToken(
    id           = dao.id.value.toString(),
    userId       = dao.userId.toString(),
    refreshToken = dao.refreshToken,
    authToken    = dao.authToken,
    createdAt    = dao.createdAt,
)

fun sportEventDAOToModel(dao: SportEventDAO) = SportEvent(
    id            = dao.id.value.toString(),
    userId        = dao.userId.toString(),
    title         = dao.title,
    description   = dao.description,
    cover         = dao.cover,
    kategori      = dao.kategori,
    tanggalEvent  = dao.tanggalEvent,
    lokasi        = dao.lokasi,
    status        = dao.status,
    penyelenggara = dao.penyelenggara,
    createdAt     = dao.createdAt,
    updatedAt     = dao.updatedAt
)
