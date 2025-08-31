package ru.CheSeVe.lutiy_project.service.mapper;

import org.springframework.stereotype.Component;
import ru.CheSeVe.lutiy_project.dto.api.HeroDTO;
import ru.CheSeVe.lutiy_project.entity.Hero;
import ru.CheSeVe.lutiy_project.exception.BadRequestException;

@Component
public class HeroMapper {
    public String getRidOfNullBytes(String text) {
        return text != null ? text.replace("\0", "") : null;
    }

    public Hero map(HeroDTO dto) {
        if (dto == null) throw new BadRequestException("heroDTO is null");

        return new Hero(dto.id(),
                getRidOfNullBytes(dto.name()),
                getRidOfNullBytes(dto.displayName()),
                null);
    }
}
