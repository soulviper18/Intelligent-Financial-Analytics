import React, { useState } from 'react';
import axios from 'axios';
import { ShieldCheck, ShieldAlert, Activity } from 'lucide-react';
import './index.css';

function App() {
  const [formData, setFormData] = useState({
    userId: '',
    amount: '',
    currency: 'USD'
  });
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setResult(null);

    try {
      // Connects to Backend running on port 8080
      const response = await axios.post('http://localhost:8080/api/transactions', {
        userId: parseInt(formData.userId),
        amount: parseFloat(formData.amount),
        currency: formData.currency
      });
      setResult(response.data);
    } catch (error) {
      console.error("Error:", error);
      alert("Failed to connect to the backend. Is Docker running?");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <header className="header">
        <div className="logo">ATHENA <span style={{color:'white', fontWeight:'300'}}>ANALYTICS</span></div>
      </header>

      <div className="card">
        <h2 style={{ display: 'flex', alignItems: 'center', gap: '10px', marginTop: 0 }}>
          <Activity color="#3b82f6" /> New Transaction
        </h2>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>User ID</label>
            <input 
              type="number" 
              name="userId" 
              placeholder="e.g. 101" 
              value={formData.userId}
              onChange={handleChange}
              required 
            />
          </div>

          <div className="form-group">
            <label>Amount</label>
            <input 
              type="number" 
              name="amount" 
              placeholder="0.00" 
              step="0.01"
              value={formData.amount}
              onChange={handleChange}
              required 
            />
          </div>

          <div className="form-group">
            <label>Currency</label>
            <select name="currency" value={formData.currency} onChange={handleChange}>
              <option value="USD">USD - US Dollar</option>
              <option value="EUR">EUR - Euro</option>
              <option value="GBP">GBP - British Pound</option>
            </select>
          </div>

          <button type="submit" disabled={loading}>
            {loading ? 'Analyzing Pattern...' : 'Process Transaction'}
          </button>
        </form>

        {result && (
          <div className={`result-box ${result.status}`}>
            <div style={{display:'flex', justifyContent:'center', marginBottom:'10px'}}>
              {result.status === 'REJECTED' ? <ShieldAlert size={48} /> : <ShieldCheck size={48} />}
            </div>
            <h2 style={{margin:'0'}}>{result.status}</h2>
            <p>Risk Analysis Complete</p>
            <div style={{fontSize:'0.9rem', opacity:0.8}}>
              Transaction ID: #{result.id}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;