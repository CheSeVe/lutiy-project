package ru.CheSeVe.lutiy_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.CheSeVe.lutiy_project.entity.Hero;

import java.util.List;
import java.util.Optional;

public interface HeroRepository extends JpaRepository<Hero, Short> {
    Optional<Hero> findByDisplayName(String displayName);

    boolean existsByImgUrlIsNotNull();

    @Query("""
            SELECT h.id FROM Hero h WHERE h.id IN (:mainHeroId, :enemyHeroId)
            """)
    List<Short> findExistingHeroIds(@Param("mainHeroId") Short mainHeroId,
                            @Param("enemyHeroId") Short enemyHeroId);

}
