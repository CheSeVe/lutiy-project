package ru.CheSeVe.lutiy_project.controller;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.CheSeVe.lutiy_project.dto.api.MatchDTO;
import ru.CheSeVe.lutiy_project.entity.Match;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.MatchRepository;
import ru.CheSeVe.lutiy_project.service.mapper.MatchMapper;

@RestController
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MatchController {
    MatchMapper mapper;
    MatchRepository repository;
    public final static String CREATE_MATCH = "match/create";
    public final static String UPDATE_MATCH = "match/update";
    public final static String DELETE_MATCH = "match/delete";
    public final static String GET_MATCH = "match/get";

    @PostMapping(CREATE_MATCH)
    public void saveMatch(@RequestBody MatchDTO matchDTO) {
        Match match = mapper.map(matchDTO);
        repository.save(match);
    }

    @DeleteMapping(DELETE_MATCH)
    public void DeleteMatch(@RequestParam Long matchId) {
        repository.findById(matchId)
                .orElseThrow(() -> new NotFoundException(String.format("match with id %d not found", matchId)));
        repository.deleteById(matchId);
    }


}
