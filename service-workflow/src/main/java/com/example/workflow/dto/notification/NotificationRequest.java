package com.example.workflow.dto;// dto/NotificationRequest.java


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private Long studentId;
    private String type; // EMAIL, SMS
    private String subject;
    private String message;
}