package ru.CheSeVe.lutiy_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.CheSeVe.lutiy_project.entity.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("SELECT COUNT(DISTINCT m) " +
            "FROM Match m " +
            "JOIN m.matchPlayers mpA " +
            "JOIN m.matchPlayers mpB " +
            "WHERE mpA.heroId = :heroA " +
            "AND mpB.heroId = :heroB " +
            "AND mpA.isVictory <> mpB.isVictory")
    Long countMatchesWithEnemies(@Param("heroA") Short heroA,
                                 @Param("heroB") Short heroB);

    @Query("SELECT COUNT(DISTINCT m) " +
            "FROM Match m " +
            "JOIN m.matchPlayers mpA " +
            "JOIN m.matchPlayers mpB " +
            "JOIN mpA.items it " +
            "WHERE mpA.heroId = :heroA " +
            "AND mpA.isVictory = true " +
            "AND mpB.heroId = :heroB " +
            "AND mpB.isVictory = false " +
            "AND it.itemId = :itemId")
    Long countWinsWithItem(@Param("heroA") Short heroA,
                           @Param("heroB") Short heroB,
                           @Param("itemId") Short itemId);

    @Query("SELECT COUNT(DISTINCT m) " +
            "FROM Match m " +
            "JOIN m.matchPlayers mpA " +
            "JOIN m.matchPlayers mpB " +
            "WHERE mpA.heroId = :heroA " +
            "AND mpA.isVictory = true " +
            "AND mpB.heroId = :heroB " +
            "AND mpB.isVictory = false " +
            "AND NOT EXISTS ( " +
            "SELECT 1 FROM PlayerItem pi " +
            "WHERE pi.matchPlayer = mpA " +
            "AND pi.itemId = :itemId )")
    Long countWinsWithoutItem(@Param("heroA") Short heroA,
                              @Param("heroB") Short heroB,
                              @Param("itemId") Short itemId);
}
