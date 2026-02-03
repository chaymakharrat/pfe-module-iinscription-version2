package pfe.example.etudiantservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "authentification-service")
public interface AuthServiceClient {
    @GetMapping("/auth/validate")
    Boolean validate(@RequestHeader("Authorization") String token);
}
