package com.example.workflow.client;// client/StudentServiceClient.java
import com.example.workflow.dto.MatriculeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "etudiant-service", url = "${services.student.url}")
public interface StudentServiceClient {

//    @PostMapping("/api/students/{id}/activate")
//    void activateStudent(@PathVariable Long id);

    @PostMapping("/api/students/{id}/generate-matricule")
    MatriculeResponse updateEtudiant(@PathVariable("id") Long id);
}