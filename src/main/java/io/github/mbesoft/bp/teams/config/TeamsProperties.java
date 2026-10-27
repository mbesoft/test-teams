package io.github.mbesoft.bp.teams.config;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import java.net.URI;
@Validated
@ConfigurationProperties(prefix = "bp.teams")
public record TeamsProperties(
 @NotNull URI tokenUrl, @NotNull URI sendUrl,
 @NotBlank String clientId, @NotBlank String clientSecret,
 @NotBlank String targetRole, @NotBlank String identityHeaderName,
 @NotBlank String apimValue) {
 public TeamsProperties {
  if (tokenUrl != null && !"https".equalsIgnoreCase(tokenUrl.getScheme())) throw new IllegalArgumentException("token-url exige HTTPS");
  if (sendUrl != null && !"https".equalsIgnoreCase(sendUrl.getScheme())) throw new IllegalArgumentException("send-url exige HTTPS");
 }
}
