package io.github.mbesoft.bp.teams.runner;

import io.github.mbesoft.bp.teams.model.CampaignNotification;
import io.github.mbesoft.bp.teams.service.TeamsNotificationService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.LocalDate;

/**
 * Optional local integration runner.
 *
 * This bean is created only when the "local" profile is active AND
 * bp.teams.enabled=true. The default local profile never sends anything.
 *
 * IMPORTANT: Enabling this runner causes one real send attempt at every startup.
 */
@Component
@Profile("local")
@ConditionalOnProperty(prefix = "bp.teams", name = "enabled", havingValue = "true")
public class LocalTeamsRunner implements ApplicationRunner {

    private final TeamsNotificationService teamsService;

    public LocalTeamsRunner(TeamsNotificationService teamsService) {
        this.teamsService = teamsService;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Only synthetic values are stored in source control.
        CampaignNotification notification = new CampaignNotification(
                "BP - Local Integration Test",
                "Open",
                "Testing a Teams notification from Spring Boot.",
                LocalDate.now().plusDays(7),
                URI.create("https://example.invalid/test")
        );

        // This call executes only after the user explicitly enables sending.
        teamsService.sendCampaign(notification);
    }
}
