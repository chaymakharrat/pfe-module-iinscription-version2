package com.example.workflow.client;

import com.example.workflow.dto.GenerateInvoiceRequest;
import com.example.workflow.dto.InvoiceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "finance-service", url = "${services.finance.url}")
public interface FinanceServiceClient {

    @PostMapping("/api/invoices/generate")
    InvoiceDTO generateInvoice(@RequestBody GenerateInvoiceRequest request);
}