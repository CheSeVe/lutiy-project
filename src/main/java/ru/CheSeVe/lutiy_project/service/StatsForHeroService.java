package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.repository.MatchRepository;

import java.util.HashMap;
import java.util.Map;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsForHeroService {
    @Autowired
    MatchRepository matchRepository;

    public Map<String, Long> itemWRWithTheseHeroes(Short heroId0, Short heroId1, Short itemId) {

        Map<String, Long> data = new HashMap<>();

        data.put("matchCount", matchRepository.countMatchesWithEnemies(heroId0, heroId1));
        data.put("winCountWithItem", matchRepository.countWinsWithItem(heroId0, heroId1, itemId));
        data.put("winCountWithoutItem", matchRepository.countWinsWithoutItem(heroId0, heroId1, itemId));

        return data;
    }

}
