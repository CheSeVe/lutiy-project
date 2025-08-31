package ru.CheSeVe.lutiy_project.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.dto.api.*;
import ru.CheSeVe.lutiy_project.entity.Match;
import ru.CheSeVe.lutiy_project.enums.LobbyTypeEnum;
import ru.CheSeVe.lutiy_project.repository.MatchRepository;
import ru.CheSeVe.lutiy_project.service.mapper.MatchMapper;

import java.util.Optional;

@Service
@Transactional
public class MatchFetchService {

    private static final int RETRY_DELAY = 3000;
    @Autowired
    MatchIdService matchIdService;
    @Autowired
    ApiService apiService;
    @Autowired
    MatchRepository repository;

    @Autowired
    MatchMapper mapper;

    private Long lastSleep = System.currentTimeMillis();

    private static final Logger log = LoggerFactory.getLogger(MatchFetchService.class);

    @Scheduled(fixedRate = 9000)
    public void validateAndSave() throws InterruptedException {
        Long now = System.currentTimeMillis();

        if (now - lastSleep >= 4*60*60*1000L) {
            log.info("taking a 5 minute break zzZ...");
            Thread.sleep(5*60*1000L);
            lastSleep = System.currentTimeMillis();
        }

        Long matchId = matchIdService.getNextMatchId();
        log.info("Fetching match with id {}", matchId);

        Optional<MatchDTO> dtoOptional = fetch(matchId);

        if (dtoOptional.isEmpty()) {
            return;
        }

        MatchDTO dto = dtoOptional.get();

        if (isValidMatch(dto)) {
            Match match = mapper.map(dto);
            repository.save(match);
            log.info("saved match {} to database", matchId);
        } else {log.info("skipped match {}. Bracket={} LobbyType={}", matchId, dto.bracket(), dto.lobbyType());}
    }

    public Optional<MatchDTO> fetch(Long matchId) throws InterruptedException {
        for (int attempt = 1; attempt <= 2; attempt++) {

            Optional<MatchResponse> matchResponse = apiService.getMatch(matchId);

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


    public boolean isValidMatch(MatchDTO dto) {
        return dto.lobbyType() == LobbyTypeEnum.RANKED && dto.bracket() >= 8;
    }
}
