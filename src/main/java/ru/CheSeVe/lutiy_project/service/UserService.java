package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.CheSeVe.lutiy_project.dto.UserDto;
import ru.CheSeVe.lutiy_project.dto.UserIdDTO;
import ru.CheSeVe.lutiy_project.entity.User;
import ru.CheSeVe.lutiy_project.exception.AlreadyExistException;
import ru.CheSeVe.lutiy_project.exception.BadRequestException;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.factory.UserDtoFactory;
import ru.CheSeVe.lutiy_project.repository.UserRepository;

import java.util.Optional;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;

    UserDtoFactory userDtoFactory;

    public UserService(UserRepository repository, UserDtoFactory factory) {
        this.userRepository = repository;
        this.userDtoFactory = factory;
    }

    public UserDto updateUser(Long steamAccountId,
                              Optional<String> optionalUsername,
                              Optional<String> optionalPassword,
                              Optional<String> optionalRank) {

        if (steamAccountId == null) {
            throw new BadRequestException("SteamAccountId can't be null");
        }

        optionalUsername = optionalUsername.filter(username -> !username.trim().isEmpty());

        final User user = userRepository.findById(steamAccountId)
                .orElseThrow(() -> new NotFoundException(String
                        .format("User with id \"%d\" doesn't exist", steamAccountId)));

        optionalUsername.ifPresent(username -> {
            userRepository.findByUsername(username).ifPresent(anotherUser -> {
                throw new AlreadyExistException(String.format("User name \"%s\" already exists", username));
            });
            user.setUsername(username);
        });

        optionalPassword.ifPresent(user::setPassword);
        optionalRank.ifPresent(user::setRank);

        userRepository.saveAndFlush(user);
        return userDtoFactory.createUserDto(user);
    }

    public void deleteUser(Long steamAccountId) {
        if (steamAccountId == null) {
            throw new BadRequestException("SteamAccountId can't be null");
        }

        userRepository.findById(steamAccountId)
                .orElseThrow(() -> new NotFoundException(String.format("user \"%d\" not found", steamAccountId)));
        userRepository.deleteById(steamAccountId);
    }

    public UserDto getUser(Long steamAccountId) {
        if (steamAccountId == null) {
            throw new BadRequestException("SteamAccountId can't be null");
        }
        User user = userRepository.findById(steamAccountId)
                .orElseThrow(() -> new NotFoundException(String.format("user \"%d\" not found", steamAccountId)));
        return userDtoFactory.createUserDto(user);
    }

}
