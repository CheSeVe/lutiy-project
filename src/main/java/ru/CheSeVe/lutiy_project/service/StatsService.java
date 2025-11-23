package ru.CheSeVe.lutiy_project.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.dto.HeroDTO;
import ru.CheSeVe.lutiy_project.dto.ItemStatsDTO;
import ru.CheSeVe.lutiy_project.dto.MatchupStatsDTO;
import ru.CheSeVe.lutiy_project.entity.Hero;
import ru.CheSeVe.lutiy_project.exception.BadRequestException;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.HeroRepository;
import ru.CheSeVe.lutiy_project.repository.MatchRepository;
import ru.CheSeVe.lutiy_project.repository.projection.TotalMatchesProjection;
import ru.CheSeVe.lutiy_project.repository.projection.TotalMatchesWithItemInMatchupProjection;
import ru.CheSeVe.lutiy_project.repository.projection.TotalMatchesWithItemProjection;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatsService {

    private final MatchRepository matchRepository;

    private final HeroRepository heroRepository;

    private static final double SMOOTHING_COEF = 7.0;

    private static final int MINIMAL_ITEM_COUNT = 4;


    public StatsService(MatchRepository matchRepository, HeroRepository heroRepository) {
        this.matchRepository = matchRepository;
        this.heroRepository = heroRepository;
    }

    public MatchupStatsDTO getMatchUpStats(Short mainHeroId, Short enemyHeroId) {

        if (mainHeroId == null || enemyHeroId == null) {
            throw new IllegalArgumentException("Invalid hero parameters");
        }

        if (Objects.equals(mainHeroId, enemyHeroId)) {
            throw new BadRequestException("Main hero id and enemy hero id can't be the same");
        }

        List<Short> existingIds = heroRepository.findExistingHeroIds(mainHeroId, enemyHeroId);

        if (!existingIds.contains(mainHeroId)) {
            throw new NotFoundException(String.format("Hero with id %d does not exist", mainHeroId));
        }

        if (!existingIds.contains(enemyHeroId)) {
            throw new NotFoundException(String.format("Hero with id %d does not exist", enemyHeroId));
        }

        List<TotalMatchesWithItemProjection> totalMatchWithItemProj = matchRepository.getMatchesAndWinsWithItem(mainHeroId);

        Map<Short, TotalMatchesWithItemProjection> totalMatchWithItemMap = totalMatchWithItemProj.stream()
                .collect(Collectors.toMap(
                        TotalMatchesWithItemProjection::getItemId,
                        Function.identity()
                ));

        TotalMatchesProjection totalMatchProj = matchRepository.getTotalMatchesAndWins(mainHeroId, enemyHeroId);
        List<TotalMatchesWithItemInMatchupProjection> itemStatsInMatchupProjList = matchRepository.getMatchesAndWinsInMatchupWithItem(mainHeroId, enemyHeroId);

        List<ItemStatsDTO> itemStats = itemStatsInMatchupProjList.stream()
                .filter(itemStatsProj -> itemStatsProj.getMatchesWithItem() >= MINIMAL_ITEM_COUNT)
                .map(projectionInMatchup -> {
                    Short itemId = projectionInMatchup.getItemId();
                    String imgUrl = projectionInMatchup.getImgUrl();
                    Long matchupMatchesWithItem = projectionInMatchup.getMatchesWithItem();
                    Long matchupWinsWithItem = projectionInMatchup.getWinsWithItem();

                    TotalMatchesWithItemProjection totalMatchesWithItemProj = totalMatchWithItemMap.get(itemId);

                    if (totalMatchesWithItemProj == null) {
                        log.warn("no such itemId={} in matchUp mainHeroId={}, enemyHeroId={} for some reason", itemId, mainHeroId, enemyHeroId);
                    }

                    long heroATotalMatchesWithItem = totalMatchesWithItemProj != null ? totalMatchesWithItemProj.getTotalMatchesWithItem()
                            : 0L;

                    long heroATotalWinsWithItem = totalMatchesWithItemProj != null ? totalMatchesWithItemProj.getTotalWinsWithItem()
                            : 0L;

                    double globalWinrateWithItem = heroATotalMatchesWithItem != 0 ? (double)heroATotalWinsWithItem / heroATotalMatchesWithItem
                            : 0.0;

                    double roughWinrate = matchupMatchesWithItem != 0 ? (double)matchupWinsWithItem / matchupMatchesWithItem
                            : 0.0;

                    double weightedWinrate = (matchupWinsWithItem + SMOOTHING_COEF*globalWinrateWithItem)
                            / (matchupMatchesWithItem + SMOOTHING_COEF);

                    double impact = weightedWinrate - globalWinrateWithItem;

                    return new ItemStatsDTO(itemId,
                            matchupMatchesWithItem,
                            roughWinrate*100,
                            weightedWinrate*100,
                            impact*100,
                            imgUrl);
                }).sorted(Comparator.comparing(ItemStatsDTO::impact).reversed())
                    .collect(Collectors.toList());

        long totalMatches = totalMatchProj.getTotalMatches();

        double totalWinrate = totalMatches != 0 ? (double)totalMatchProj.getTotalWins() / totalMatches
                : 0.0;

        return new MatchupStatsDTO(totalMatches, totalWinrate*100, itemStats);
    }

    public List<HeroDTO> getAndSortHeroes() {
        return heroRepository.findAll().stream().
                sorted(Comparator.comparing(Hero::getDisplayName))
                .map(hero -> new HeroDTO(hero.getId(), hero.getDisplayName(), hero.getImgUrl()))
                .collect(Collectors.toList());
    }

}
