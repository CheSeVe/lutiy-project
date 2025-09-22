package ru.CheSeVe.lutiy_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.CheSeVe.lutiy_project.entity.Match;
import ru.CheSeVe.lutiy_project.repository.projection.TotalMatchesProjection;
import ru.CheSeVe.lutiy_project.repository.projection.TotalMatchesWithItemInMatchupProjection;
import ru.CheSeVe.lutiy_project.repository.projection.TotalMatchesWithItemProjection;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT COUNT(DISTINCT m) AS totalMatches, " +
            "SUM(CASE WHEN mpA.isVictory = true THEN 1 ELSE 0 END) AS totalWins " +
            "FROM Match m " +
            "JOIN m.matchPlayers mpA " +
            "JOIN m.matchPlayers mpB " +
            "WHERE mpA.heroId = :heroA " +
            "AND mpB.heroId = :heroB " +
            "AND mpA.isVictory <> mpB.isVictory")
    TotalMatchesProjection getTotalMatchesAndWins(@Param("heroA") Short heroA,
                                                  @Param("heroB") Short heroB);

    @Query("""
            SELECT
                it.itemId AS itemId,
                COUNT(DISTINCT m) AS matchesWithItem,
                COUNT(DISTINCT CASE WHEN mpA.isVictory = true THEN m.id ELSE NULL END) AS winsWithItem,
                i.imgUrl AS imgUrl
            From Match m
            JOIN m.matchPlayers mpA
            JOIN m.matchPlayers mpB
            JOIN mpA.items it
            JOIN Item i ON it.itemId = i.id
            WHERE mpA.heroId = :heroA
                AND mpB.heroId = :heroB
                AND mpA.isVictory <> mpB.isVictory
                AND i.name NOT LIKE '%recipe%'
            GROUP BY it.itemId, i.imgUrl
            """)
    List<TotalMatchesWithItemInMatchupProjection> getMatchesAndWinsInMatchupWithItem(@Param("heroA") Short heroA,
                                                                                     @Param("heroB") Short heroB);

    @Query("""
            SELECT
                it.itemId AS itemId,
                COUNT(DISTINCT m) AS totalMatchesWithItem,
                COUNT(DISTINCT CASE WHEN mp.isVictory = true THEN m.id ELSE NULL END) AS totalWinsWithItem
            From Match m
            JOIN m.matchPlayers mp
            JOIN mp.items it
            JOIN Item i ON it.itemId = i.id
            WHERE mp.heroId = :heroId
                AND i.name NOT LIKE '%recipe%'
            GROUP BY it.itemId
            """)
    List<TotalMatchesWithItemProjection> getMatchesAndWinsWithItem(@Param("heroId") Short heroId);

    @Query("SELECT MAX (m.matchId) FROM Match m")
    Optional<Long> findMaxMatchId();
}
