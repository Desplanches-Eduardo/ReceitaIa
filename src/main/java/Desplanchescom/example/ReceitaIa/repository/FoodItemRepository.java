package Desplanchescom.example.ReceitaIa.repository;

import Desplanchescom.example.ReceitaIa.model.FoodItemModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FoodItemRepository extends JpaRepository<FoodItemModel, Long> {
}
