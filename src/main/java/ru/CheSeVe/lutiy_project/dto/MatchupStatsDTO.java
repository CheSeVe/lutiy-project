package ru.CheSeVe.lutiy_project.dto;

import java.util.List;

public record MatchupStatsDTO(Long totalMatches,
                              Double matchupWinrate,
                              List<ItemStatsDTO> items) {
}
