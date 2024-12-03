package app.simplecloud.droplet.api.auth

data class OAuthClient(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String? = null,
    val grantTypes: String,
    val scope: List<String> = emptyList(),
)