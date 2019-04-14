package co.blasthack.mood.text.controllers;

import co.blasthack.mood.text.model.MoodTextMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/")
class TestController {

    private final RestTemplate restTemplate;

    public TestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public @ResponseBody
    String test() {
        return "This is test response from fun-service.";
    }

    @RequestMapping(path = "/mood", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    String rateMoodOfText(@RequestBody MoodTextMessage data) {
        ResponseEntity<String> result = restTemplate.postForEntity("http://text-service/mood", data, String.class);
        return result.getBody();
    }
}
