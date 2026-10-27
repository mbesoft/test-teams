package io.github.mbesoft.bp.teams.card;
import io.github.mbesoft.bp.teams.model.*;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Component
public class AdaptiveCardFactory {
 public AdaptiveCard campaign(CampaignNotification n) {
  return new AdaptiveCard("AdaptiveCard", "1.0", List.of(
   new TextBlock("TextBlock", "Title", n.name(), "Medium", "ExtraLarge", "Bolder", "Accent", null, true),
   new TextBlock("TextBlock", "Stage", "Stade : " + n.stage(), null, null, null, null, true, true),
   new TextBlock("TextBlock", "Message", n.message(), null, null, null, null, null, true),
   new TextBlock("TextBlock", "Deadline", "Échéance : " + n.deadline().format(DateTimeFormatter.ofPattern("dd/MM/uuuu")), null, null, "Bolder", null, null, true)
  ), List.of(new OpenUrlAction("Action.OpenUrl", "OUVRIR DANS BP", n.deepLink().toString())));
 }
}
