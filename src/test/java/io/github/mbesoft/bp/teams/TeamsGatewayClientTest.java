package io.github.mbesoft.bp.teams;

import io.github.mbesoft.bp.teams.client.TeamsGatewayClient;
import io.github.mbesoft.bp.teams.client.TeamsGatewayException;
import io.github.mbesoft.bp.teams.config.TeamsProperties;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Tests only the local safety guard, without performing any HTTP request. */
class TeamsGatewayClientTest {

    @Test
    void refusesToSendWhenDisabled() {
        TeamsProperties properties = new TeamsProperties(
                false,
                URI.create("https://example.invalid/token"),
                URI.create("https://example.invalid/send"),
                "sample-client", "sample-secret", "sample-role",
                "X-App-Client", "TargetAppRole", "APIM", "sample-value",
                Duration.ofSeconds(5), Duration.ofSeconds(15));

        TeamsGatewayClient client = new TeamsGatewayClient(properties);

        assertThatThrownBy(() -> client.send(null))
                .isInstanceOf(TeamsGatewayException.class)
                .hasMessageContaining("disabled");
    }
}
