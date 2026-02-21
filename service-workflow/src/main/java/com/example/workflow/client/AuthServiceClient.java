package pfe.example.enrollement_module.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pfe.example.enrollement_module.dto.auth.CreateUserRequest;

@FeignClient(name = "authentification-service")
public interface AuthServiceClient {

    @PostMapping("/authentifier/utilisateurs/create")
    void createUtilisateur(@RequestBody CreateUserRequest request);
}
