package pfe.example.enrollement_module.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {
    private String login;
    private String password;
    private String role;
    private String nom;
    private String prenom;
    private String email;
}
