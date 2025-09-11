package ru.CheSeVe.lutiy_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    public final static String LOGIN_PAGE = "/login";
    public final static String INDEX = "/";
    public final static String REGISTRATION = "/registration";


    @GetMapping(LOGIN_PAGE)
    public static String loginPage() {
        return "login-page.html";
    }

    @GetMapping(INDEX)
    public static String helloPage() {
        return "index.html";
    }

    @GetMapping(REGISTRATION)
    public static String registrationPage() {
        return "registration-page.html";
    }


}
