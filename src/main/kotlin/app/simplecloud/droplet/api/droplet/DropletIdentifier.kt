package app.simplecloud.droplet.api.droplet

import java.nio.file.Paths
import java.util.UUID
import kotlin.io.path.readText
import kotlin.io.path.writeText

object DropletIdentifier {
    fun getOrCreate(): String {
        val path = Paths.get(".sc.id")
        var id = path.readText(Charsets.UTF_8)
        try {
            UUID.fromString(id)
        }catch (e: Exception) {
            id = UUID.randomUUID().toString()
            path.writeText(id, Charsets.UTF_8)
        }
        return id
    }
}