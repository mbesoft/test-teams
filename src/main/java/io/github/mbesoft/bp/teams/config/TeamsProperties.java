package io.github.mbesoft.bp.teams.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;

/**
 * Maps the bp.teams YAML properties to a strongly typed configuration object.
 * Secrets must be provided at runtime for qualification and production.
 */
@Validated
@ConfigurationProperties(prefix = "bp.teams")
public record TeamsProperties(
        boolean enabled,
        @NotNull URI tokenUrl,
        @NotNull URI sendUrl,
        @NotBlank String clientId,
        @NotBlank String clientSecret,
        @NotBlank String targetRole,
        @NotBlank String identityHeaderName,
        @NotBlank String targetRoleHeaderName,
        @NotBlank String apimHeaderName,
        @NotBlank String apimValue,
        @DefaultValue("5s") Duration connectTimeout,
        @DefaultValue("15s") Duration readTimeout
) {
    public TeamsProperties {
        // Reject unencrypted destinations before Spring starts the client.
        if (tokenUrl != null && (!"https".equalsIgnoreCase(tokenUrl.getScheme())
                || tokenUrl.getHost() == null)) {
            throw new IllegalArgumentException("The token URL must be an absolute HTTPS URL");
        }
        if (sendUrl != null && (!"https".equalsIgnoreCase(sendUrl.getScheme())
                || sendUrl.getHost() == null)) {
            throw new IllegalArgumentException("The send URL must be an absolute HTTPS URL");
        }
        if (connectTimeout != null && (connectTimeout.isZero() || connectTimeout.isNegative())) {
            throw new IllegalArgumentException("The connection timeout must be positive");
        }
        if (readTimeout != null && (readTimeout.isZero() || readTimeout.isNegative())) {
            throw new IllegalArgumentException("The read timeout must be positive");
        }
        // Header names are configurable, but must not contain arbitrary content.
        for (String header : new String[]{identityHeaderName, targetRoleHeaderName, apimHeaderName}) {
            if (header != null && !header.matches("[A-Za-z0-9-]+")) {
                throw new IllegalArgumentException("An invalid HTTP header name was configured");
            }
        }
    }
}
