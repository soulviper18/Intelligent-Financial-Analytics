from fastapi import FastAPI
from pydantic import BaseModel
import numpy as np
from sklearn.ensemble import IsolationForest

# Initialize FastAPI app
app = FastAPI(title="Athena ML Service")

# ---------------------------
# Mock ML Model (on startup)
# ---------------------------
model = IsolationForest(
    n_estimators=100,
    contamination=0.1,
    random_state=42
)

# Train on fake historical transaction data
# [amount, location_score, merchant_score]
X_train = np.random.rand(500, 3)
model.fit(X_train)

# ---------------------------
# Request / Response Models
# ---------------------------
class TransactionRequest(BaseModel):
    amount: float
    location_risk: float
    merchant_risk: float

class PredictionResponse(BaseModel):
    fraud_score: float
    is_fraud: bool

# ---------------------------
# API Endpoint
# ---------------------------
@app.post("/predict", response_model=PredictionResponse)
def predict_fraud(tx: TransactionRequest):
    features = np.array([[tx.amount, tx.location_risk, tx.merchant_risk]])
    score = model.decision_function(features)[0]

    # Normalize score to 0–1 range
    fraud_score = float((1 - score) / 2)
    is_fraud = fraud_score > 0.7

    return {
        "fraud_score": round(fraud_score, 4),
        "is_fraud": is_fraud
    }

# ---------------------------
# Health Check
# ---------------------------
@app.get("/health")
def health():
    return {"status": "ML service is running"}
