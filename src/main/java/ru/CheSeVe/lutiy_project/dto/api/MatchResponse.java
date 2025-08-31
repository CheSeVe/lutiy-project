package ru.CheSeVe.lutiy_project.dto.api;

import java.util.List;

public record MatchResponse(DataDTOForMatch data,
                            List<ErrorDTO> errors) {
}
