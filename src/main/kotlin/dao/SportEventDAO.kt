package org.olahraga.dao

import org.olahraga.tables.SportEventTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import java.util.UUID

class SportEventDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, SportEventDAO>(SportEventTable)

    var userId        by SportEventTable.userId
    var title         by SportEventTable.title
    var description   by SportEventTable.description
    var cover         by SportEventTable.cover
    var kategori      by SportEventTable.kategori
    var tanggalEvent  by SportEventTable.tanggalEvent
    var lokasi        by SportEventTable.lokasi
    var status        by SportEventTable.status
    var penyelenggara by SportEventTable.penyelenggara
    var createdAt     by SportEventTable.createdAt
    var updatedAt     by SportEventTable.updatedAt
}
