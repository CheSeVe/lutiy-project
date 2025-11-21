package ru.CheSeVe.lutiy_project.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.CheSeVe.lutiy_project.entity.User;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.UserRepository;

@Component
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Helper {
    UserRepository userRepository;

    Helper(UserRepository repository) {
        this.userRepository = repository;
    }

    public User getUserOrThrowException(Long userId) {
        return userRepository.findById(userId).orElseThrow(()->new NotFoundException(String.format("user with id \"%d\" not found", userId)));
    }

    public User getUserByUsernameOrException(String username) {
        return userRepository.findAllByUsername(username).findAny().orElseThrow(()->new NotFoundException(String.format(("user \"%s\" not found"), username)));
    }

}
