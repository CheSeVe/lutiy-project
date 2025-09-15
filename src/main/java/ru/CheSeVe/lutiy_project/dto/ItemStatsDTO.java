package ru.CheSeVe.lutiy_project.dto;

public record ItemStatsDTO(Short itemId,
                           Long matchesWithItem,
                           Double winRateWithItem,
                           Double winRateWithoutItem,
                           Double impact,
                           String imgUrl) {
}
