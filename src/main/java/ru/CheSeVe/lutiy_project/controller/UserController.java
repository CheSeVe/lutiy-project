package ru.CheSeVe.lutiy_project.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.CheSeVe.lutiy_project.dto.UserDto;
import ru.CheSeVe.lutiy_project.dto.UserIdDTO;
import ru.CheSeVe.lutiy_project.entity.User;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.factory.UserDtoFactory;
import ru.CheSeVe.lutiy_project.repository.UserRepository;
import ru.CheSeVe.lutiy_project.service.UserService;

import java.util.Optional;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {
    public final static String DELETE_USER = "user/delete";
    public final static String GET_USER = "user/get";
    public final static String PUT_USER = "user/put";

    final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PutMapping(PUT_USER)
        public UserDto updateUser(
            @RequestParam(value = "user_id", required = false) Long steamAccountId,
            @RequestParam(value = "user_name", required = false) Optional<String> optionalUsername,
            @RequestParam(value = "password", required = false) Optional<String> optionalPassword,
            @RequestParam(value = "rank", required = false) Optional<String> optionalRank) {

        return service.updateUser(steamAccountId,
                optionalUsername,
                optionalPassword,
                optionalRank);
    }

    @DeleteMapping(DELETE_USER)
    public void deleteUser(@RequestParam(value = "id") Long userId) {
        service.deleteUser(userId);
    }

    @GetMapping(GET_USER)
    public UserDto getUser(@RequestParam("id") Long userId) {
        return service.getUser(userId);
    }

    @GetMapping("/me")
    public UserIdDTO getCurrentUserId(@AuthenticationPrincipal User user) {
        return new UserIdDTO(user.getSteamAccountId());
    }

}
