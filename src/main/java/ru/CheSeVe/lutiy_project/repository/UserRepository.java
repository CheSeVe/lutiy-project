package ru.CheSeVe.lutiy_project.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.CheSeVe.lutiy_project.entity.User;


import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    @Query("select u from User u")
    Stream<User> findAllByUsername(String username);
    Optional<User> findByUsername(String username);
}
