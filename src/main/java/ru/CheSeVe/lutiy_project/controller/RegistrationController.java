package ru.CheSeVe.lutiy_project.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.CheSeVe.lutiy_project.exception.AlreadyExistException;
import ru.CheSeVe.lutiy_project.exception.BadRequestException;
import ru.CheSeVe.lutiy_project.service.RegistrationService;

import java.util.Optional;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegistrationController {
    public static final String PERFORM_REGISTRATION = "/register";

    RegistrationService service;

    RegistrationController(RegistrationService service) {
        this.service = service;
    }


    @PostMapping(PERFORM_REGISTRATION)
    public String performRegistration(@RequestParam Long steamAccountId,
                                      @RequestParam String username,
                                      @RequestParam String password,
                                      @RequestParam(required = false) Optional<String> rank) {
        try {
            service.registerAndSaveUser(steamAccountId, username, password, rank);
            return "redirect:/registration?success";
        } catch (BadRequestException e) {
            return "redirect:/registration?error=" + e.getMessage();
        } catch (AlreadyExistException e) {
            if (e.getMessage().contains("Username")) {
                return "redirect:/registration?username_error";
            } else {
                return "redirect:/registration?steam_account_id_error";
            }
        }
    }
}
