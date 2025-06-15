package app.simplecloud.droplet.api.droplet

import java.nio.file.Paths
import java.util.UUID
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object DropletIdentifier {

    fun getOrCreate(): String {
        val path = Paths.get(".sc.id")
        if (!path.exists()) {
            val id = UUID.randomUUID().toString()
            path.writeText(id, Charsets.UTF_8)
            return id
        }

        return path.readText(Charsets.UTF_8)
    }

}