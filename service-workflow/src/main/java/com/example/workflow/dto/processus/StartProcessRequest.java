package com.example.workflow.dto;// dto/StartProcessRequest.java


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartProcessRequest {
    private Long enrollmentId;
    private Long studentId;
    private String nomDiplome;
}
//--> je reçoit StartProcessRequest et je le traite et transforme en ProcessInstanceResponse