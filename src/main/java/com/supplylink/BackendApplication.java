package com.supplylink;

import com.twilio.Twilio;
import io.github.cdimascio.dotenv.Dotenv;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication(exclude = { ThymeleafAutoConfiguration.class })
public class BackendApplication {

    private static Dotenv dotenv;
    private static String ACCOUNT_SID = "";
    private static String AUTH_ID = "";

    static {
        dotenv = Dotenv.load();
        ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
        AUTH_ID = dotenv.get("TWILIO_AUTH_TOKEN");

        if(AUTH_ID == "" || ACCOUNT_SID == "") throw new RuntimeException("TWILIO credentials not set");
        Twilio.init(ACCOUNT_SID, AUTH_ID);
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
