package Desplanchescom.example.ReceitaIa.DTO;

import Desplanchescom.example.ReceitaIa.enums.Categoria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemDTO {
    private Long id;
    private String nome;
    private Categoria categoria;
    private Integer quantidade;
    private LocalDate validade;
}
