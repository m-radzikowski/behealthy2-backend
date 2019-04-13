package co.blasthack.mood.audio.controllers;

import co.blasthack.mood.audio.model.MoodAudioMessage;
import co.blasthack.mood.audio.model.MoodTextMessage;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
class HomeController {

    private final RestTemplate restTemplate;

    public HomeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /*
        Test method to check connection with Google Cloud Speech to Text API using sample wav data
     */
    @RequestMapping("/test")
    public String test() throws IOException {
        File audioSample = loadAudioSample();
        byte[] data = Files.readAllBytes(audioSample.toPath());
        String audioResponse = audioTranscription(data, "en-EN");

        return "Text: " + audioResponse;
    }

    @RequestMapping(path = "/mood", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    String rateMoodOfText(@RequestBody MoodAudioMessage data) {
        String transcribedAudio = audioTranscription(data.getMessage(), "pl-PL");
        MoodTextMessage textMessage = new MoodTextMessage("text-id", transcribedAudio);

        ResponseEntity<String> result = restTemplate.postForEntity("http://text-service/mood", textMessage, String.class);
        return result.getBody();
    }

    private String audioTranscription(byte[] audioData, String locale) {
        StringBuilder textResponse = new StringBuilder();
        try {

            FileInputStream credentialsStream = new FileInputStream("C:/Java/speech-credentials.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

            SpeechSettings speechSettings = SpeechSettings.newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .build();

            SpeechClient speechClient = SpeechClient.create(speechSettings);

            ByteString audioBytes = ByteString.copyFrom(audioData);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode(locale)
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Performs speech recognition on the audio file
            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for (SpeechRecognitionResult result : results) {
                // There can be several alternative transcripts for a given chunk of speech. Just use the
                // first (most likely) one here.
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
                textResponse.append(alternative.getTranscript());
            }
        } catch (IOException e) {
            e.printStackTrace();
            textResponse = new StringBuilder(e.getMessage());
        }
        return textResponse.toString();
    }

    private File loadAudioSample() throws FileNotFoundException {
        return ResourceUtils.getFile("classpath:sample.wav");
    }
}
