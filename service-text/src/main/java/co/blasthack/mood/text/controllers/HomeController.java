package co.blasthack.mood.text.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
class HomeController {

    @RequestMapping("/test")
    public String test() {
        return "This is test response.";
    }
}
