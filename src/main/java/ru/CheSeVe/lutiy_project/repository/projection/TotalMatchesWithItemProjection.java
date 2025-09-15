package ru.CheSeVe.lutiy_project.repository.projection;

public interface TotalMatchesWithItemProjection {
    Short getItemId();
    Long getMatchesWithItem();
    Long getWinsWithItem();
    String getImgUrl();
}
