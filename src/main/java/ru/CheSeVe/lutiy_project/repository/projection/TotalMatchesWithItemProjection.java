package ru.CheSeVe.lutiy_project.repository.projection;

public interface TotalMatchesWithItemProjection {
    Short getItemId();
    Long getTotalMatchesWithItem();
    Long getTotalWinsWithItem();
}
