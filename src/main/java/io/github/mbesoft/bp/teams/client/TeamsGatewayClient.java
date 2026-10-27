package io.github.mbesoft.bp.teams.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.mbesoft.bp.teams.config.TeamsProperties;
import io.github.mbesoft.bp.teams.model.AdaptiveCard;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Implements the existing two-step gateway protocol:
 * 1) OAuth2 client_credentials token request with HTTP Basic authentication.
 * 2) Authenticated POST of an Adaptive Card to the channel gateway.
 *
 * The gateway resolves the destination channel from the configured role.
 * This class does not call Microsoft Graph directly.
 */
@Component
public class TeamsGatewayClient {

    private final RestClient http;
    private final TeamsProperties properties;

    public TeamsGatewayClient(RestClient.Builder builder, TeamsProperties properties) {
        this.properties = properties;

        // Keep TLS certificate validation enabled (the HTTP client default).
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.connectTimeout());
        requestFactory.setReadTimeout(properties.readTimeout());
        this.http = builder.requestFactory(requestFactory).build();
    }

    /**
     * Sending is explicitly disabled in the local profile. The guard runs
     * before any attempt to fetch a token or open a network connection.
     */
    public void send(AdaptiveCard card) {
        if (!properties.enabled()) {
            throw new TeamsGatewayException("Teams sending is disabled for the active profile");
        }

        String accessToken = fetchAccessToken();
        try {
            http.post()
                    .uri(properties.sendUrl())
                    .headers(headers -> {
                        headers.setBearerAuth(accessToken);
                        headers.set(properties.targetRoleHeaderName(), properties.targetRole());
                        headers.set(properties.identityHeaderName(), properties.clientId());
                        headers.set(properties.apimHeaderName(), properties.apimValue());
                    })
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(card)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            // Never include response bodies, credentials or request headers in errors.
            throw new TeamsGatewayException("The Teams gateway rejected the notification");
        }
    }

    private String fetchAccessToken() {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "client_credentials");

        try {
            TokenResponse token = http.post()
                    .uri(properties.tokenUrl())
                    .headers(headers -> headers.setBasicAuth(
                            properties.clientId(), properties.clientSecret()))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TokenResponse.class);

            if (token == null || token.accessToken() == null
                    || token.accessToken().isBlank()) {
                throw new TeamsGatewayException("The OAuth2 response did not contain an access token");
            }
            return token.accessToken();
        } catch (RestClientException exception) {
            throw new TeamsGatewayException("The OAuth2 token request failed");
        }
    }

    /** Maps the OAuth2 access_token JSON property to a Java record. */
    private record TokenResponse(@JsonProperty("access_token") String accessToken) {}
}
