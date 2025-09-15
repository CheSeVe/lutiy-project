package ru.CheSeVe.lutiy_project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.dto.ItemStatsDTO;
import ru.CheSeVe.lutiy_project.dto.MatchupStatsDTO;
import ru.CheSeVe.lutiy_project.repository.MatchRepository;
import ru.CheSeVe.lutiy_project.repository.projection.TotalMatchesProjection;
import ru.CheSeVe.lutiy_project.repository.projection.TotalMatchesWithItemProjection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final MatchRepository repository;

    private static final Logger log = LoggerFactory.getLogger(StatsService.class);


    public StatsService(MatchRepository repository) {
        this.repository = repository;
    }

    public MatchupStatsDTO getMatchUpStats(Short heroA, Short heroB) {

        TotalMatchesProjection totals = repository.getTotalMatchesAndWins(heroA, heroB);

        if (totals == null) {
            log.debug("TotalMatches for heroA={} heroB={} is null", heroA, heroB);
            return new MatchupStatsDTO(0L, List.of());
        }

        List<TotalMatchesWithItemProjection> itemStatsData = repository.getMatchesAndWinsWithItem(heroA, heroB);

        if (itemStatsData.isEmpty()) {
            log.debug("List of itemStats for heroA={} heroB={} is empty", heroA, heroB);
            return new MatchupStatsDTO(totals.getTotalMatches(), List.of());
        }

        List<ItemStatsDTO> itemStats = itemStatsData.stream().map(projection -> {

            Double winRateWithItem = projection.getMatchesWithItem() != 0
                    ? ((double) projection.getWinsWithItem() / projection.getMatchesWithItem())*100
                    : 0.0;

            long winsWithoutItem = totals.getTotalWins() - projection.getWinsWithItem();
            long matchesWithoutItem = totals.getTotalMatches() - projection.getMatchesWithItem();

            Double winRateWithoutItem = totals.getTotalMatches() - projection.getMatchesWithItem() != 0
                    ? ((double) winsWithoutItem / matchesWithoutItem)*100
                    : 0.0;
            return new ItemStatsDTO(
                    projection.getItemId(),
                    projection.getMatchesWithItem(),
                    winRateWithItem,
                    winRateWithoutItem,
                    winRateWithItem - winRateWithoutItem,
                    projection.getImgUrl()
            );
        }).sorted(Comparator.comparing(ItemStatsDTO::impact).reversed()).collect(Collectors.toList());

        return new MatchupStatsDTO(totals.getTotalMatches(), itemStats);
    }

}
