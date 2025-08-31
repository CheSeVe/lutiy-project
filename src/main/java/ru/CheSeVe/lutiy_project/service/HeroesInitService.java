package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.dto.api.ConstantHeroesDTO;
import ru.CheSeVe.lutiy_project.dto.api.DataDTOForHeroes;
import ru.CheSeVe.lutiy_project.dto.api.HeroDTO;
import ru.CheSeVe.lutiy_project.dto.api.HeroesResponse;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.HeroRepository;
import ru.CheSeVe.lutiy_project.service.mapper.HeroMapper;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HeroesInitService {
    @Autowired
    ApiService apiService;

    @Autowired
    HeroRepository repository;

    @Autowired
    HeroMapper mapper;

    private static final Logger log = LoggerFactory.getLogger(HeroesInitService.class);

    public void getAndSaveHeroes() {
        log.info("getting heroes for DB");

        Optional<HeroesResponse> response = apiService.getHeroes();

        List<HeroDTO> heroes = response.map(HeroesResponse::data)
                .map(DataDTOForHeroes::constants)
                .map(ConstantHeroesDTO::heroes)
                .orElseThrow(() -> new NotFoundException("no heroes in api response"));

        repository.saveAll(heroes.stream().map(mapper::map).toList());
    }
}
