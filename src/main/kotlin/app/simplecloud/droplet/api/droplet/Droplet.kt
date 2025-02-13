package app.simplecloud.droplet.api.droplet

import app.simplecloud.droplet.api.time.ProtobufTimestamp
import build.buf.gen.simplecloud.controller.v1.DropletDefinition
import build.buf.gen.simplecloud.controller.v1.dropletDefinition
import java.time.LocalDateTime
import java.util.*

@Suppress("unused")
data class Droplet(
    val type: String,
    val id: String,
    val host: String,
    val port: Int,
    val uniqueId: String = DropletIdentifier.getOrCreate(),
    val envoyPort: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun fromDefinition(definition: DropletDefinition): Droplet {
            return Droplet(
                definition.type,
                definition.id,
                definition.host,
                definition.port,
                uniqueId = definition.uniqueId,
                createdAt = ProtobufTimestamp.toLocalDateTime(definition.createdAt)
            )
        }
    }

    fun toDefinition(): DropletDefinition {
        return dropletDefinition {
            type = this@Droplet.type
            id = this@Droplet.id
            host = this@Droplet.host
            port = this@Droplet.port
            uniqueId = this@Droplet.uniqueId
            createdAt = ProtobufTimestamp.fromLocalDateTime(this@Droplet.createdAt)
        }
    }
}
