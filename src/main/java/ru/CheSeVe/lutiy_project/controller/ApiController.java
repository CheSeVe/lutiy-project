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
import ru.CheSeVe.lutiy_project.entity.Hero;
import ru.CheSeVe.lutiy_project.exception.BadRequestException;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.HeroRepository;
import ru.CheSeVe.lutiy_project.service.StatsService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    StatsService statsService;

    HeroRepository heroRepository;

    @GetMapping("/stats")
    public MatchupStatsDTO getStats(@RequestParam Short mainHeroId,
                                    @RequestParam Short enemyHeroId) {

        if (Objects.equals(mainHeroId, enemyHeroId)) {
            throw new BadRequestException("Main hero id and enemy hero id can't be the same");
        }
        if (!heroRepository.existsById(mainHeroId)) {
            throw new NotFoundException(String.format("Hero with id %d does not exist", mainHeroId));
        }

        if (!heroRepository.existsById(enemyHeroId)) {
            throw new NotFoundException(String.format("Hero with id %d does not exist", enemyHeroId));
        }

        return statsService.getMatchUpStats(mainHeroId, enemyHeroId);
    }

    @GetMapping("/heroes")
    public List<HeroDTO> getHeroes() {
        return heroRepository.findAll().stream().sorted(Comparator.comparing(Hero::getDisplayName))
                .map(hero -> new HeroDTO(hero.getId(), hero.getDisplayName(), hero.getImgUrl()))
                .collect(Collectors.toList());
    }

}
