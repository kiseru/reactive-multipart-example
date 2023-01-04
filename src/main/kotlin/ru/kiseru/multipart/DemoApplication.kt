package ru.kiseru.multipart

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import java.nio.file.Paths

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}

@RestController
@RequestMapping("/file")
class FileController {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun upload(@RequestPart file: FilePart) {
        val path = Paths.get(file.filename())
        file.transferTo(path)
            .awaitSingleOrNull()
    }

    @GetMapping
    fun download(@RequestParam filename: String): ResponseEntity<out Resource> {
        val path = Paths.get(filename)
        val resource = FileSystemResource(path)
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${filename}\"")
            .body(resource)
    }
}
