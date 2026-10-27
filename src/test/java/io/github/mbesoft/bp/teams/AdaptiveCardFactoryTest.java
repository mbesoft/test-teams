package io.github.mbesoft.bp.teams;
import io.github.mbesoft.bp.teams.card.AdaptiveCardFactory;
import io.github.mbesoft.bp.teams.model.CampaignNotification;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
class AdaptiveCardFactoryTest {
 @Test void buildsCardWithoutExternalSchema() {
  var result = new AdaptiveCardFactory().campaign(new CampaignNotification("Campagne", "Ouverte", "Action attendue", LocalDate.of(2026,10,31), URI.create("https://example.invalid/42")));
  assertThat(result.body()).hasSize(4);
  assertThat(result.body().get(3).text()).isEqualTo("Échéance : 31/10/2026");
  assertThat(result.actions().get(0).title()).isEqualTo("OUVRIR DANS BP");
 }
}
