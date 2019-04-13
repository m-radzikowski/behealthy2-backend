package co.blasthack.mood.fun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class FunServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunServiceApplication.class, args);
    }
}



