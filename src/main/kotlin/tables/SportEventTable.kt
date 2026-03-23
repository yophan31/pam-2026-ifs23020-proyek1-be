package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object SportEventTable : UUIDTable("sport_events") {
    val userId         = uuid("user_id")
    val title          = varchar("title", 150)
    val description    = text("description")
    val cover          = text("cover").nullable()

    // Kolom khusus event olahraga
    val kategori       = varchar("kategori", 100)
    val tanggalEvent   = varchar("tanggal_event", 100)
    val lokasi         = varchar("lokasi", 255)
    val status         = varchar("status", 50).default("akan datang")
    val penyelenggara  = varchar("penyelenggara", 150)

    val createdAt      = timestamp("created_at")
    val updatedAt      = timestamp("updated_at")
}
