package co.blasthack.mood.fun.controllers;

import co.blasthack.mood.fun.integration.BaseFunSource;
import co.blasthack.mood.fun.integration.GipfyFunSource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
class HomeController {

    @RequestMapping(path = "/giphy", method = RequestMethod.GET)
    public @ResponseBody
    List test() {
        BaseFunSource testSource = new GipfyFunSource();
        return testSource.getFunMessages();
    }
}
