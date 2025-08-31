package ru.CheSeVe.lutiy_project.dto.api;

import java.util.List;
import java.util.Map;

public record ErrorDTO(String message,
                       List<Location> locations,
                       Map<String, Object> extensions) {
    public record Location(int line, int column) {}
}
