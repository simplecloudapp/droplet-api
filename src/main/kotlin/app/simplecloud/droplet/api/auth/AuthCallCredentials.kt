package app.simplecloud.droplet.api.auth

import io.grpc.CallCredentials
import io.grpc.Metadata
import java.util.concurrent.Executor

class AuthCallCredentials(
    private val secretKey: String
) : CallCredentials() {

    override fun applyRequestMetadata(
        requestInfo: RequestInfo,
        appExecutor: Executor,
        applier: MetadataApplier
    ) {
        appExecutor.execute {
            val headers = Metadata()
            headers.put(MetadataKeys.AUTH_SECRET_KEY, secretKey)
            applier.apply(headers)
        }
    }

}