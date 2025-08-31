package ru.CheSeVe.lutiy_project.controller;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.CheSeVe.lutiy_project.dto.UserDto;
import ru.CheSeVe.lutiy_project.dto.UserIdDTO;
import ru.CheSeVe.lutiy_project.entity.User;
import ru.CheSeVe.lutiy_project.exception.AlreadyExistException;
import ru.CheSeVe.lutiy_project.exception.BadRequestException;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.factory.UserDtoFactory;
import ru.CheSeVe.lutiy_project.repository.UserRepository;
import ru.CheSeVe.lutiy_project.service.Helper;

import java.util.Optional;

@RestController
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserController {
    public final static String DELETE_USER = "user/delete";
    public final static String GET_USER = "user/get";
    public final static String CREATE_OR_UPDATE_USER = "user/put";
    public final static String PATCH_USER = "user/patch/{user_id}";

    @Autowired
    UserDtoFactory userDtoFactory;

    @Autowired
    UserRepository userRepository;

    @Autowired
    Helper helper;

    @PutMapping(CREATE_OR_UPDATE_USER)
        public UserDto createOrUpdateUser(
            @RequestParam(value = "user_id", required = false) Optional<Long> optionalUserId,
            @RequestParam(value = "user_name", required = false) Optional<String> optionalUsername,
            @RequestParam(value = "password", required = false) Optional<String> optionalPassword,
            @RequestParam(value = "rank", required = false) Optional<String> optionalRank
    ){
        optionalUsername = optionalUsername.filter(username -> !username.trim().isEmpty());

        boolean isCreate = optionalUserId.isEmpty();

        if (isCreate) {
            if (optionalUsername.isEmpty())
                throw new BadRequestException("User name can't be empty");
            if (optionalPassword.isEmpty())
                throw new BadRequestException("Password can't be empty");
        }

        final User user = optionalUserId.isEmpty() ?
                User.builder()
                        .username("")
                        .password("")
                        .build() : userRepository.findById(optionalUserId.get())
                .orElseThrow(() -> new NotFoundException(String
                        .format("User with id \"%d\" doesn't exist", optionalUserId.get()))
        );

        optionalUsername.ifPresent(username -> {
            userRepository.findByUsername(username).filter(anotherUser -> {
                throw new AlreadyExistException(String.format("User name \"%s\" already exists", username));
            });
            user.setUsername(username);
        });

        optionalPassword.ifPresent(user::setPassword);

        optionalRank.ifPresent(user::setRank);

        userRepository.saveAndFlush(user);
        return userDtoFactory.createUserDto(user);
    }

    @DeleteMapping(DELETE_USER)
    public void deleteUser(@RequestParam(value = "id") Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user \"%d\" not found", userId)));
        userRepository.deleteById(userId);
    }

    @GetMapping(GET_USER)
    public UserDto getUser(@RequestParam("id") Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user \"%d\" not found", userId)));
        return userDtoFactory.createUserDto(user);
    }

    @PatchMapping(PATCH_USER)
    public UserDto patchUser(@PathVariable(name = "user_id") Long userId,
                             @RequestParam(name = "user_name", required = false) Optional<String> optionalUsername,
                             @RequestParam(name = "password", required = false) Optional<String> optionalPassword,
                             @RequestParam(name = "rank", required = false) Optional<String> optionalRank) {

        if (optionalUsername.isPresent() && optionalUsername.get().trim().isEmpty()) {
            throw new BadRequestException("user name can't be empty");
        }

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("user with id \"%d\" doesn't exist", userId)));

        optionalUsername.ifPresent(username -> {
            userRepository.findByUsername(username).filter(anotherUser -> {
                throw new BadRequestException("user name  \"%s\" already exist");
            });
            user.setUsername(username);
        });

        optionalPassword.ifPresent(user::setPassword);

        optionalRank.ifPresent(user::setRank);

        userRepository.saveAndFlush(user);

        return userDtoFactory.createUserDto(user);
    }

    @GetMapping("api/me")
    public UserIdDTO getCurrentUserId(@AuthenticationPrincipal User user) {
        return new UserIdDTO(user.getSteamAccountId());
    }

}
