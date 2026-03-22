package org.olahraga.repositories

import org.olahraga.dao.SportEventDAO
import org.olahraga.entities.SportEvent
import org.olahraga.helpers.sportEventDAOToModel
import org.olahraga.helpers.suspendTransaction
import org.olahraga.tables.SportEventTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class SportEventRepository : ISportEventRepository {

    override suspend fun getAll(
        userId: String,
        search: String,
        page: Int,
        perPage: Int,
        status: String?,
        kategori: String?
    ): List<SportEvent> = suspendTransaction {
        val query = if (search.isBlank()) {
            SportEventDAO.find {
                var op: org.jetbrains.exposed.sql.Op<Boolean> =
                    (SportEventTable.userId eq UUID.fromString(userId))
                if (status != null)   op = op and (SportEventTable.status   eq status)
                if (kategori != null) op = op and (SportEventTable.kategori eq kategori)
                op
            }
        } else {
            val keyword = "%${search.lowercase()}%"
            SportEventDAO.find {
                var op: org.jetbrains.exposed.sql.Op<Boolean> =
                    (SportEventTable.userId eq UUID.fromString(userId)) and
                            (SportEventTable.title.lowerCase() like keyword)
                if (status != null)   op = op and (SportEventTable.status   eq status)
                if (kategori != null) op = op and (SportEventTable.kategori eq kategori)
                op
            }
        }

        query.orderBy(SportEventTable.createdAt to SortOrder.DESC)
            .limit(perPage)
            .offset(((page - 1) * perPage).toLong())
            .map(::sportEventDAOToModel)
    }

    override suspend fun getHomeStats(userId: String): Map<String, Long> = suspendTransaction {
        val uid = UUID.fromString(userId)
        val total       = SportEventDAO.find { SportEventTable.userId eq uid }.count()
        val akanDatang  = SportEventDAO.find { (SportEventTable.userId eq uid) and (SportEventTable.status eq "akan datang") }.count()
        val berlangsung = SportEventDAO.find { (SportEventTable.userId eq uid) and (SportEventTable.status eq "berlangsung") }.count()
        val selesai     = SportEventDAO.find { (SportEventTable.userId eq uid) and (SportEventTable.status eq "selesai") }.count()

        mapOf(
            "total"       to total,
            "akanDatang"  to akanDatang,
            "berlangsung" to berlangsung,
            "selesai"     to selesai
        )
    }

    override suspend fun getById(eventId: String): SportEvent? = suspendTransaction {
        SportEventDAO
            .find { SportEventTable.id eq UUID.fromString(eventId) }
            .limit(1)
            .map(::sportEventDAOToModel)
            .firstOrNull()
    }

    override suspend fun create(event: SportEvent): String = suspendTransaction {
        val dao = SportEventDAO.new {
            userId        = UUID.fromString(event.userId)
            title         = event.title
            description   = event.description
            cover         = event.cover
            kategori      = event.kategori
            tanggalEvent  = event.tanggalEvent
            lokasi        = event.lokasi
            status        = event.status
            penyelenggara = event.penyelenggara
            createdAt     = event.createdAt
            updatedAt     = event.updatedAt
        }
        dao.id.value.toString()
    }

    override suspend fun update(userId: String, eventId: String, newEvent: SportEvent): Boolean = suspendTransaction {
        val dao = SportEventDAO
            .find {
                (SportEventTable.id     eq UUID.fromString(eventId)) and
                        (SportEventTable.userId eq UUID.fromString(userId))
            }
            .limit(1)
            .firstOrNull()

        if (dao != null) {
            dao.title         = newEvent.title
            dao.description   = newEvent.description
            dao.cover         = newEvent.cover
            dao.kategori      = newEvent.kategori
            dao.tanggalEvent  = newEvent.tanggalEvent
            dao.lokasi        = newEvent.lokasi
            dao.status        = newEvent.status
            dao.penyelenggara = newEvent.penyelenggara
            dao.updatedAt     = newEvent.updatedAt
            true
        } else false
    }

    override suspend fun delete(userId: String, eventId: String): Boolean = suspendTransaction {
        val rows = SportEventTable.deleteWhere {
            (SportEventTable.id     eq UUID.fromString(eventId)) and
                    (SportEventTable.userId eq UUID.fromString(userId))
        }
        rows >= 1
    }
}
