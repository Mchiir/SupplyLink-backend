package com.supplylink;

import ClickSend.ApiClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootApplication(exclude = { ThymeleafAutoConfiguration.class })
public class BackendApplication {

    @Value("${clickSend-username}")
    private String clickSendUsername;
    @Value("${clickSend-apiKey}")
    private String clickSendApiKey;

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ApiClient clickSendConfig() {
        ApiClient clickSendApiClient = new ApiClient();
        clickSendApiClient.setUsername(clickSendUsername);
        clickSendApiClient.setPassword(clickSendApiKey);
        return clickSendApiClient;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
