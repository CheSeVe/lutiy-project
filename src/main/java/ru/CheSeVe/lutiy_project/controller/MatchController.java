package ru.CheSeVe.lutiy_project.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.CheSeVe.lutiy_project.dto.api.MatchDTO;
import ru.CheSeVe.lutiy_project.service.MatchService;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MatchController {

    public final static String CREATE_MATCH = "match/create";
    public final static String DELETE_MATCH = "match/delete";

    final MatchService service;

    @PostMapping(CREATE_MATCH)
    public void saveMatch(@RequestBody MatchDTO matchDTO) {
        service.saveMatch(matchDTO);
    }

    @DeleteMapping(DELETE_MATCH)
    public void deleteMatch(@RequestParam Long matchId) {
        service.deleteMatch(matchId);
    }

}
