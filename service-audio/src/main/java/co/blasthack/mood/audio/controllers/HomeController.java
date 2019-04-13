package co.blasthack.mood.audio.controllers;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @RequestMapping("/test")
    public String test() {

        String audioResponse = analiseAudio();
        String textResponse = restTemplate.getForObject("http://text-service/test/", String.class);

        return "::: AUDIO(google) --> " + audioResponse + "  ::: TEXT(service) --> " + textResponse;
    }

    private String analiseAudio() {
        StringBuilder textResponse = new StringBuilder();
        try {

            FileInputStream credentialsStream = new FileInputStream("C:/Java/speech-credentials.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

            SpeechSettings speechSettings = SpeechSettings.newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .build();

            SpeechClient speechClient = SpeechClient.create(speechSettings);

            File audioSample = loadAudioSample();
            byte[] data = Files.readAllBytes(audioSample.toPath());
            ByteString audioBytes = ByteString.copyFrom(data);

            // Builds the sync recognize request
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(8000)
                    .setLanguageCode("en-US")
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
