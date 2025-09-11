package ru.CheSeVe.lutiy_project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.dto.api.HeroesResponse;
import ru.CheSeVe.lutiy_project.dto.api.ItemsResponse;
import ru.CheSeVe.lutiy_project.dto.api.MatchResponse;
import ru.CheSeVe.lutiy_project.dto.api.NameResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StratzApiService {

    private static final Logger log = LoggerFactory.getLogger(StratzApiService.class);

    private static final Logger failedLog = LoggerFactory.getLogger("FailedMatchesLogger");

    static String API_URL = "https://api.stratz.com/graphql";

    private static final Map<Class<? extends Exception>, String> REASONS = Map.of(
            JsonProcessingException.class, "JSON_PARSING_ERROR",
            IOException.class, "IO_ERROR",
            InterruptedException.class, "INTERRUPTED"
    );
    HttpClient client;
    ObjectMapper mapper;

    String apiKey;

    public StratzApiService(ObjectMapper mapper, @Value("${stratz.api.key}") String apiKey) {
        this.mapper = mapper;
        this.apiKey = apiKey;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    private HttpResponse<String> sendGraphQLRequest(String graphqlQuery) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(graphqlQuery))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public Optional<NameResponse> getPlayerName(Long steamAccountId) {
        HttpResponse<String> response = null;
        try {
            String graphqlQuery = """
                    { "query": "query GetPlayer($steamAccountId: Long!) { player(steamAccountId: $steamAccountId) { names(take: 1) { name } } }",
                      "variables": { "steamAccountId": %d }
                       }
                    """.formatted(steamAccountId);

            response = sendGraphQLRequest(graphqlQuery);

            return Optional.ofNullable(mapper.readValue(response.body(), NameResponse.class));
        } catch (Exception e) {
            String reason = REASONS.getOrDefault(e.getClass(), "HTTP_ERROR");

            failedLog.error("matchId={} reason={} body={} message={}", steamAccountId,
                    reason,
                    response != null ? response.body() : null,
                    e.getMessage());

            if (e instanceof InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Request interrupted", e);
            }

            return Optional.empty();
        }
    }

    public Optional<MatchResponse> getMatch(Long matchId) {
        HttpResponse<String> response = null;
        try {
            String graphqlQuery = """
                    { "query": "query GetMatch($id: Long!) { match(id: $id) { id, lobbyType, bracket, startDateTime, players { isVictory, heroId, item0Id, item1Id, item2Id, item3Id, item4Id, item5Id, backpack0Id, backpack1Id, backpack2Id, neutral0Id } } }",
                      "variables": { "id": %d}
                    }
                    """.formatted(matchId);

            response = sendGraphQLRequest(graphqlQuery);

            log.info("Response from API: {}", response.body());
            return Optional.ofNullable(mapper.readValue(response.body(), MatchResponse.class));
        } catch (Exception e) {
            String reason = REASONS.getOrDefault(e.getClass(), "HTTP_ERROR");

            failedLog.error("matchId={} reason={} body={} message={}", matchId,
                    reason,
                    response != null ? response.body() : null,
                    e.getMessage());

            if (e instanceof InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Request interrupted", e);
            }

            return Optional.empty();
        }
    }

    public Optional<ItemsResponse> getItems() {
        HttpResponse<String> response = null;
        try {
            String graphqlQuery = """
                    { "query": "{ constants { items { id, name, displayName } } }" }
                    """;

            response = sendGraphQLRequest(graphqlQuery);

            return Optional.ofNullable(mapper.readValue(response.body(), ItemsResponse.class));
        } catch (Exception e) {
            String reason = REASONS.getOrDefault(e.getClass(), "HTTP_ERROR");

            failedLog.error("reason={} body={} message={}", reason,
                    response != null ? response.body() : null,
                    e.getMessage());

            if (e instanceof InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Request interrupted", ie);
            }
            return Optional.empty();
        }
    }

    public Optional<HeroesResponse> getHeroes() {
        HttpResponse<String> response = null;

        try {
            String graphqlQuery = """
                    { "query": "{ constants { heroes { id, name, displayName } } }" }
                    """;

            response = sendGraphQLRequest(graphqlQuery);

            return Optional.ofNullable(mapper.readValue(response.body(), HeroesResponse.class));
        } catch(Exception e) {
            String reason = REASONS.getOrDefault(e.getClass(), "HTTP_ERROR");

            failedLog.error("reason={} body={} message={}", reason,
                    response != null ? response.body() : null,
                    e.getMessage());

            if (e instanceof InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Request interrupted", ie);
            }
            return Optional.empty();
        }
    }
}
