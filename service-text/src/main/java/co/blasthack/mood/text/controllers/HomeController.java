package co.blasthack.mood.text.controllers;

import co.blasthack.mood.text.model.MoodTextMessage;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
class HomeController {

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public @ResponseBody
    String test() {
        return "This is test response.";
    }

    @RequestMapping(path = "/mood", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    String rateMoodOfText(@RequestBody MoodTextMessage data) {
        return "Mood Rated";
    }
}
