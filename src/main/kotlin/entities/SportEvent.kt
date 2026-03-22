package org.olahraga.entities

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class SportEvent(
    var id: String = UUID.randomUUID().toString(),
    var userId: String,
    var title: String,
    var description: String,
    var cover: String?,

    // Field khusus event olahraga
    var kategori: String,          // Jenis olahraga: sepak bola, basket, voli, dll
    var tanggalEvent: String,      // Tanggal pelaksanaan event
    var lokasi: String,            // Tempat/stadion event
    var status: String = "akan datang", // akan datang / berlangsung / selesai
    var penyelenggara: String,     // Nama penyelenggara / organisasi

    @Contextual
    val createdAt: Instant = Clock.System.now(),
    @Contextual
    var updatedAt: Instant = Clock.System.now(),
)
