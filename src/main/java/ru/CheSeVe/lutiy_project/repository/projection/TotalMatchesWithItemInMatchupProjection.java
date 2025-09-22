package ru.CheSeVe.lutiy_project.repository.projection;

public interface TotalMatchesWithItemInMatchupProjection {
    Short getItemId();
    Long getMatchesWithItem();
    Long getWinsWithItem();
    String getImgUrl();
}
