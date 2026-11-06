package io.github.mbesoft.bp.teams.card;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.mbesoft.bp.teams.model.CampaignNotification;
import io.github.mbesoft.bp.teams.model.CampaignOverviewNotification;
import io.github.mbesoft.bp.teams.model.VotingUnitStatus;
import io.github.mbesoft.bp.teams.model.VotingUnitSummary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Uses the designer JSON file at the repository root as a template.
 * Maven packages that same file under cards/ (see pom.xml).
 * Existing single-campaign AdaptiveCardFactory remains unchanged.
 */
@Component
public class CampaignOverviewCardFactory {

    private static final String TEMPLATE = "cards/broker-voting-campaign-adaptive-card.json";
    private static final DateTimeFormatter DEADLINE_FORMAT =
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH);

    private final ObjectMapper objectMapper;

    public CampaignOverviewCardFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ObjectNode create(CampaignOverviewNotification notification) {
        ObjectNode card = loadTemplate();
        CampaignNotification campaign = notification.getCampaign();

        objectAt(card, "/body/0").put(
                "text", "BROKER VOTING  /  " + campaign.getDeadline().getYear());
        objectAt(card, "/body/1").put("text", campaign.getName());
        objectAt(card, "/body/2").put("text", campaign.getMessage());

        ObjectNode stageText = objectAt(card, "/body/3/columns/0/items/1");
        stageText.put("text", campaign.getStage().toUpperCase(Locale.ENGLISH));
        stageText.put("color", campaign.getStage().toUpperCase(Locale.ENGLISH)
                .contains("OPEN") ? "Good" : "Accent");
        objectAt(card, "/body/3/columns/1/items/1").put(
                "text", campaign.getDeadline().format(DEADLINE_FORMAT));
        objectAt(card, "/body/4").put(
                "text", "VOTING UNITS (" + notification.getVotingUnits().size() + ")");
        objectAt(card, "/actions/0").put("url", campaign.getDeepLink().toString());

        ArrayNode originalBody = (ArrayNode) card.get("body");
        JsonNode openTemplate = originalBody.get(5);
        JsonNode nonOpenTemplate = originalBody.get(7);
        JsonNode footerTemplate = originalBody.get(originalBody.size() - 1);

        ArrayNode updatedBody = objectMapper.createArrayNode();
        for (int i = 0; i < 5; i++) {
            updatedBody.add(originalBody.get(i).deepCopy());
        }
        for (VotingUnitSummary unit : notification.getVotingUnits()) {
            boolean open = unit.getStatus() == VotingUnitStatus.VOTING_OPEN;
            ObjectNode unitCard = (ObjectNode) (open ? openTemplate : nonOpenTemplate).deepCopy();

            objectAt(unitCard, "/items/0/columns/0/items/0").put("text", unit.getName());
            ObjectNode statusText = objectAt(unitCard, "/items/0/columns/1/items/0");
            statusText.put("text", unit.getStatus().getLabel());
            statusText.put("color", unit.getStatus().getColor());

            if (open) {
                int done = unit.getDonePercentage();
                int completedSegments = (int) Math.round(done / 10.0);
                objectAt(unitCard, "/items/2/inlines/0").put(
                        "text", "█".repeat(completedSegments));
                objectAt(unitCard, "/items/2/inlines/1").put(
                        "text", "█".repeat(10 - completedSegments));
                objectAt(unitCard, "/items/3/columns/0/items/0").put(
                        "text", done + "% done");
                objectAt(unitCard, "/items/3/columns/1/items/0").put(
                        "text", unit.getPendingPercentage() + "% pending");
                objectAt(unitCard, "/items/4/actions/0").put(
                        "url", unit.getDeepLink().toString());
            } else {
                objectAt(unitCard, "/items/1").put(
                        "text", unit.getStatus().getDescription());
                objectAt(unitCard, "/items/2/actions/0").put(
                        "url", unit.getDeepLink().toString());
            }
            updatedBody.add(unitCard);
        }
        updatedBody.add(footerTemplate.deepCopy());
        card.set("body", updatedBody);
        return card;
    }

    private ObjectNode loadTemplate() {
        try (InputStream input = new ClassPathResource(TEMPLATE).getInputStream()) {
            JsonNode parsed = objectMapper.readTree(input);
            if (!(parsed instanceof ObjectNode)) {
                throw new IllegalStateException("Campaign overview template must be a JSON object");
            }
            return (ObjectNode) parsed;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load campaign overview card template", exception);
        }
    }

    private static ObjectNode objectAt(JsonNode parent, String pointer) {
        JsonNode node = parent.at(pointer);
        if (!(node instanceof ObjectNode)) {
            throw new IllegalStateException("Invalid campaign overview template at " + pointer);
        }
        return (ObjectNode) node;
    }
}
