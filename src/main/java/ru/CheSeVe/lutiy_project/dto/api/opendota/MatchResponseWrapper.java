package ru.CheSeVe.lutiy_project.dto.api.opendota;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MatchResponseWrapper(List<MatchDTO> rows) {
}
