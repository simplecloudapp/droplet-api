package app.simplecloud.droplet.api.controller

import app.simplecloud.droplet.api.droplet.Droplet
import build.buf.gen.simplecloud.controller.v1.ControllerDropletServiceGrpcKt
import build.buf.gen.simplecloud.controller.v1.HealthHandshakeRequest
import build.buf.gen.simplecloud.controller.v1.RegisterDropletRequest
import io.grpc.ConnectivityState
import io.grpc.ManagedChannel
import kotlinx.coroutines.*

class Connector(
    private val droplet: Droplet,
    private val channel: ManagedChannel,
    private val stub: ControllerDropletServiceGrpcKt.ControllerDropletServiceCoroutineStub,
) {
    private suspend fun connect(): Boolean {
        try {
            stub.registerDroplet(
                RegisterDropletRequest.newBuilder().setDefinition(
                    droplet.toDefinition()
                ).build()
            )
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private suspend fun handshake(): Boolean {
        try {
            stub.healthHandshake(HealthHandshakeRequest.newBuilder().setUniqueId(droplet.uniqueId).build())
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun enforceConnect(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            var attached = connect()
            while (isActive) {
                if (attached) {
                    if (!channel.getState(true).equals(ConnectivityState.READY)) {
                        attached = false
                        continue
                    }
                    if (!handshake()) {
                        attached = false
                        continue
                    }

                } else {
                    attached = connect()
                }
                delay(5000L)
            }
        }
    }

}