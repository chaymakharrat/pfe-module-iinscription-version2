package com.test_cumanda.workflow;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicationService {

private final RuntimeService runtimeService;
    //private ProcessEngine processEngine;

//    public PublicationService(RuntimeService processEngine) {
//        this.runtimeService = processEngine;
//    }

    public void demarrerProcess() {
//        processEngine.startProcessInstanceByKey("Process_0p6t46q");
        runtimeService.startProcessInstanceByMessage("nouvelleIdee");
    }

    public void notifierMessage() {
        runtimeService.correlateMessage("VideoEditee");
    }
}
