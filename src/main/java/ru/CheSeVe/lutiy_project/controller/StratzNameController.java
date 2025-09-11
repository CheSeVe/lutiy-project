package ru.CheSeVe.lutiy_project.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.CheSeVe.lutiy_project.dto.api.*;
import ru.CheSeVe.lutiy_project.service.StratzApiService;

import java.util.List;
import java.util.stream.Stream;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/stratz")
public class StratzNameController {

    StratzApiService stratzApiService;

    @GetMapping("/players/{steamId}/name")
    public ResponseEntity<String> getName(@PathVariable("steamId") Long steamAccountId) {
        return stratzApiService.getPlayerName(steamAccountId)
                .map(NameResponse::data)
                .map(DataDTO::player)
                .flatMap(player -> Stream.ofNullable(player.names())
                        .flatMap(List::stream)
                        .map(NameDTO::name)
                        .findFirst())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Name not found"));
    }
}
