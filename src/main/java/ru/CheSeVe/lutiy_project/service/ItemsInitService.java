package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.CheSeVe.lutiy_project.dto.api.ConstantItemsDTO;
import ru.CheSeVe.lutiy_project.dto.api.DataDTOForItems;
import ru.CheSeVe.lutiy_project.dto.api.ItemDTO;
import ru.CheSeVe.lutiy_project.dto.api.ItemsResponse;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.ItemRepository;
import ru.CheSeVe.lutiy_project.service.mapper.ItemMapper;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ItemsInitService {
    ItemRepository repository;

    StratzApiService stratzApiService;

    ItemMapper mapper;

    ItemsInitService(ItemRepository repository, StratzApiService service, ItemMapper mapper) {
        this.repository = repository;
        this.stratzApiService = service;
        this.mapper = mapper;
    }

    public void getAndSaveItems() {
        log.info("getting items for DB");

        Optional<ItemsResponse> response = stratzApiService.getItems();

        List<ItemDTO> items = response.map(ItemsResponse::data)
                .map(DataDTOForItems::constants)
                .map(ConstantItemsDTO::items)
                .orElseThrow(() -> new NotFoundException("no items in api response"));

        repository.saveAll(items.stream().map(mapper::map).toList());
    }
}
