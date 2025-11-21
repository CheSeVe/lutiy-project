package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class HeroesInitService {
    StratzApiService stratzApiService;

    HeroRepository repository;

    HeroMapper mapper;

    public HeroesInitService(StratzApiService stratzApiService, HeroRepository repository, HeroMapper mapper) {
        this.stratzApiService = stratzApiService;
        this.repository = repository;
        this.mapper = mapper;
    }

    public void getAndSaveHeroes() {
        log.info("getting heroes for DB");

        Optional<HeroesResponse> response = stratzApiService.getHeroes();

        List<HeroDTO> heroes = response.map(HeroesResponse::data)
                .map(DataDTOForHeroes::constants)
                .map(ConstantHeroesDTO::heroes)
                .orElseThrow(() -> new NotFoundException("no heroes in api response"));

        repository.saveAll(heroes.stream().map(mapper::map).toList());
    }
}
