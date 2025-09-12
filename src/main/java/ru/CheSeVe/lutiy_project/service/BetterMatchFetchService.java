package ru.CheSeVe.lutiy_project.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.dto.api.DataDTOForMatch;
import ru.CheSeVe.lutiy_project.dto.api.ErrorDTO;
import ru.CheSeVe.lutiy_project.dto.api.MatchDTO;
import ru.CheSeVe.lutiy_project.dto.api.MatchResponse;
import ru.CheSeVe.lutiy_project.entity.Match;
import ru.CheSeVe.lutiy_project.enums.LobbyTypeEnum;
import ru.CheSeVe.lutiy_project.repository.MatchIdRepository;
import ru.CheSeVe.lutiy_project.repository.MatchRepository;
import ru.CheSeVe.lutiy_project.service.mapper.MatchMapper;
import java.util.Optional;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BetterMatchFetchService {

    public static final Logger log = LoggerFactory.getLogger(BetterMatchFetchService.class);

    final MatchRepository matchRepository;
    final StratzApiService stratzApiService;

    final MatchIdRepository matchIdRepository;

    final MatchMapper mapper;

    private Long lastSleep = System.currentTimeMillis();

    private static final Long RETRY_DELAY = 3000L;

    public BetterMatchFetchService(MatchRepository matchRepository, StratzApiService stratzApiService, MatchIdRepository matchIdRepository, MatchMapper mapper) {
        this.matchRepository = matchRepository;
        this.stratzApiService = stratzApiService;
        this.matchIdRepository = matchIdRepository;
        this.mapper = mapper;
    }
    @Scheduled(fixedRate = 9000L)
    public void getAndSaveMatches(){

        try {
            Long now = System.currentTimeMillis();

            if (now - lastSleep >= 4*60*60*1000L) {
                log.info("taking a 5 minute break zzZ...");
                Thread.sleep(5*60*1000L);
                lastSleep = System.currentTimeMillis();
            }

            Optional<Long> optMatchId = matchIdRepository.findMinMatchId();

            if (optMatchId.isEmpty()) {
                log.info("no id's in matchId repository");
                return;
            }

            Long matchId = optMatchId.get();

            if (matchRepository.existsById(matchId)) {
                log.info("match with id {} already in database", matchId);
                return;
            }

            log.info("Fetching match with id {}", matchId);

            Optional<MatchDTO> dtoOptional = fetch(matchId);

            if (dtoOptional.isEmpty()) {
                log.info("Stratz api returned empty match for matchId={}", matchId);
                return;
            }

            MatchDTO dto = dtoOptional.get();

            if (isValidMatch(dto)) {
                Match match = mapper.map(dto);
                matchRepository.save(match);
                log.info("saved match {} to database", matchId);
            } else {log.info("skipped match {}. Bracket={} LobbyType={}", matchId, dto.bracket(), dto.lobbyType());}

            matchIdRepository.deleteById(matchId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Thread was interrupted while running");
        }
    }

    private Optional<MatchDTO> fetch(Long matchId) throws InterruptedException {
        for (int attempt = 1; attempt <= 2; attempt++) {

            Optional<MatchResponse> matchResponse = stratzApiService.getMatch(matchId);

            if (matchResponse.isEmpty() || matchResponse.get().errors() != null) {

                if (matchResponse.isPresent()) {
                    for (ErrorDTO error : matchResponse.get().errors()) {
                        log.info("Error getting match {}: {}", matchId,
                                error.message());
                    }
                }

                if (attempt < 2) {
                    log.info("Retrying getting data after 3s");
                    Thread.sleep(RETRY_DELAY);
                }
            } else {
                return matchResponse.map(MatchResponse::data)
                        .map(DataDTOForMatch::match);
            }
        }
        return Optional.empty();
    }

    private boolean isValidMatch(MatchDTO dto) {
        return dto.lobbyType() == LobbyTypeEnum.RANKED && dto.bracket() >= 8;
    }
}
