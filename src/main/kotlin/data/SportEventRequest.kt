package org.delcom.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import org.delcom.entities.SportEvent

@Serializable
data class SportEventRequest(
    var userId: String = "",
    var title: String = "",
    var description: String = "",
    var cover: String? = null,

    // Field khusus event olahraga
    var kategori: String = "",
    var tanggalEvent: String = "",
    var lokasi: String = "",
    var status: String = "akan datang",
    var penyelenggara: String = ""
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "title" to title,
        "description" to description,
        "cover" to cover,
        "kategori" to kategori,
        "tanggalEvent" to tanggalEvent,
        "lokasi" to lokasi,
        "status" to status,
        "penyelenggara" to penyelenggara
    )

    fun toEntity(): SportEvent = SportEvent(
        userId = userId,
        title = title,
        description = description,
        cover = cover,
        kategori = kategori,
        tanggalEvent = tanggalEvent,
        lokasi = lokasi,
        status = status,
        penyelenggara = penyelenggara,
        updatedAt = Clock.System.now()
    )
}
