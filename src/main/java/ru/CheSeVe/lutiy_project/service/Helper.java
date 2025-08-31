package ru.CheSeVe.lutiy_project.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.CheSeVe.lutiy_project.entity.User;
import ru.CheSeVe.lutiy_project.exception.NotFoundException;
import ru.CheSeVe.lutiy_project.repository.UserRepository;

@Component
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class Helper {
    @Autowired
    UserRepository userRepository;

    public User getUserOrThrowException(Long userId) {
        return userRepository.findById(userId).orElseThrow(()->new NotFoundException(String.format("user with id \"%d\" not found", userId)));
    }

    public User getUserByUsernameOrException(String username) {
        return userRepository.findAllByUsername(username).findAny().orElseThrow(()->new NotFoundException(String.format(("user \"%s\" not found"), username)));
    }

}
