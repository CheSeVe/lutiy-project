package ru.CheSeVe.lutiy_project.dto;

public record ItemStatsDTO(Short itemId,
                           Long totalMatches,
                           Double roughWinrate,
                           Double weightedWinrate,
                           Double impact,
                           String imgUrl) {
}
