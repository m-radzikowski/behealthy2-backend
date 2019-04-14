package co.blasthack.mood.audio;

import co.blasthack.mood.audio.config.GoogleAuthConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableConfigurationProperties(GoogleAuthConfig.class)
@EnableEurekaClient
public class AudioServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AudioServiceApplication.class, args);
    }
}