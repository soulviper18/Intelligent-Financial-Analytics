package com.athena.backend.controller;

import com.athena.backend.dto.TransactionRequest;
import com.athena.backend.entity.Transaction;
import com.athena.backend.service.TransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public Transaction submit(@RequestBody TransactionRequest request) {
        return service.processTransaction(request);
    }
}
