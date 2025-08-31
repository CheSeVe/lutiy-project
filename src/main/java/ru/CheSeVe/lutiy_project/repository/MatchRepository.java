package ru.CheSeVe.lutiy_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.CheSeVe.lutiy_project.entity.Match;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
}
