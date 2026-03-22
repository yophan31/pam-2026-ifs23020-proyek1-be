package org.olahraga.repositories

import org.olahraga.entities.SportEvent

interface ISportEventRepository {
    suspend fun getAll(
        userId: String,
        search: String,
        page: Int,
        perPage: Int,
        status: String?,
        kategori: String?
    ): List<SportEvent>

    suspend fun getHomeStats(userId: String): Map<String, Long>
    suspend fun getById(eventId: String): SportEvent?
    suspend fun create(event: SportEvent): String
    suspend fun update(userId: String, eventId: String, newEvent: SportEvent): Boolean
    suspend fun delete(userId: String, eventId: String): Boolean
}
