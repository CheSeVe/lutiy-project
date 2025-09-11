package ru.CheSeVe.lutiy_project.service;

import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.dto.api.OneItemStatsDTO;
import ru.CheSeVe.lutiy_project.repository.MatchRepository;


@Service
public class StatsForOneHeroService {

    private final MatchRepository matchRepository;

    public StatsForOneHeroService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public OneItemStatsDTO itemWRWithTheseHeroes(Short mainHeroId, Short enemyHeroId, Short itemId) {

        return new OneItemStatsDTO(matchRepository.countMatchesWithEnemies(mainHeroId, enemyHeroId),
                matchRepository.countWinsWithItem(mainHeroId, enemyHeroId, itemId),
                matchRepository.countWinsWithoutItem(mainHeroId, enemyHeroId, itemId));

    }

}
