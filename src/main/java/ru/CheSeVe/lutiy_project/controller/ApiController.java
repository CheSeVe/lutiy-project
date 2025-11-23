package ru.CheSeVe.lutiy_project.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.CheSeVe.lutiy_project.dto.HeroDTO;
import ru.CheSeVe.lutiy_project.dto.MatchupStatsDTO;
import ru.CheSeVe.lutiy_project.service.StatsService;

import java.util.List;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    StatsService statsService;

    @GetMapping("/stats")
    public MatchupStatsDTO getStats(@RequestParam Short mainHeroId,
                                    @RequestParam Short enemyHeroId) {
        return statsService.getMatchUpStats(mainHeroId, enemyHeroId);
    }

    @GetMapping("/heroes")
    public List<HeroDTO> getHeroes() {
        return statsService.getAndSortHeroes();
    }

}
