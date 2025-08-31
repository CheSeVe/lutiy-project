package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.ItemRepository;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemsInit {

    static final Logger log = LoggerFactory.getLogger(ItemsInit.class);

    boolean hasItems = false;

    @Autowired
    ItemsInitService service;

    @Autowired
    ItemRepository repository;

    @Scheduled(fixedDelay = 60*1000L)
    public void initIfNeeded() {
        if (hasItems) return;

        if (repository.count() == 0) {
            try {
                service.getAndSaveItems();
                log.info("got items successfully");
                hasItems = true;
            } catch (NotFoundException e) {
                log.warn("no items from api retrying in 1 min");
            }
        } else { hasItems = true; }
    }
}
