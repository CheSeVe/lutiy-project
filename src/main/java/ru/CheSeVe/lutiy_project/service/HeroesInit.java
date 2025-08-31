package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.HeroRepository;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HeroesInit {

    static final Logger log = LoggerFactory.getLogger(HeroesInit.class);

    boolean hasHeroes = false;

    @Autowired
    HeroRepository repository;

    @Autowired
    HeroesInitService service;

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
