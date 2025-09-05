package ru.CheSeVe.lutiy_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.CheSeVe.lutiy_project.entity.Hero;

import java.util.Optional;

public interface HeroRepository extends JpaRepository<Hero, Short> {
    Optional<Hero> findByDisplayName(String displayName);

    boolean existsByImgUrlIsNotNull();

}
