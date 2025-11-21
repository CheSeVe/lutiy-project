package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.HeroRepository;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class HeroesInit {

    boolean hasHeroes = false;

    final HeroRepository repository;

    final HeroesInitService service;

    HeroesInit(HeroRepository repository, HeroesInitService service) {
        this.repository = repository;
        this.service = service;
    }

    @Scheduled(fixedDelay = 60*1000L)
    public void initIfNeeded() {
        if (hasHeroes) return;

        if (repository.count() == 0) {
            try {
                service.getAndSaveHeroes();
                log.info("got heroes successfully");
                hasHeroes = true;
            } catch (NotFoundException e) {
                log.warn("no heroes in api retrying in 1 min");
            }
        } else hasHeroes = true;
    }
}
