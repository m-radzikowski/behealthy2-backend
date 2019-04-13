package co.blasthack.mood.audio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AudioServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AudioServiceApplication.class, args);
    }
}