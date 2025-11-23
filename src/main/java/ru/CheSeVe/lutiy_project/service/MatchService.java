package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.CheSeVe.lutiy_project.dto.api.MatchDTO;
import ru.CheSeVe.lutiy_project.exception.BadRequestException;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.MatchRepository;
import ru.CheSeVe.lutiy_project.service.mapper.MatchMapper;

@Transactional
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MatchService {

    MatchRepository repository;

    MatchMapper mapper;

    public MatchService(MatchRepository repository, MatchMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public void saveMatch(MatchDTO dto) {
        if (dto == null) {
            throw new BadRequestException("MatchDTO can't be null");
        }
        repository.save(mapper.map(dto));
    }

    public void deleteMatch(Long matchId) {
        repository.findById(matchId)
                .orElseThrow(() -> new NotFoundException(String.format("match with id %d not found", matchId)));
        repository.deleteById(matchId);
    }
}
