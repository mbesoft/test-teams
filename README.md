# bp-teams-notifier

A standard **Spring Initializr-style Maven project** using **Java 17** and **Spring Boot 3.5.7**. It publishes Microsoft Teams channel notifications through an existing OAuth2-secured HTTP gateway. There is no Teams-specific SDK and no direct Microsoft Graph integration.

## Project layout

```text
bp-teams-notifier/
├── .mvn/wrapper/maven-wrapper.properties
├── mvnw
├── mvnw.cmd
├── pom.xml
├── .gitignore
├── .gitattributes
├── .env.example
├── src/
│   ├── main/
│   │   ├── java/io/github/mbesoft/bp/teams/
│   │   │   ├── BpTeamsApplication.java
│   │   │   ├── card/AdaptiveCardFactory.java
│   │   │   ├── client/TeamsGatewayClient.java
│   │   │   ├── client/TeamsGatewayException.java
│   │   │   ├── config/TeamsProperties.java
│   │   │   ├── model/
│   │   │   └── service/TeamsNotificationService.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-qual.yml
│   │       └── application-prod.yml
│   └── test/java/io/github/mbesoft/bp/teams/
│       ├── AdaptiveCardFactoryTest.java
│       ├── TeamsNotificationServiceTest.java
│       └── TeamsGatewayClientTest.java
```

## How it works

1. A Spring business component calls `TeamsNotificationService.sendCampaign(...)`.
2. `AdaptiveCardFactory` creates a typed Adaptive Card with title, stage, instructions, due date and an **OPEN IN BP** action.
3. `TeamsGatewayClient` obtains an OAuth2 token using the `client_credentials` grant and HTTP Basic authentication.
4. The client posts the card JSON with a bearer token and the gateway-specific headers.
5. The gateway uses the configured **TargetAppRole** to determine the destination channel.

The optional `$schema` property is intentionally absent. Jackson serializes the Java records to JSON without downloading an Adaptive Cards library or schema.

## Profiles

| Profile | File | Behavior |
| --- | --- | --- |
| `local` | `application-local.yml` | Default. Uses synthetic `example.invalid` URLs. **Sending is disabled.** |
| `qual` | `application-qual.yml` | Uses environment-provided qualification endpoints and credentials. Sending enabled. |
| `prod` | `application-prod.yml` | Uses environment-provided production endpoints and credentials. Sending enabled. |

Common settings, including timeouts and gateway header names, are in `application.yml`. If no profile is selected, Spring uses `local`.

### Environment variables

For `qual` and `prod`, provide the following via your approved secret manager or process environment:

- `BP_TEAMS_CLIENT_ID`: OAuth2 client ID.
- `BP_TEAMS_CLIENT_SECRET`: OAuth2 client secret.
- `BP_TEAMS_TARGET_ROLE`: routing role for the Teams channel.
- `BP_TEAMS_IDENTITY_HEADER_NAME`: optional override of the application identity header name.
- `BP_TEAMS_APIM_VALUE`: gateway APIM header value.
- `BP_TEAMS_QUAL_TOKEN_URL` and `BP_TEAMS_QUAL_SEND_URL`: qualification endpoints.
- `BP_TEAMS_PROD_TOKEN_URL` and `BP_TEAMS_PROD_SEND_URL`: production endpoints.

The application identity header name is configurable; no organization-specific name appears in source control. The identity header value is the configured client ID. Do not commit secrets, tokens, internal endpoints, certificate files or local `.env` files.

## Build and run

Requires JDK 17 or newer. The checked-in Maven Wrapper can download the specified Maven distribution from Maven Central if Maven is not already installed.

```bash
# Build and run offline-only unit tests (no gateway calls):
./mvnw clean verify

# Start in the safe local profile:
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Start in qualification only when explicitly authorized and configured:
./mvnw spring-boot:run -Dspring-boot.run.profiles=qual
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

The application runs as a non-web Spring Boot process; it does not expose a REST endpoint and does **not** automatically publish messages at startup.

### Calling the service from another Spring bean

```java
@Service
class CampaignWorkflow {
    private final TeamsNotificationService notifications;

    CampaignWorkflow(TeamsNotificationService notifications) {
        this.notifications = notifications;
    }

    void notifyReviewers() {
        notifications.sendCampaign(new CampaignNotification(
            "Campaign 2026", "Open", "Please complete your review",
            LocalDate.of(2026, 10, 31),
            URI.create("https://example.invalid/campaign/42")
        ));
    }
}
```

The example URL and data are synthetic and are not executed.

## Security notes

- TLS verification remains enabled. Configure a Java truststore if an approved private certificate authority is required.
- The `local` profile rejects send attempts **before** the first network request.
- Both OAuth2 and notification errors are reduced to safe application messages; raw response bodies and bearer tokens are not logged.
- The gateway request headers and routing contract must be verified with the gateway's owner before production deployment.
- Tokens are requested for each send, matching the simple reference flow. Token caching or retry policies can be added later if required.
- Unit tests use synthetic data and Mockito mocks; **no real credentials or external Teams calls were used to create this project**.
