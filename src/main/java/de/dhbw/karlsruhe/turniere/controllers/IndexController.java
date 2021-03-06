package de.dhbw.karlsruhe.turniere.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping("/")
    String index() {
        return "index";
    }

    @RequestMapping("/impressum")
    String impressum() {
        return "impressum";
    }

    @RequestMapping("/privacy")
    String privacy() {
        return "privacy";
    }
}
