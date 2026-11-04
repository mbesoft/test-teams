package io.github.mbesoft.bp.teams.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.mbesoft.bp.teams.config.TeamsProperties;
import io.github.mbesoft.bp.teams.model.AdaptiveCard;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Java 11 / Spring Boot 2.7-compatible gateway client.
 *
 * Step 1: Request an OAuth2 token with the client_credentials grant.
 * Step 2: Send an Adaptive Card with the bearer token and configured headers.
 *
 * This class never sends anything unless enabled is explicitly true.
 */
@Component
public class TeamsGatewayClient {

    private final RestTemplate http;
    private final TeamsProperties properties;

    public TeamsGatewayClient(TeamsProperties properties) {
        this.properties = properties;

        // Leave normal TLS certificate verification enabled.
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Math.toIntExact(properties.getConnectTimeout().toMillis()));
        factory.setReadTimeout(Math.toIntExact(properties.getReadTimeout().toMillis()));
        this.http = new RestTemplate(factory);
    }

    /**
     * The local configuration disables outbound calls by default.
     * The check occurs before requesting any access token.
     */
    public void send(AdaptiveCard card) {
        if (!properties.isEnabled()) {
            throw new TeamsGatewayException("Teams sending is disabled for the active profile");
        }

        String token = fetchAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set(properties.getTargetRoleHeaderName(), properties.getTargetRole());
        headers.set(properties.getIdentityHeaderName(), properties.getClientId());
        headers.set(properties.getApimHeaderName(), properties.getApimValue());
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            // RestTemplate serializes the JavaBean payload using Jackson.
            http.exchange(properties.getSendUrl(), HttpMethod.POST,
                    new HttpEntity<AdaptiveCard>(card, headers), Void.class);
        } catch (RestClientException exception) {
            // Do not disclose credentials, access tokens or server response bodies.
            throw new TeamsGatewayException("The Teams gateway rejected the notification");
        }
    }

    private String fetchAccessToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(properties.getClientId(), properties.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            TokenResponse response = http.exchange(
                    properties.getTokenUrl(), HttpMethod.POST,
                    new HttpEntity<MultiValueMap<String, String>>(form, headers),
                    TokenResponse.class).getBody();

            if (response == null || response.getAccessToken() == null
                    || response.getAccessToken().isBlank()) {
                throw new TeamsGatewayException("The OAuth2 response did not contain an access token");
            }
            return response.getAccessToken();
        } catch (RestClientException exception) {
            throw new TeamsGatewayException("The OAuth2 token request failed");
        }
    }

    /**
     * Jackson DTO for the OAuth2 response.
     * Unknown fields (such as expires_in) are safely ignored.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class TokenResponse {

        @JsonProperty("access_token")
        private String accessToken;

        public TokenResponse() {
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }
}
