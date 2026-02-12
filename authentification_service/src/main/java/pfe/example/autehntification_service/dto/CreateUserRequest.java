package pfe.example.autehntification_service.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String login;
    private String password;
    private String role;
    private String nom;
    private String prenom;
}