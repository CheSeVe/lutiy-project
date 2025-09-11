package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemsInitService {
    @Autowired
    ItemRepository repository;

    @Autowired
    StratzApiService stratzApiService;

    @Autowired
    ItemMapper mapper;

    private static final Logger log = LoggerFactory.getLogger(ItemsInitService.class);

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
