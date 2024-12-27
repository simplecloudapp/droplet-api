package app.simplecloud.droplet.api.controller

import app.simplecloud.droplet.api.droplet.Droplet
import build.buf.gen.simplecloud.controller.v1.ControllerDropletServiceGrpcKt
import build.buf.gen.simplecloud.controller.v1.RegisterDropletRequest
import io.grpc.ConnectivityState
import io.grpc.ManagedChannel
import kotlinx.coroutines.*

class Attacher(
    private val droplet: Droplet,
    private val channel: ManagedChannel,
    private val stub: ControllerDropletServiceGrpcKt.ControllerDropletServiceCoroutineStub,
) {
    private suspend fun attach(): Boolean {
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

    fun enforceAttach(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            var attached = attach()
            while (isActive) {
                if (attached) {
                    if (!channel.getState(true).equals(ConnectivityState.READY)) {
                        attached = false
                    }
                } else {
                    attached = attach()
                }
                delay(5000L)
            }
        }
    }

}