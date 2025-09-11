package ru.CheSeVe.lutiy_project.service.urls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.entity.Hero;
import ru.CheSeVe.lutiy_project.entity.Item;
import ru.CheSeVe.lutiy_project.repository.HeroRepository;
import ru.CheSeVe.lutiy_project.repository.ItemRepository;

import java.io.File;
import java.net.URL;
import java.util.*;

@Service
public class UrlInDBSetter {

    public static final Logger log = LoggerFactory.getLogger(UrlInDBSetter.class);

    @Autowired
    HeroRepository heroRepository;

    @Autowired
    ItemRepository itemRepository;

    @Scheduled(fixedRate = 60*60*1000L)
    public void setHeroUrls() {

        if (heroRepository.count() == 0) {
            log.error("can't link item urls, no heroes in db");
            return;
        }

        if (heroRepository.existsByImgUrlIsNotNull()) {
            log.info("hero URLs already exist");
            return;
        }

        URL resource = getClass().getClassLoader().getResource("static/hero-icons");

        if (resource == null) {
            log.error("no hero resource folder");
            return;
        }

        File heroFolder = new File(resource.getFile());

        List<Hero> heroes = new ArrayList<>();

        Arrays.stream(Objects.requireNonNull(heroFolder.listFiles())).forEach(file -> {

            String name = file.getName();

            String path = "/hero-icons/" + name;

            int lastChar = name.indexOf("_icon");

            String heroName = name.substring(0, lastChar).replace("_", " ");

            log.info("adding file={}, hero name={}", name, heroName);

            Optional<Hero> optHero = heroRepository.findByDisplayName(heroName);

            optHero.ifPresent(hero -> {
                hero.setImgUrl(path);
                heroes.add(hero);
            });
        });
        heroRepository.saveAll(heroes);
    }

    @Scheduled(fixedRate = 60*60*1000L)
    public void setItemUrls() {

        if (itemRepository.count() == 0) {
            log.error("can't link item urls, no items in db");
            return;
        }

        if (itemRepository.existsByImgUrlIsNotNull()) {
            log.info("item URLs already exist");
            return;
        }

        URL resource = getClass().getClassLoader().getResource("static/item-icons");

        if (resource == null) {
            log.error("no item resource folder");
            return;
        }

        File itemFolder = new File(resource.getFile());

        List<Item> items = new ArrayList<>();

        Arrays.stream(Objects.requireNonNull(itemFolder.listFiles())).forEach(file -> {

            String name = file.getName();

            String path = "/item-icons/" + name;

            int lastChar = name.indexOf(" itemicon");

            String itemName = name.substring(0, lastChar);

            if (itemName.contains("Dagon")
                    || itemName.contains("Diffusal Blade")
                    || itemName.contains("Tranquil Boots (Active)")) {
                log.info("linking item={}", name);

                if (name.contains("Dagon itemicon")) {
                    log.info("LINKING DAGON 1");
                    Optional<Item> optItem = itemRepository.findById((short)104);
                    optItem.ifPresent(item -> {
                        item.setImgUrl(path);
                        items.add(item);
                    });
                    return;
                }

                if (name.contains("Dagon 2")) {
                    log.info("LINKING DAGON 2");
                    Optional<Item> optItem = itemRepository.findById((short)201);
                    optItem.ifPresent(item -> {
                        item.setImgUrl(path);
                        items.add(item);
                    });
                    return;
                }

                if (name.contains("Dagon 3")) {
                    log.info("LINKING DAGON 3");
                    Optional<Item> optItem = itemRepository.findById((short)202);
                    optItem.ifPresent(item -> {
                        item.setImgUrl(path);
                        items.add(item);
                    });
                    return;
                }

                if (name.contains("Dagon 4")) {
                    log.info("LINKING DAGON 4");
                    Optional<Item> optItem = itemRepository.findById((short)203);
                    optItem.ifPresent(item -> {
                        item.setImgUrl(path);
                        items.add(item);
                    });
                    return;
                }

                if (name.contains("Dagon 5")) {
                    log.info("LINKING DAGON 5");
                    Optional<Item> optItem = itemRepository.findById((short)204);
                    optItem.ifPresent(item -> {
                        item.setImgUrl(path);
                        items.add(item);
                    });
                    return;
                }

                if (name.contains("Blade itemicon")) {
                    log.info("LINKING DIFFUSAL BLADE 1");
                    Optional<Item> optItem = itemRepository.findById((short)174);
                    optItem.ifPresent(item -> {
                        item.setImgUrl(path);
                        items.add(item);
                    });
                    return;
                }

                if (name.contains("Diffusal Blade 2")) {
                    log.info("LINKING DIFFUSAL BLADE 2");
                    Optional<Item> optItem = itemRepository.findById((short)196);
                    optItem.ifPresent(item -> {
                        item.setImgUrl(path);
                        items.add(item);
                    });
                    return;
                }

                if (name.contains(("Active"))) {
                    log.info("LINKING TRANQUIL");
                    Optional<Item> optItem = itemRepository.findById((short)214);
                    optItem.ifPresent(item -> {
                        item.setImgUrl(path);
                        items.add(item);
                    });
                    return;
                }
            }

            log.info("adding file={} item name={}", name, itemName);

            Optional<Item> optItem = itemRepository.findByDisplayName(itemName);

            optItem.ifPresent(item -> {
                item.setImgUrl(path);
                items.add(item);
            });
        });
        itemRepository.saveAll(items);
    }
}
