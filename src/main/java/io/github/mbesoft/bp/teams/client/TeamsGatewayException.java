package io.github.mbesoft.bp.teams.client;

/**
 * Safe exception returned to callers without copying potentially sensitive
 * gateway responses, access tokens or authorization headers.
 */
public class TeamsGatewayException extends RuntimeException {
    public TeamsGatewayException(String message) {
        super(message);
    }
}
