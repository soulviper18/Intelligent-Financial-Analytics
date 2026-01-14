import { useEffect, useState } from "react";

import Navbar from "./components/layout/Navbar";
import Hero from "./components/sections/Hero";
import Dashboard from "./pages/Dashboard";

import { checkBackendHealth } from "./services/healthService";

export default function App() {
  const [backendStatus, setBackendStatus] = useState("Checking backend...");

  useEffect(() => {
    checkBackendHealth()
      .then((res) => setBackendStatus(res))
      .catch(() => setBackendStatus("Backend not reachable ❌"));
  }, []);

  return (
    <>
      <Navbar />
      <Hero />

      {/* ✅ TEMP: Backend status indicator */}
      <div style={{ textAlign: "center", margin: "1rem", fontWeight: "bold" }}>
        {backendStatus}
      </div>

      <Dashboard />
    </>
  );
}
