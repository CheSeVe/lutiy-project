package ru.CheSeVe.lutiy_project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.dto.api.opendota.MatchDTO;
import ru.CheSeVe.lutiy_project.repository.MatchIdRepository;
import ru.CheSeVe.lutiy_project.repository.MatchRepository;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OpenDotaApiService {
    private static final Logger log = LoggerFactory.getLogger(OpenDotaApiService.class);

    private static final Logger failedLog = LoggerFactory.getLogger("FailedMatchesLogger");

    private static final Map<Class<? extends Exception>, String> REASONS = Map.of(
            JsonProcessingException.class, "JSON_PARSING_ERROR",
            IOException.class, "IO_ERROR",
            InterruptedException.class, "INTERRUPTED"
    );

    private static final int LOBBY_TYPE = 7;

    private static final int AVG_RANK_TIER = 80;

    private static final int MATCHES_PER_REQUEST = 500;

    private static final Long DEFAULT_START_MATCH_ID = 8409138129L;

    private static final String BASE_URL = "https://api.opendota.com/api/explorer?sql=";

    private static final String SQL_TEMPLATE = """
            SELECT
              match_id
            FROM public_matches
            WHERE lobby_type = %d
              AND avg_rank_tier >= %d
              AND match_id > %d
            ORDER BY match_id ASC
            LIMIT %d;""";


    HttpClient client;

    ObjectMapper mapper;

    MatchIdRepository matchIdRepository;

    public OpenDotaApiService(ObjectMapper mapper, MatchIdRepository matchIdRepository) {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(60))
                .build();
        this.mapper = mapper;
        this.matchIdRepository = matchIdRepository;

    }

    public Optional<List<MatchDTO>> getMatches(){
        HttpResponse<String> response = null;
        try {
            Long matchId = matchIdRepository.findMaxMatchId().orElse(DEFAULT_START_MATCH_ID);

            String rawSqlQuery = String.format(SQL_TEMPLATE,
                    LOBBY_TYPE,
                    AVG_RANK_TIER,
                    matchId,
                    MATCHES_PER_REQUEST);

            String encodedSqlQuery = URLEncoder.encode(rawSqlQuery, StandardCharsets.UTF_8);

            String url = BASE_URL + encodedSqlQuery;

            log.debug("Request url for OpenDota API Explorer = {}", url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("HTTP Status Code: {}", response.statusCode());
            log.info("Raw API Response Body: {}", response.body());
            String jsonString = response.body();

            JsonNode rootNode = mapper.readTree(jsonString);

            JsonNode rowsNode = rootNode.get("rows");
            if (rowsNode == null || !rowsNode.isArray()) {
                log.warn("field \"rows\" does not exist or is not an array in API response={}", jsonString);
                return Optional.empty();
            }

            JavaType matchListType = mapper.getTypeFactory().constructCollectionType(List.class, MatchDTO.class);
            List<MatchDTO> matchList = mapper.readValue(rowsNode.toString(), matchListType);
            log.info("Got {} matchIds from OpenDota Api", matchList.size());
            return Optional.of(matchList);
        }  catch (Exception e) {
            String reason = REASONS.getOrDefault(e.getClass(), "HTTP_ERROR");

            failedLog.error("reason={}, body={}, message={}", reason,
                    response != null ? response.body() : null,
                    e.getMessage());

            if (e instanceof InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Request interrupted", e);
            }

            return Optional.empty();

        }
    }
}
