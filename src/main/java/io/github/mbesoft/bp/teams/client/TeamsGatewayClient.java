package io.github.mbesoft.bp.teams.client;
import io.github.mbesoft.bp.teams.config.TeamsProperties;
import io.github.mbesoft.bp.teams.model.AdaptiveCard;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import java.time.Duration;
import java.util.Map;
@Component
public class TeamsGatewayClient {
 private final RestClient http;
 private final TeamsProperties settings;
 public TeamsGatewayClient(RestClient.Builder builder, TeamsProperties settings) {
  this.settings = settings;
  var factory = new SimpleClientHttpRequestFactory();
  factory.setConnectTimeout(Duration.ofSeconds(5));
  factory.setReadTimeout(Duration.ofSeconds(15));
  this.http = builder.requestFactory(factory).build();
 }
 public void send(AdaptiveCard card) {
  String token = fetchToken();
  try {
   http.post().uri(settings.sendUrl())
    .headers(h -> {
     h.setBearerAuth(token);
     h.set("TargetAppRole", settings.targetRole());
     h.set(settings.identityHeaderName(), settings.clientId());
     h.set("APIM", settings.apimValue());
    })
    .contentType(MediaType.APPLICATION_JSON).body(card).retrieve().toBodilessEntity();
  } catch (RestClientException ex) {
   throw new TeamsGatewayException("Échec de l'envoi via la passerelle Teams");
  }
 }
 private String fetchToken() {
  var form = new LinkedMultiValueMap<String,String>();
  form.add("grant_type", "client_credentials");
  try {
   @SuppressWarnings("unchecked")
   Map<String,Object> response = http.post().uri(settings.tokenUrl())
    .headers(h -> h.setBasicAuth(settings.clientId(), settings.clientSecret()))
    .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form).retrieve().body(Map.class);
   if (response == null || !(response.get("access_token") instanceof String token) || token.isBlank())
    throw new TeamsGatewayException("OAuth2 : jeton absent");
   return token;
  } catch (RestClientException ex) {
   throw new TeamsGatewayException("Échec d'authentification OAuth2");
  }
 }
}
