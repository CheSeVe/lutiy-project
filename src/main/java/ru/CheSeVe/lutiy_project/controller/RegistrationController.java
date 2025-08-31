package ru.CheSeVe.lutiy_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.CheSeVe.lutiy_project.entity.User;
import ru.CheSeVe.lutiy_project.repository.UserRepository;

import java.util.Optional;

@Controller
public class RegistrationController {
public static final String PERFORM_REGISTRATION = "/register";

@Autowired
UserRepository userRepository;

@Autowired
PasswordEncoder passwordEncoder;


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
