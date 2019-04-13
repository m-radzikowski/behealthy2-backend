package co.blasthack.mood;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class TextServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TextServiceApplication.class, args);
    }
}



