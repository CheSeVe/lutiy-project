package ru.CheSeVe.lutiy_project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.dto.api.MatchResponse;
import ru.CheSeVe.lutiy_project.dto.api.NameResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

@Service
public class ApiService {

    private static final Logger log = LoggerFactory.getLogger(ApiService.class);

    private static final Logger failedLog = LoggerFactory.getLogger("FailedMatchesLogger");
    HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final String API_URL = "https://api.stratz.com/graphql";
    @Value("${stratz.api.key}")
    private String apiKey;

    public Optional<NameResponse> getPlayerName(Long steamAccountId) {
        HttpResponse<String> response = null;
        try {
            String graphqlQuery = """
                    { "query": "query GetPlayer($steamAccountId: Long!) { player(steamAccountId: $steamAccountId) { names(take: 1) { name } } }",
                      "variables": { "steamAccountId": %d }
                       }
                    """.formatted(steamAccountId);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(graphqlQuery))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            return Optional.ofNullable(mapper.readValue(response.body(), NameResponse.class));
        } catch (JsonProcessingException e) {
            failedLog.error("matchId={} reason=JSON_PARSING_ERROR body={} message={}",
                    steamAccountId, response != null ? response.body() : null, e.getMessage(), e);
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            failedLog.error("matchId={} reason=INTERRUPTED message={}", steamAccountId, e.getMessage(), e);
            throw new RuntimeException("Error executing request", e);
        } catch (IOException e){
            failedLog.error("matchId={} reason=IO_ERROR message={}", steamAccountId, e.getMessage(), e);
            return Optional.empty();
        } catch (Exception e) {
            failedLog.error("matchId={} reason=HTTP_ERROR message={}", steamAccountId, e.getMessage());
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
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(graphqlQuery))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            log.info("Response from API: {}", response.body());
            return Optional.ofNullable(mapper.readValue(response.body(), MatchResponse.class));
        } catch (JsonProcessingException e) {
            failedLog.error("matchId={} reason=JSON_PARSING_ERROR body={} message={}",
                    matchId, response != null ? response.body() : null, e.getMessage());
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            failedLog.error("matchId={} reason=INTERRUPTED message={}", matchId, e.getMessage());
            throw new RuntimeException("Error executing request", e);
        } catch (IOException e){
            failedLog.error("matchId={} reason=IO_ERROR message={}", matchId, e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            failedLog.error("matchId={} reason=HTTP_ERROR message={}", matchId, e.getMessage());
            return Optional.empty();
        }
    }
}
