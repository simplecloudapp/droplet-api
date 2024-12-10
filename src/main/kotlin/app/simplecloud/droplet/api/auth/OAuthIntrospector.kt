package app.simplecloud.droplet.api.auth

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*

class OAuthIntrospector(private val issuer: String) {
    private val gson = Gson()

    /**
     * @return list of all scopes issued to this token, or null if the token does not exist
     */
    suspend fun introspect(token: String): List<String>? {
        try {
            val response = HttpClient(CIO).use { client ->
                client.submitForm(
                    url = "$issuer/oauth/introspect",
                    formParameters = parameters {
                        append("token", token)
                    }
                )
            }
            val body = gson.fromJson(response.bodyAsText(), JsonObject::class.java)
            return if (!response.status.isSuccess() || !body["active"].asBoolean) {
                null
            } else {
                Scope.fromString(body["scope"].asString)
            }
        } catch (e: Exception) {
            return null
        }
    }
}