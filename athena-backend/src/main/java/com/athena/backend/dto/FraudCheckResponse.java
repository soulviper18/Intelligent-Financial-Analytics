package com.athena.backend.dto;

public class FraudCheckResponse {

    private double fraudScore;

    public double getFraudScore() {
        return fraudScore;
    }

    public void setFraudScore(double fraudScore) {
        this.fraudScore = fraudScore;
    }
}
