package co.blasthack.mood.audio.controllers;

import co.blasthack.mood.audio.model.MoodAudioMessage;
import co.blasthack.mood.audio.model.MoodTextMessage;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1p1beta1.*;
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
import java.util.List;

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
        // en-GB  pl-PL
        String audioResponse = audioTranscription(data, "pl-PL");

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

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16) // LINEAR16 for WAV
                    .setLanguageCode(locale)
                    .setEnableAutomaticPunctuation(false)
                    //.setSampleRateHertz(48000)
                    .setModel("command_and_search") // default
                    .build();

            RecognitionAudio audioConfig = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            RecognizeResponse response = speechClient.recognize(config, audioConfig);
            List<SpeechRecognitionResult> results = response.getResultsList();

            System.out.print("Obtained Google Cloud response.");

            for (SpeechRecognitionResult result : results) {
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
        return ResourceUtils.getFile("classpath:sample_pl.wav");
        //return ResourceUtils.getFile("classpath:sample_pl.webm");
    }
}
