package co.blasthack.mood.audio.controllers;

import co.blasthack.mood.audio.config.GoogleAuthConfig;
import co.blasthack.mood.audio.model.MoodAudioMessage;
import co.blasthack.mood.audio.model.MoodTextMessage;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/")
class AudioController {

    private final RestTemplate restTemplate;

    private final GoogleAuthConfig googleAuthConfig;

    public AudioController(RestTemplate restTemplate, GoogleAuthConfig googleAuthConfig) {
        this.restTemplate = restTemplate;
        this.googleAuthConfig = googleAuthConfig;
    }

    /*
        Test method to check connection with Google Cloud Speech to Text API using sample wav data
     */
    @RequestMapping("/test")
    public String test() throws IOException {
        File audioSample = loadAudioSample();
        byte[] data = Files.readAllBytes(audioSample.toPath());
        String audioResponse = audioTranscription(data);
        return "Text: " + audioResponse;
    }

    @RequestMapping(path = "/mood", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    Float rateMoodOfText(@RequestBody MoodAudioMessage audioBase64) {
        MoodTextMessage textMessage;
        RestTemplate restTemplate1 = new RestTemplate();
        restTemplate1.getMessageConverters().add(
                new ByteArrayHttpMessageConverter());


        //ResponseEntity<byte[]> codecResult = restTemplate1.postForEntity("http://codec-service/convert", audioBase64, byte[].class);
        ResponseEntity<byte[]> codecResult = restTemplate1.postForEntity("http://127.0.0.1:5001/convert", audioBase64, byte[].class);

/*
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        ResponseEntity<byte[]> response = restTemplate1.exchange(
                "http://codec-service/convert",
                HttpMethod.POST, entity, byte[].class, "1");
*/



        //ResponseEntity<File> codecResult = restTemplate1.postForEntity("http://codec-service/convert", audioBase64, File.class);

          //  byte[] data  = Files.readAllBytes(codecResult.getBody());

        String transcribedAudio = audioTranscription(codecResult.getBody());
        textMessage  = new MoodTextMessage(transcribedAudio);

        ResponseEntity<Float> result = restTemplate.postForEntity("http://witai-service/sentiment", textMessage, Float.class);

        return result.getBody();
    }

    private String audioTranscription(byte[] audioData) {
        StringBuilder textResponse = new StringBuilder();
        try {
            FileInputStream credentialsStream = new FileInputStream(googleAuthConfig.getCredentialFilePath());
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

            SpeechSettings speechSettings = SpeechSettings.newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .build();

            SpeechClient speechClient = SpeechClient.create(speechSettings);
            ByteString audioBytes = ByteString.copyFrom(audioData);

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16) // LINEAR16 for WAV
                    .setLanguageCode("pl-PL")
                    .setEnableAutomaticPunctuation(false)
                    //.setSampleRateHertz(48000) // WAV doesnt need
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
        return ResourceUtils.getFile("classpath:sample.wav");
    }
}
