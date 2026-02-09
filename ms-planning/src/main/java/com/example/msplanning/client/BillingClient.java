package com.example.msplanning.client;

import com.example.msplanning.dto.InvoiceVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-billing")
public interface BillingClient {

    @PostMapping("/api/billing")
    InvoiceVO createInvoice(@RequestBody InvoiceVO invoiceVO);
}
