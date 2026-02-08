package pfe.example.etudiantservice.dto;

import lombok.Getter;
import lombok.Setter;
// Removed incorrect import


import java.util.List;

@Getter
@Setter
public class PaysApiResponse {

    private String status;
    private List<PaysApiDto> data;
}
