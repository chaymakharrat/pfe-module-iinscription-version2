package com.test_cumanda.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class PublicationLinkedin implements JavaDelegate{
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        System.out.println("PublicationLinkedin");
    }
}
