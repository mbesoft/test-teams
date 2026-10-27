package io.github.mbesoft.bp.teams;

import io.github.mbesoft.bp.teams.client.TeamsGatewayClient;
import io.github.mbesoft.bp.teams.client.TeamsGatewayException;
import io.github.mbesoft.bp.teams.config.TeamsProperties;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Ensures the local-profile safety switch prevents an HTTP request. */
class TeamsGatewayClientTest {

    @Test
    void refusesToSendWhenDisabled() {
        var properties = new TeamsProperties(
                false,
                URI.create("https://example.invalid/token"),
                URI.create("https://example.invalid/send"),
                "sample-client", "sample-secret", "sample-role",
                "X-App-Client", "TargetAppRole", "APIM", "sample-value",
                Duration.ofSeconds(5), Duration.ofSeconds(15));

        var client = new TeamsGatewayClient(RestClient.builder(), properties);

        assertThatThrownBy(() -> client.send(null))
                .isInstanceOf(TeamsGatewayException.class)
                .hasMessageContaining("disabled");
    }
}
