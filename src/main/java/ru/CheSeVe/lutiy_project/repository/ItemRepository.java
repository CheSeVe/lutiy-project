package ru.CheSeVe.lutiy_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.CheSeVe.lutiy_project.entity.Item;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Short> {

    Optional<Item> findByDisplayName(String displayName);

    boolean existsByImgUrlIsNotNull();
}
