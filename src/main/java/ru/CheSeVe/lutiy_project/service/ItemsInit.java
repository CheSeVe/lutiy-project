package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.ItemRepository;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class ItemsInit {

    boolean hasItems = false;

    final ItemsInitService service;

    final ItemRepository repository;

    public ItemsInit(ItemsInitService service, ItemRepository repository) {
        this.service = service;
        this.repository = repository;
    }

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
