package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.dto.api.opendota.MatchDTO;
import ru.CheSeVe.lutiy_project.entity.MatchId;
import ru.CheSeVe.lutiy_project.repository.MatchIdRepository;
import ru.CheSeVe.lutiy_project.repository.MatchRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BetterMatchIdService {

    private static final Logger log = LoggerFactory.getLogger(BetterMatchIdService.class);

    MatchIdRepository matchIdRepository;

    MatchRepository matchRepository;

    OpenDotaApiService apiService;

    public BetterMatchIdService(MatchIdRepository matchIdRepository, MatchRepository matchRepository, OpenDotaApiService apiService) {
        this.matchIdRepository = matchIdRepository;
        this.matchRepository = matchRepository;
        this.apiService = apiService;
    }

    @Scheduled(fixedDelay = 60*1000L)
    public void getAndSaveIds() {
        if (matchIdRepository.count() + matchRepository.count() >= 200_000L) {
            return;
        }

        Optional<List<MatchDTO>> optMatchDTOS = apiService.getMatches();

        if (optMatchDTOS.isEmpty()) {
            log.warn("OpenDota api returned empty result");
            return;
        }

        List<MatchDTO> matchDTOS = optMatchDTOS.get();

        List<Long> matchIds = matchDtoToIdList(matchDTOS);

        List<MatchId> matchIdsEntity = matchIds.stream().map(MatchId::new).collect(Collectors.toList());

        matchIdRepository.saveAll(matchIdsEntity);
        log.info("Got from OpenDota api and saved {} matchIds", matchIdsEntity.size());

    }

    private List<Long> matchDtoToIdList(List<MatchDTO> listDto) {

        return listDto.stream().map(MatchDTO::match_id).collect(Collectors.toList());
    }
}
