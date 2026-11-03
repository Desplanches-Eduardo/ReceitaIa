package Desplanchescom.example.ReceitaIa.controller;

import Desplanchescom.example.ReceitaIa.DTO.FoodItemDTO;
import Desplanchescom.example.ReceitaIa.service.FoodItemService;
import Desplanchescom.example.ReceitaIa.service.OpenrouterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class RecipeController {
    private FoodItemService foodItemService;
    private OpenrouterService service;

    public RecipeController(FoodItemService foodItemService, OpenrouterService service) {
        this.foodItemService = foodItemService;
        this.service = service;
    }

    @GetMapping("/generate")
    public Mono<ResponseEntity<String>> generatRecipe(){
        List<FoodItemDTO> foodItemDTOS = foodItemService.listar();
        return service.generatedRecipe(foodItemDTOS)
                .map(recipe -> ResponseEntity.ok(recipe))
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }
}
