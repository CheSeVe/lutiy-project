package ru.CheSeVe.lutiy_project.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.CheSeVe.lutiy_project.entity.User;
import ru.CheSeVe.lutiy_project.exception.AlreadyExistException;
import ru.CheSeVe.lutiy_project.exception.BadRequestException;
import ru.CheSeVe.lutiy_project.repository.UserRepository;

import java.util.Optional;

@Transactional
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationService {

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository repository, PasswordEncoder encoder) {
        this.userRepository = repository;
        this.passwordEncoder = encoder;
    }

    public void registerAndSaveUser(Long steamAccountId,
                                    String username,
                                    String password,
                                    Optional<String> rank) {

        if (steamAccountId == null || username == null || password == null || username.trim().isEmpty()) {
            throw new BadRequestException("Incorrect data");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new AlreadyExistException("Username already taken");
        }

        if (userRepository.findById(steamAccountId).isPresent()) {
            throw new AlreadyExistException("SteamAccountId already registered");
        }

        User user = new User(steamAccountId, username, passwordEncoder.encode(password));
        rank.ifPresent(user::setRank);
        userRepository.saveAndFlush(user);
    }
}
