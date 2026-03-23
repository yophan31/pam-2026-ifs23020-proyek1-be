package org.delcom.services

import io.ktor.http.*
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
import org.delcom.repositories.ISportEventRepository
import org.delcom.repositories.IUserRepository
import java.io.File
import java.util.UUID

class SportEventService(
    private val userRepo: IUserRepository,
    private val sportEventRepo: ISportEventRepository
) {
    suspend fun getAll(call: ApplicationCall) {
        val user = ServiceHelper.getAuthUser(call, userRepo)

        val search   = call.request.queryParameters["search"]  ?: ""
        val page     = call.request.queryParameters["page"]?.toIntOrNull()    ?: 1
        val perPage  = call.request.queryParameters["perPage"]?.toIntOrNull() ?: 10
        val status   = call.request.queryParameters["status"]
        val kategori = call.request.queryParameters["kategori"]

        val events = sportEventRepo.getAll(user.id, search, page, perPage, status, kategori)

        call.respond(DataResponse("success", "Berhasil mengambil daftar event olahraga",
            mapOf("sportEvents" to events)))
    }

    suspend fun getStats(call: ApplicationCall) {
        val user  = ServiceHelper.getAuthUser(call, userRepo)
        val stats = sportEventRepo.getHomeStats(user.id)

        call.respond(DataResponse("success", "Berhasil mengambil statistik event olahraga",
            mapOf("stats" to stats)))
    }

    suspend fun getById(call: ApplicationCall) {
        val eventId = call.parameters["id"]
            ?: throw AppException(400, "Data event tidak valid!")

        val user  = ServiceHelper.getAuthUser(call, userRepo)
        val event = sportEventRepo.getById(eventId)

        if (event == null || event.userId != user.id)
            throw AppException(404, "Data event olahraga tidak tersedia!")

        call.respond(DataResponse("success", "Berhasil mengambil data event olahraga",
            mapOf("sportEvent" to event)))
    }

    suspend fun post(call: ApplicationCall) {
        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<SportEventRequest>()
        request.userId = user.id

        val validator = ValidatorHelper(request.toMap())
        validator.required("title",         "Nama event tidak boleh kosong")
        validator.required("description",   "Deskripsi tidak boleh kosong")
        validator.required("kategori",      "Kategori olahraga tidak boleh kosong")
        validator.required("tanggalEvent",  "Tanggal event tidak boleh kosong")
        validator.required("lokasi",        "Lokasi event tidak boleh kosong")
        validator.required("status",        "Status tidak boleh kosong")
        validator.required("penyelenggara", "Penyelenggara tidak boleh kosong")
        validator.validate()

        val eventId = sportEventRepo.create(request.toEntity())

        call.respond(DataResponse("success", "Berhasil menambahkan event olahraga",
            mapOf("sportEventId" to eventId)))
    }

    suspend fun put(call: ApplicationCall) {
        val eventId = call.parameters["id"]
            ?: throw AppException(400, "Data event tidak valid!")

        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val request = call.receive<SportEventRequest>()
        request.userId = user.id

        val validator = ValidatorHelper(request.toMap())
        validator.required("title",         "Nama event tidak boleh kosong")
        validator.required("description",   "Deskripsi tidak boleh kosong")
        validator.required("kategori",      "Kategori olahraga tidak boleh kosong")
        validator.required("tanggalEvent",  "Tanggal event tidak boleh kosong")
        validator.required("lokasi",        "Lokasi event tidak boleh kosong")
        validator.required("status",        "Status tidak boleh kosong")
        validator.required("penyelenggara", "Penyelenggara tidak boleh kosong")
        validator.validate()

        val oldEvent = sportEventRepo.getById(eventId)
        if (oldEvent == null || oldEvent.userId != user.id)
            throw AppException(404, "Data event olahraga tidak tersedia!")

        request.cover = oldEvent.cover

        val isUpdated = sportEventRepo.update(user.id, eventId, request.toEntity())
        if (!isUpdated) throw AppException(400, "Gagal memperbarui data event olahraga!")

        call.respond(DataResponse<Nothing>("success", "Berhasil mengubah data event olahraga", null))
    }

    suspend fun putCover(call: ApplicationCall) {
        val eventId = call.parameters["id"]
            ?: throw AppException(400, "Data event tidak valid!")

        val user    = ServiceHelper.getAuthUser(call, userRepo)
        val request = SportEventRequest()
        request.userId = user.id

        val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val ext      = part.originalFileName?.substringAfterLast('.', "")
                        ?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/sport_events/$fileName"

                    withContext(Dispatchers.IO) {
                        val file = File(filePath)
                        file.parentFile.mkdirs()
                        part.provider().copyAndClose(file.writeChannel())
                        request.cover = filePath
                    }
                }
                else -> {}
            }
            part.dispose()
        }

        if (request.cover == null) throw AppException(404, "Cover event tidak tersedia!")
        if (!File(request.cover!!).exists()) throw AppException(404, "Cover event gagal diunggah!")

        val oldEvent = sportEventRepo.getById(eventId)
        if (oldEvent == null || oldEvent.userId != user.id)
            throw AppException(404, "Data event olahraga tidak tersedia!")

        // Pertahankan data lama
        request.title         = oldEvent.title
        request.description   = oldEvent.description
        request.kategori      = oldEvent.kategori
        request.tanggalEvent  = oldEvent.tanggalEvent
        request.lokasi        = oldEvent.lokasi
        request.status        = oldEvent.status
        request.penyelenggara = oldEvent.penyelenggara

        val isUpdated = sportEventRepo.update(user.id, eventId, request.toEntity())
        if (!isUpdated) throw AppException(400, "Gagal memperbarui cover event!")

        if (oldEvent.cover != null) {
            val oldFile = File(oldEvent.cover!!)
            if (oldFile.exists()) oldFile.delete()
        }

        call.respond(DataResponse<Nothing>("success", "Berhasil mengubah cover event olahraga", null))
    }

    suspend fun delete(call: ApplicationCall) {
        val eventId = call.parameters["id"]
            ?: throw AppException(400, "Data event tidak valid!")

        val user     = ServiceHelper.getAuthUser(call, userRepo)
        val oldEvent = sportEventRepo.getById(eventId)

        if (oldEvent == null || oldEvent.userId != user.id)
            throw AppException(404, "Data event olahraga tidak tersedia!")

        val isDeleted = sportEventRepo.delete(user.id, eventId)
        if (!isDeleted) throw AppException(400, "Gagal menghapus data event olahraga!")

        if (oldEvent.cover != null) {
            val oldFile = File(oldEvent.cover!!)
            if (oldFile.exists()) oldFile.delete()
        }

        call.respond(DataResponse<Nothing>("success", "Berhasil menghapus data event olahraga", null))
    }

    suspend fun getCover(call: ApplicationCall) {
        val eventId = call.parameters["id"]
            ?: throw AppException(400, "Data event tidak valid!")

        val event = sportEventRepo.getById(eventId)
            ?: return call.respond(HttpStatusCode.NotFound)

        if (event.cover == null) throw AppException(404, "Event belum memiliki cover")

        val file = File(event.cover!!)
        if (!file.exists()) throw AppException(404, "Cover event tidak tersedia")

        call.respondFile(file)
    }
}
