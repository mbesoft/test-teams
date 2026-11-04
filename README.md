# bp-teams-notifier

A standard Maven / Spring Boot project targeting **Java 11** with **Spring Boot 2.7.18**. It sends Microsoft Teams channel notifications via an existing OAuth2-secured HTTP gateway, without using Microsoft Graph or any Teams-specific SDK.

> **Lifecycle notice:** Spring Boot 2.7 has reached the end of open-source support. This version is used to satisfy the Java 11 requirement. Plan an upgrade to a supported Java and Spring Boot release in production where possible, or use your organization's approved extended-support distribution.

## Project layout

```text
bp-teams-notifier/
├── .mvn/wrapper/maven-wrapper.properties
├── mvnw
├── mvnw.cmd
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/io/github/mbesoft/bp/teams/
    │   │   ├── BpTeamsApplication.java
    │   │   ├── card/AdaptiveCardFactory.java
    │   │   ├── client/
    │   │   │   ├── TeamsGatewayClient.java
    │   │   │   └── TeamsGatewayException.java
    │   │   ├── config/TeamsProperties.java
    │   │   ├── model/
    │   │   │   ├── AdaptiveCard.java
    │   │   │   ├── CampaignNotification.java
    │   │   │   ├── TextBlock.java
    │   │   │   └── OpenUrlAction.java
    │   │   ├── runner/LocalTeamsRunner.java
    │   │   └── service/TeamsNotificationService.java
    │   └── resources/
    │       ├── application.yml
    │       ├── application-local.yml
    │       ├── application-qual.yml
    │       └── application-prod.yml
    └── test/java/io/github/mbesoft/bp/teams/
        ├── AdaptiveCardFactoryTest.java
        ├── TeamsGatewayClientTest.java
        └── TeamsNotificationServiceTest.java
```

## Java 11 compatibility

- Java 11 bytecode is enforced in `pom.xml` with `maven.compiler.release=11`.
- Spring Boot 2.7.18 uses Spring Framework 5.3 and `javax.validation` annotations.
- `RestTemplate` implements the OAuth2 request and JSON notification POST; `RestClient` is not available in this Spring generation.
- Immutable JavaBean-style DTOs replace Java `record` types, which are not available in Java 11.
- Jackson serializes the Adaptive Card into JSON. The optional `$schema` field is not included in the current repository version.

## Notification flow

1. A business component calls `TeamsNotificationService.sendCampaign(notification)`.
2. `AdaptiveCardFactory` constructs the card in memory.
3. `TeamsGatewayClient` requests an OAuth2 access token using `client_credentials` and HTTP Basic authentication.
4. The client sends the JSON card and the configured authorization/routing headers.
5. The gateway determines the channel using the configured target role.

## Spring profiles

| Profile | Configuration file | Behavior |
| --- | --- | --- |
| `local` | `application-local.yml` | Default; synthetic values and sending disabled |
| `qual` | `application-qual.yml` | Runtime-provided qualification configuration |
| `prod` | `application-prod.yml` | Runtime-provided production configuration |

Shared configuration is in `application.yml`. No real credentials or internal URLs should be checked into Git.

### Runtime configuration

Use your secret manager or the deployment environment to provide:

- `BP_TEAMS_CLIENT_ID`: OAuth2 client identifier.
- `BP_TEAMS_CLIENT_SECRET`: OAuth2 client secret.
- `BP_TEAMS_TARGET_ROLE`: gateway routing role.
- `BP_TEAMS_IDENTITY_HEADER_NAME`: the expected application-identity header name.
- `BP_TEAMS_APIM_VALUE`: API routing header value.
- `BP_TEAMS_QUAL_TOKEN_URL`, `BP_TEAMS_QUAL_SEND_URL`: qualification destinations.
- `BP_TEAMS_PROD_TOKEN_URL`, `BP_TEAMS_PROD_SEND_URL`: production destinations.

The application identity header value comes from the configured client ID. TLS certificate verification remains enabled.

## Build and isolated unit tests

Java 11 and Maven 3.8+ are required. Alternatively, use the included Maven Wrapper.

```bash
java -version
./mvnw clean verify
```

The existing tests are isolated unit tests: they do not call OAuth2, the Teams gateway, or any other external endpoint.

## Running locally

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The `LocalTeamsRunner` bean exists only with the `local` profile **and** `bp.teams.enabled=true`. With the default `enabled=false`, startup does not attempt to send a message. **If you enable sending for the local profile, the runner initiates a real notification on every application startup.** Do not enable it without authorization.

The application is configured as a non-web Spring Boot process; it exposes no inbound HTTP endpoint.

### Calling from another Spring component

```java
notifications.sendCampaign(new CampaignNotification(
        "Campaign 2026",
        "Open",
        "Please complete your review",
        LocalDate.of(2026, 10, 31),
        URI.create("https://example.invalid/campaign/42")
));
```

This example contains only synthetic data. It is never executed by the documentation.

## Security

- Do not commit secrets, real gateway URLs, tokens or internal certificates.
- Keep `.env` files, private keys and truststores outside source control.
- Failed gateway requests are wrapped in safe exceptions without logging credentials or raw response bodies.
- TLS verification stays enabled; install an approved internal truststore when required.
- No live integration requests were made during the Java 11 migration.
