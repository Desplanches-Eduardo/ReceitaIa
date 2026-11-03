package Desplanchescom.example.ReceitaIa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClienteConfig {
    @Value("${openrouter.api.url:https://openrouter.ai/api/v1/chat/completions}")
    private String OpenRouterApiUrl;

    @Bean
    public WebClient webClient( WebClient.Builder builder){
        return builder.baseUrl(OpenRouterApiUrl).build();
    }

}
