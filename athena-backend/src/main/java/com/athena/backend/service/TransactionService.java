package com.athena.backend.service;

import com.athena.backend.dto.FraudCheckResponse;
import com.athena.backend.dto.TransactionRequest;
import com.athena.backend.entity.Transaction;
import com.athena.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final RestTemplate restTemplate;

    // --- FIX IS HERE ---
    // The colon ':' adds a default value. 
    // If "ml.service.url" is missing, use "http://localhost:5000/predict"
    @Value("${ml.service.url:http://localhost:5000/predict}")
    private String mlServiceUrl;

    public TransactionService(TransactionRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public Transaction processTransaction(TransactionRequest request) {
        String status = "APPROVED";
        try {
            FraudCheckResponse response = restTemplate.postForObject(
                mlServiceUrl, 
                request, 
                FraudCheckResponse.class
            );

            if (response != null && response.getFraudScore() > 0.8) {
                status = "REJECTED";
            }
        } catch (Exception e) {
            System.err.println("ML Service Error: " + e.getMessage());
        }

        Transaction tx = new Transaction();
        tx.setUserId(request.getUserId());
        tx.setAmount(request.getAmount());
        tx.setCurrency(request.getCurrency());
        tx.setStatus(status);
        if (tx.getTimestamp() == null) {
            tx.setTimestamp(LocalDateTime.now());
        }
        
        return repository.save(tx);
    }

    public Transaction process(TransactionRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'process'");
    }
}
