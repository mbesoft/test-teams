package io.github.mbesoft.bp.teams.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.Duration;

/**
 * External configuration for the Teams HTTP client.
 *
 * JavaBean binding is used for compatibility with Spring Boot 2.7 and Java 11.
 * Never store production credentials or internal URLs in source control.
 */
@Validated
@ConfigurationProperties(prefix = "bp.teams")
public class TeamsProperties {

    private boolean enabled;

    @NotNull
    private URI tokenUrl;

    @NotNull
    private URI sendUrl;

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @NotBlank
    private String targetRole;

    @NotBlank
    private String identityHeaderName;

    @NotBlank
    private String targetRoleHeaderName;

    @NotBlank
    private String apimHeaderName;

    @NotBlank
    private String apimValue;

    private Duration connectTimeout = Duration.ofSeconds(5);
    private Duration readTimeout = Duration.ofSeconds(15);

    /** Required by Spring Boot's JavaBean property binder. */
    public TeamsProperties() {
    }

    /** Convenient constructor for isolated unit tests. */
    public TeamsProperties(boolean enabled, URI tokenUrl, URI sendUrl,
                           String clientId, String clientSecret, String targetRole,
                           String identityHeaderName, String targetRoleHeaderName,
                           String apimHeaderName, String apimValue,
                           Duration connectTimeout, Duration readTimeout) {
        setEnabled(enabled);
        setTokenUrl(tokenUrl);
        setSendUrl(sendUrl);
        setClientId(clientId);
        setClientSecret(clientSecret);
        setTargetRole(targetRole);
        setIdentityHeaderName(identityHeaderName);
        setTargetRoleHeaderName(targetRoleHeaderName);
        setApimHeaderName(apimHeaderName);
        setApimValue(apimValue);
        setConnectTimeout(connectTimeout);
        setReadTimeout(readTimeout);
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public URI getTokenUrl() { return tokenUrl; }
    public void setTokenUrl(URI tokenUrl) {
        requireHttps(tokenUrl, "token-url");
        this.tokenUrl = tokenUrl;
    }

    public URI getSendUrl() { return sendUrl; }
    public void setSendUrl(URI sendUrl) {
        requireHttps(sendUrl, "send-url");
        this.sendUrl = sendUrl;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getClientSecret() { return clientSecret; }
    public void setClientSecret(String clientSecret) { this.clientSecret = clientSecret; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getIdentityHeaderName() { return identityHeaderName; }
    public void setIdentityHeaderName(String name) {
        requireHeaderName(name);
        this.identityHeaderName = name;
    }

    public String getTargetRoleHeaderName() { return targetRoleHeaderName; }
    public void setTargetRoleHeaderName(String name) {
        requireHeaderName(name);
        this.targetRoleHeaderName = name;
    }

    public String getApimHeaderName() { return apimHeaderName; }
    public void setApimHeaderName(String name) {
        requireHeaderName(name);
        this.apimHeaderName = name;
    }

    public String getApimValue() { return apimValue; }
    public void setApimValue(String apimValue) { this.apimValue = apimValue; }

    public Duration getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(Duration timeout) {
        requirePositive(timeout, "connect-timeout");
        this.connectTimeout = timeout;
    }

    public Duration getReadTimeout() { return readTimeout; }
    public void setReadTimeout(Duration timeout) {
        requirePositive(timeout, "read-timeout");
        this.readTimeout = timeout;
    }

    private static void requireHttps(URI uri, String property) {
        if (uri != null && (!"https".equalsIgnoreCase(uri.getScheme())
                || uri.getHost() == null)) {
            throw new IllegalArgumentException(property + " must be an absolute HTTPS URL");
        }
    }

    private static void requireHeaderName(String name) {
        if (name != null && !name.matches("[A-Za-z0-9-]+")) {
            throw new IllegalArgumentException("An invalid HTTP header name was configured");
        }
    }

    private static void requirePositive(Duration timeout, String name) {
        if (timeout == null || timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException(name + " must be positive");
        }
    }
}
