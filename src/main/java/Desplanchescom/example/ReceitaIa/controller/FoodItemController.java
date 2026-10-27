package Desplanchescom.example.ReceitaIa.controller;

import Desplanchescom.example.ReceitaIa.DTO.FoodItemDTO;
import Desplanchescom.example.ReceitaIa.service.FoodItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
public class FoodItemController {

    private final FoodItemService service;

    public FoodItemController(FoodItemService service) {
        this.service = service;
    }

    //POST
    @PostMapping
    public ResponseEntity<String> criar(@RequestBody FoodItemDTO dto){
        FoodItemDTO salvo = service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo.getNome() + " criado(a) com sucesso");
    }

    //GET
    @GetMapping
    public ResponseEntity<List<FoodItemDTO>> listar(){
        List<FoodItemDTO> foods = service.listar();
        return ResponseEntity.ok(foods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> listarId(@PathVariable Long id) {
        FoodItemDTO food = service.listarId(id);
        if (food != null) {
            return ResponseEntity.ok(food);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comida não encontrada");
    }

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    //UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@RequestBody FoodItemDTO dto,
                                       @PathVariable Long id){
        FoodItemDTO food = service.atualizar(dto, id);
        if (food != null){
            return ResponseEntity.ok(food);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comida não encontrada");
    }
}
