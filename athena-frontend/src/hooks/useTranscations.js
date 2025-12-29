import { useEffect, useState } from "react";
import { getTransactions } from "../api/transactionApi";

export function useTransactions() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getTransactions()
      .then(res => setData(res.data))
      .catch(() => setError("Failed to load transactions"))
      .finally(() => setLoading(false));
  }, []);

  return { data, loading, error };
}
