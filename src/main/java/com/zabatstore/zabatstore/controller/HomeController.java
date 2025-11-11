package com.zabatstore.zabatstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

@GetMapping({"/", "/home", "/recetas"})
public String home(Model model) {
    model.addAttribute("titulo", "Zabat Store");
    return "home";
}
}
