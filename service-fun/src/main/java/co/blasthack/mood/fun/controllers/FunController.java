package co.blasthack.mood.fun.controllers;

import co.blasthack.mood.fun.integration.BaseFunSource;
import co.blasthack.mood.fun.integration.GipfyFunSource;
import co.blasthack.mood.fun.integration.RedditFunSource;
import co.blasthack.mood.fun.model.FunMessage;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
class FunController {

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    List<FunMessage> getUpliftingFunMessages() {
        List<FunMessage> messages = new ArrayList<>();

        BaseFunSource redditFunSource = new RedditFunSource();
        messages.addAll(redditFunSource.getFunMessages());

        BaseFunSource gipfyFunSource = new GipfyFunSource();
        messages.addAll(gipfyFunSource.getFunMessages());

        return messages;
    }
}
