package co.blasthack.mood.audio.controllers;

import co.blasthack.mood.audio.integration.GoogleSpeechToText;
import co.blasthack.mood.audio.model.MoodTextMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/")
class AudioController {

    private final RestTemplate restTemplate;

    private final GoogleSpeechToText googleSpeechToText;

    public AudioController(RestTemplate restTemplate, GoogleSpeechToText googleSpeechToText) {
        this.restTemplate = restTemplate;
        this.googleSpeechToText = googleSpeechToText;
    }

    /*
        Test method to check connection with Google Cloud Speech to Text API using sample wav data
     */
    @RequestMapping("/test")
    public String test() throws IOException {
        File audioSample = loadAudioSample();
        byte[] data = Files.readAllBytes(audioSample.toPath());
        String audioResponse = googleSpeechToText.audioTranscription(data);
        return "Text: " + audioResponse;
    }

    @RequestMapping(path = "/mood", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    Float rateMoodOfText(@RequestBody byte[] audioData) {
        String transcribedAudio = googleSpeechToText.audioTranscription(audioData);
        MoodTextMessage textMessage = new MoodTextMessage(transcribedAudio);

        ResponseEntity<Float> result = restTemplate.postForEntity("http://witai-service/sentiment", textMessage, Float.class);
        return result.getBody();
    }

    private File loadAudioSample() throws FileNotFoundException {
        return ResourceUtils.getFile("classpath:sample.wav");
    }
}
