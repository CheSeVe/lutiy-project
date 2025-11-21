package ru.CheSeVe.lutiy_project.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.CheSeVe.lutiy_project.entity.User;
import ru.CheSeVe.lutiy_project.repository.UserRepository;

import java.util.Optional;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationController {
public static final String PERFORM_REGISTRATION = "/register";

UserRepository userRepository;

PasswordEncoder passwordEncoder;

RegistrationController(UserRepository repository, PasswordEncoder encoder) {
    userRepository = repository;
    passwordEncoder = encoder;
}


@PostMapping(PERFORM_REGISTRATION)
public String performRegistration(@RequestParam Long steamAccountId,
                                  @RequestParam String username,
                                  @RequestParam String password,
                                  @RequestParam(required = false) Optional<String> rank) {
    if (userRepository.findByUsername(username).isPresent() || userRepository.findById(steamAccountId).isPresent()) {
        return "redirect:/registration?error"; // нужна отдельная ошибка на id и username
    }

    User user = new User(steamAccountId, username, passwordEncoder.encode(password));
    rank.ifPresent(user::setRank);
    userRepository.saveAndFlush(user);
        return  "redirect:/registration?success";
}


}
