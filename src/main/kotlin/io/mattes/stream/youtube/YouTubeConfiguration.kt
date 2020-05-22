package io.mattes.stream.youtube

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest
import com.google.api.client.auth.oauth2.ClientParametersAuthentication
import com.google.api.client.auth.oauth2.RefreshTokenRequest
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeRequest
import com.google.api.services.youtube.YouTubeRequestInitializer
import com.google.api.services.youtube.YouTubeScopes
import org.apache.logging.log4j.util.Strings
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.websocket.server.PathParam

private const val TOKEN_URL = "https://oauth2.googleapis.com/token"
private const val AUTH_URL = "https://accounts.google.com/o/oauth2/auth"

@Configuration
@ConditionalOnProperty("app.youtube.enabled", havingValue = "true")
class YouTubeConfiguration {

    companion object {
        private val LOG = LoggerFactory.getLogger(YouTubeConfiguration::class.java)

        private val JSON_FACTORY = JacksonFactory()
        private val HTTP_TRANSPORT = NetHttpTransport()
    }

    @Value("\${app.youtube.refresh_token:#{null}")
    private var refreshToken: String? = null
    @Value("\${app.youtube.client_id}")
    private var clientId: String? = null
    @Value("\${app.youtube.client_secret}")
    private var clientSecret: String? = null

    @Volatile private var oauthToken: String? = null

    private val refreshExecutor = Executors.newSingleThreadScheduledExecutor()

    @Bean
    fun youtube(): YouTube {

        if (Strings.isBlank(refreshToken)) {
            LOG.warn("no refresh token provided. Get an auth code /setup and call /setup/{authCode} with it.")
        } else {
            updateToken(refreshToken!!)
        }

        return YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), null)
                .setYouTubeRequestInitializer(object : YouTubeRequestInitializer() {
                    override fun initializeYouTubeRequest(request: YouTubeRequest<*>) {
                        if (oauthToken == null) {
                            throw IllegalStateException("not yet initialized!")
                        }
                        super.initializeYouTubeRequest(request)
                        request.oauthToken = oauthToken
                    }
                })
                .setApplicationName("stream.mattes.io")
                .build()
    }

    fun initToken(authCode: String): TokenResponse {

        val tokenResponse = AuthorizationCodeTokenRequest(HTTP_TRANSPORT, JSON_FACTORY, GenericUrl(TOKEN_URL), authCode)
                .setRedirectUri("urn:ietf:wg:oauth:2.0:oob")
                .setClientAuthentication(ClientParametersAuthentication(clientId, clientSecret))
                .execute()

        oauthToken = tokenResponse.accessToken
        LOG.info("got token - expires in ${tokenResponse.expiresInSeconds} seconds")
        LOG.debug("token: $oauthToken")
        scheduleTokenUpdate(tokenResponse.refreshToken, tokenResponse.expiresInSeconds)
        return tokenResponse
    }

    private fun scheduleTokenUpdate(refreshToken: String, delayInSeconds: Long) {
        refreshExecutor.schedule({ updateToken(refreshToken) }, delayInSeconds - 30, TimeUnit.SECONDS)
    }

    private fun updateToken(refreshToken: String) {
        val tokenResponse = getNewToken(refreshToken)
        oauthToken = tokenResponse.accessToken
        scheduleTokenUpdate(refreshToken, tokenResponse.expiresInSeconds)
    }

    private fun getNewToken(refreshToken: String): TokenResponse {
        val tokenResponse = RefreshTokenRequest(HTTP_TRANSPORT, JSON_FACTORY, GenericUrl(TOKEN_URL), refreshToken)
                .setScopes(listOf(YouTubeScopes.YOUTUBE_READONLY))
                .setClientAuthentication(ClientParametersAuthentication(clientId, clientSecret))
                .execute()

        LOG.info("got new token - expires in ${tokenResponse.expiresInSeconds} seconds")
        LOG.debug("new token: ${tokenResponse.accessToken}")

        return tokenResponse
    }
}

@RestController
@ConditionalOnBean(YouTubeConfiguration::class)
@ConditionalOnProperty("app.youtube.refresh_token", matchIfMissing = true, havingValue = "null")
class YoutubeSetupController(
        private val youTubeConfiguration: YouTubeConfiguration
) {

    @Value("\${app.youtube.client_id}")
    private lateinit var clientId: String

    @GetMapping("/setup")
    fun initializeYoutubeAuthentication(@RequestParam("auth_code", required = false) authCode: String?,
                                        response: HttpServletResponse): Map<String, *> {

        if (!Strings.isBlank(authCode)) {
            val tokenResponse = youTubeConfiguration.initToken(authCode!!)
            return mapOf("refreshToken" to tokenResponse.refreshToken)
        }

        val url = AuthorizationCodeRequestUrl(AUTH_URL, clientId)
                .setRedirectUri("urn:ietf:wg:oauth:2.0:oob")
                .setScopes(listOf(YouTubeScopes.YOUTUBE_READONLY))
                .set("access_type", "offline")
                .build()

        response.sendRedirect(url)
        return emptyMap<String, String>()
    }

}