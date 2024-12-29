package app.simplecloud.droplet.api.droplet

import build.buf.gen.simplecloud.controller.v1.DropletDefinition
import build.buf.gen.simplecloud.controller.v1.dropletDefinition

data class Droplet(
    val type: String,
    val id: String,
    val host: String,
    val port: Int,
    val envoyPort: Int
) {
    companion object {
        fun fromDefinition(definition: DropletDefinition): Droplet {
            return Droplet(
                definition.type,
                definition.id,
                definition.host,
                definition.port,
                definition.envoyPort
            )
        }
    }

    fun toDefinition(): DropletDefinition {
        return dropletDefinition {
            type = this@Droplet.type
            id = this@Droplet.id
            host = this@Droplet.host
            port = this@Droplet.port
            envoyPort = this@Droplet.envoyPort
        };
    }
}
