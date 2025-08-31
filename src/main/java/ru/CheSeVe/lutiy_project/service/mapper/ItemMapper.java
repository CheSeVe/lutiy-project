package ru.CheSeVe.lutiy_project.service.mapper;

import org.springframework.stereotype.Component;
import ru.CheSeVe.lutiy_project.dto.api.ItemDTO;
import ru.CheSeVe.lutiy_project.entity.Item;
import ru.CheSeVe.lutiy_project.exception.BadRequestException;

@Component
public class ItemMapper {

    public String getRidOfNullBytes(String text) {
        return text != null ? text.replace("\0", "") : null;
    }

    public Item map(ItemDTO dto) {
        if (dto == null) throw new BadRequestException("itemDTO is null");

        return new Item(dto.id(),
                getRidOfNullBytes(dto.name()),
                getRidOfNullBytes(dto.displayName()),
                null);
    }
}
