package ru.CheSeVe.lutiy_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.CheSeVe.lutiy_project.entity.MatchId;

import java.util.Optional;

public interface MatchIdRepository extends JpaRepository<MatchId, Long> {
    @Query("SELECT MAX(m.matchId) FROM MatchId m")
    Optional<Long> findMaxMatchId();

    @Query("SELECT MIN(m.matchId) FROM MatchId m")
    Optional<Long> findMinMatchId();
}
