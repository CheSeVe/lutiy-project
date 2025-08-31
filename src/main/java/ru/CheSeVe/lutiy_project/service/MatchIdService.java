package ru.CheSeVe.lutiy_project.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.entity.AppSettings;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.AppSettingsRepository;

@Service
@Transactional
public class MatchIdService {
    @Autowired
    AppSettingsRepository repository;

    public Long getNextMatchId() {
        AppSettings settings = repository.findById("last_match_id")
                .orElseThrow(() -> new NotFoundException("no id in database"));
        Long matchId = Long.parseLong(settings.getValue()) + 1;
        settings.setValue(String.valueOf(matchId));
        repository.save(settings);
        return matchId;
    }
}
