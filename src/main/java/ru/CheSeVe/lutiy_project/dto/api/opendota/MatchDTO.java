package ru.CheSeVe.lutiy_project.dto.api.opendota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MatchDTO(Long match_id) {
}
