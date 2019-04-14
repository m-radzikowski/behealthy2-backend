package co.blasthack.mood.audio.integration;

import co.blasthack.mood.audio.config.GoogleAuthConfig;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1p1beta1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class GoogleSpeechToText implements SpeechToText {

    private final GoogleAuthConfig googleAuthConfig;

    public GoogleSpeechToText(GoogleAuthConfig googleAuthConfig) {
        this.googleAuthConfig = googleAuthConfig;
    }

    @Override
    public String audioTranscription(byte[] audioData) {
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
}
