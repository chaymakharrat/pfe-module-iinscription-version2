package pfe.example.finance_service.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemiseDTO {
    private Long id;
    private String motif;
    private Integer pourcentage;
    private String description;
}
