package Desplanchescom.example.ReceitaIa.service;

import Desplanchescom.example.ReceitaIa.DTO.FoodItemDTO;
import Desplanchescom.example.ReceitaIa.Mapper.FoodItemMapper;
import Desplanchescom.example.ReceitaIa.model.FoodItemModel;
import Desplanchescom.example.ReceitaIa.repository.FoodItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodItemService {

    private final FoodItemRepository repository;
    private final FoodItemMapper mapper;

    public FoodItemService(FoodItemRepository repository, FoodItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public FoodItemDTO salvar(FoodItemDTO dto){
        FoodItemModel food = mapper.map(dto);
        food = repository.save(food);
        return mapper.map(food);
    }

    public List<FoodItemDTO> listar(){
        List<FoodItemModel> foods = repository.findAll();
        return foods.stream()
                .map(mapper::map)
                .collect(Collectors.toList());
    }

    public FoodItemDTO listarId(Long id){
        return repository.findById(id).map(mapper::map).orElse(null);
    }

    public void deletar(long id) {
        repository.deleteById(id);
    }

    public FoodItemDTO atualizar(FoodItemDTO dto,
                                 long id){
        Optional<FoodItemModel> foodId= repository.findById(id);
      if (foodId.isPresent()){
            FoodItemModel foodAtualizada = mapper.map(dto);
            foodAtualizada.setId(id);
            FoodItemModel foodSalva = repository.save(foodAtualizada);
            return mapper.map(foodSalva);
      }
      return null;
    }
}
