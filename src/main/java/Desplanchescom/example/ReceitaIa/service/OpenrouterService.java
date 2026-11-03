package Desplanchescom.example.ReceitaIa.service;

import Desplanchescom.example.ReceitaIa.DTO.FoodItemDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class OpenrouterService {
    private final WebClient webClient;
    private final String apiKey;
    private final String model ;

    public OpenrouterService(WebClient webClient,
                             @Value("${OPENROUTER_API_KEY}") String apiKey,
                             @Value("${OPENROUTER_MODEL}") String model
                             ) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.model = model;
    }

    public Mono<String> generatedRecipe(List<FoodItemDTO> dtos){
        String alimentos = dtos.stream()
                .map(item -> String.format("%s (%s) - Quantidade: %d, Validade: %s",
                        item.getNome(), item.getCategoria(), item.getQuantidade(), item.getValidade()))
                .collect(Collectors.joining("\n"));
        if (dtos.isEmpty()) {
            return Mono.just("Cadastre alimentos antes de gerar uma receita.");
        }
        String prompt = "Baseado no meu banco de dados faça uma receita alguns dos seguintes ingredientes, levando em consideração a data de validade de cada item:\n" + alimentos;
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", "Você é um chef brasileiro."),
                        Map.of("role", "user", "content", prompt)
                )
        );
        return webClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response ->{
                    var choices = (List<Map<String,Object>>) response.get("choices");
                    if (choices != null && !choices.isEmpty()){
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        return message.get("content").toString();
                    }
                    return "Nenhuma receita foi gerada.";
        });
    }
}
