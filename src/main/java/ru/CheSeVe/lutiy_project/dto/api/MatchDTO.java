package ru.CheSeVe.lutiy_project.dto.api;

import ru.CheSeVe.lutiy_project.enums.LobbyTypeEnum;

import java.util.List;

public record MatchDTO(Long id, LobbyTypeEnum lobbyType, Byte bracket, Long startDateTime, List<PlayerDTOForMatch> players) {
}
