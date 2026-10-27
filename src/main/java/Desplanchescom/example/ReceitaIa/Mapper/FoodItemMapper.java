package Desplanchescom.example.ReceitaIa.Mapper;

import Desplanchescom.example.ReceitaIa.DTO.FoodItemDTO;
import Desplanchescom.example.ReceitaIa.model.FoodItemModel;
import org.springframework.stereotype.Component;

@Component
public class FoodItemMapper {

    public FoodItemModel map(FoodItemDTO foodItemDTO){
        FoodItemModel foodItemModel = new FoodItemModel();
        foodItemModel.setId(foodItemDTO.getId());
        foodItemModel.setNome(foodItemDTO.getNome());
        foodItemModel.setCategorias(foodItemDTO.getCategorias());
        foodItemModel.setQuantidade(foodItemDTO.getQuantidade());
        foodItemModel.setValidade(foodItemDTO.getValidade());
        return foodItemModel;
    }

    public FoodItemDTO map(FoodItemModel foodItemModel) {
        FoodItemDTO foodItemDTO = new FoodItemDTO();
        foodItemDTO.setId(foodItemModel.getId());
        foodItemDTO.setNome(foodItemModel.getNome());
        foodItemDTO.setCategorias(foodItemModel.getCategorias());
        foodItemDTO.setQuantidade(foodItemModel.getQuantidade());
        foodItemDTO.setValidade(foodItemModel.getValidade());
        return foodItemDTO;
    }
}
