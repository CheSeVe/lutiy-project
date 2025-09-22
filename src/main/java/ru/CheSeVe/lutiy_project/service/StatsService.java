package ru.CheSeVe.lutiy_project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.dto.ItemStatsDTO;
import ru.CheSeVe.lutiy_project.dto.MatchupStatsDTO;
import ru.CheSeVe.lutiy_project.repository.MatchRepository;
import ru.CheSeVe.lutiy_project.repository.projection.TotalMatchesProjection;
import ru.CheSeVe.lutiy_project.repository.projection.TotalMatchesWithItemInMatchupProjection;
import ru.CheSeVe.lutiy_project.repository.projection.TotalMatchesWithItemProjection;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final MatchRepository repository;

    private static final double SMOOTHING_COEF = 7.0;

    private static final int MINIMAL_ITEM_COUNT = 4;


    private static final Logger log = LoggerFactory.getLogger(StatsService.class);

    public StatsService(MatchRepository repository) {
        this.repository = repository;
    }

    public MatchupStatsDTO getMatchUpStats(Short heroA, Short heroB) {

        if (heroA == null || heroB == null) {
            throw new IllegalArgumentException("Invalid hero parameters");
        }

        List<TotalMatchesWithItemProjection> totalMatchWithItemProj = repository.getMatchesAndWinsWithItem(heroA);

        Map<Short, TotalMatchesWithItemProjection> totalMatchWithItemMap = totalMatchWithItemProj.stream()
                .collect(Collectors.toMap(
                        TotalMatchesWithItemProjection::getItemId,
                        Function.identity()
                ));

        TotalMatchesProjection totalMatchProj = repository.getTotalMatchesAndWins(heroA, heroB);
        List<TotalMatchesWithItemInMatchupProjection> itemStatsInMatchupProjList = repository.getMatchesAndWinsInMatchupWithItem(heroA, heroB);

        List<ItemStatsDTO> itemStats = itemStatsInMatchupProjList.stream()
                .filter(itemStatsproj -> itemStatsproj.getMatchesWithItem() >= MINIMAL_ITEM_COUNT)
                .map(projectionInMatchup -> {
                    Short itemId = projectionInMatchup.getItemId();
                    String imgUrl = projectionInMatchup.getImgUrl();
                    Long matchupMatchesWithItem = projectionInMatchup.getMatchesWithItem();
                    Long matchupWinsWithItem = projectionInMatchup.getWinsWithItem();

                    TotalMatchesWithItemProjection totalMatchesWithItemProj = totalMatchWithItemMap.get(itemId);

                    if (totalMatchesWithItemProj == null) {
                        log.warn("no such itemId={} in matchUp heroA={}, heroB={} for some reason", itemId, heroA, heroB);
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

}
