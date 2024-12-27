package app.simplecloud.droplet.api.droplet

import app.simplecloud.droplet.api.auth.AuthCallCredentials
import build.buf.gen.simplecloud.controller.v1.ControllerDropletServiceGrpcKt
import build.buf.gen.simplecloud.controller.v1.getAllDropletsRequest
import build.buf.gen.simplecloud.controller.v1.getDropletRequest
import build.buf.gen.simplecloud.controller.v1.getDropletsByTypeRequest
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.kotlin.AbstractCoroutineStub

class DropletDiscoverer(
    private val stub: ControllerDropletServiceGrpcKt.ControllerDropletServiceCoroutineStub,
    private val callCredentials: AuthCallCredentials,
) {

    suspend fun <T : AbstractCoroutineStub<*>> discover(type: String, id: String, clazz: Class<T>): T {
        val droplet = stub.getDroplet(getDropletRequest {
            this.id = id
            this.type = type
        }).definition
        val channel = ManagedChannelBuilder.forAddress(droplet.host, droplet.port).usePlaintext().build()
        return clazz.getConstructor(ManagedChannel::class.java).newInstance(channel).withCallCredentials(callCredentials) as T
    }

    suspend fun <T : AbstractCoroutineStub<*>> discover(type: String, clazz: Class<T>): T {
        return discoverAllTyped(type, clazz).first()
    }

    suspend fun <T : AbstractCoroutineStub<*>> discoverAllTyped(type: String, clazz: Class<T>): List<T> {
        val droplets = stub.getDropletsByType(getDropletsByTypeRequest {
            this.type = type
        }).definitionList
        return droplets.map { droplet ->
            val channel = ManagedChannelBuilder.forAddress(droplet.host, droplet.port).usePlaintext().build()
            return@map clazz.getConstructor(ManagedChannel::class.java).newInstance(channel).withCallCredentials(callCredentials) as T
        }
    }

    suspend fun <T : AbstractCoroutineStub<*>> discoverAll(clazz: Class<T>): List<T> {
        val droplets = stub.getAllDroplets(getAllDropletsRequest { }).definitionList
        return droplets.map { droplet ->
            val channel = ManagedChannelBuilder.forAddress(droplet.host, droplet.port).usePlaintext().build()
            return@map clazz.getConstructor(ManagedChannel::class.java).newInstance(channel).withCallCredentials(callCredentials) as T
        }
    }


}