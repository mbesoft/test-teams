# bp-teams-notifier

Service Java 17, Maven et Spring Boot 3.5.7 pour envoyer une **Adaptive Card** vers un canal Teams via une passerelle HTTP interne utilisant OAuth2.

## Architecture
- `TeamsNotificationService` : façade métier ; méthode `sendCampaign`.
- `AdaptiveCardFactory` : produit le DTO de la carte sans bibliothèque spécialisée ni propriété `$schema`.
- `TeamsGatewayClient` : récupère le jeton OAuth2 puis effectue le POST.
- `TeamsProperties` : paramètres externalisés et URLs HTTPS.

Le canal destinataire est résolu par la passerelle à partir de `TargetAppRole` ; le service n'a pas besoin de connaître l'identifiant de canal.

## Configuration
Ne mettez **aucune valeur réelle** dans le dépôt. Configurez via variables d'environnement, avec le fichier `.env.example` comme simple inventaire :

- `BP_TEAMS_CLIENT_ID`, `BP_TEAMS_CLIENT_SECRET` : OAuth2 client credentials.
- `BP_TEAMS_TARGET_ROLE` : rôle cible pour le routage du canal.
- `BP_TEAMS_IDENTITY_HEADER_NAME` : nom d'en-tête applicatif attendu par la passerelle.
- `BP_TEAMS_APIM_VALUE` : valeur du champ `APIM`.
- Profil `qual` : `BP_TEAMS_QUAL_TOKEN_URL`, `BP_TEAMS_QUAL_SEND_URL`.
- Profil `prod` : `BP_TEAMS_PROD_TOKEN_URL`, `BP_TEAMS_PROD_SEND_URL`.

Le nom d'en-tête applicatif est volontairement configuré hors du code. Sa valeur est le client ID. La vérification TLS n'est pas désactivée. Installer la chaîne de confiance dans un truststore si nécessaire.

## Utilisation depuis un autre bean

```java
notifications.sendCampaign(new CampaignNotification(
    "Campagne 2026",
    "Ouverte",
    "Votre contribution est attendue.",
    LocalDate.of(2026, 10, 31),
    URI.create("https://example.invalid/campaign/42")));
```

Il s'agit de données fictives ; ce fragment n'est pas exécuté.

## Commandes Maven

```bash
mvn test
mvn clean package
SPRING_PROFILES_ACTIVE=qual mvn spring-boot:run
```

Par défaut, aucun message n'est envoyé au démarrage et aucun endpoint public n'est exposé. Les tests unitaires ne sollicitent aucun réseau. **Aucun appel réel d'authentification ni de publication n'a été effectué.**

## Sécurité

Aucun secret ni URL interne dans Git. `.env`, certificats et clés sont ignorés. Ne jamais journaliser le jeton ou les réponses brutes de la passerelle. Faire valider le contrat HTTP par l'équipe exploitant la passerelle avant toute utilisation opérationnelle.
