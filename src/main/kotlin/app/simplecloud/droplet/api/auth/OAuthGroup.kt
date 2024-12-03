package app.simplecloud.droplet.api.auth

data class OAuthGroup(
    val scopes: List<String> = emptyList(),
    val name: String,
)