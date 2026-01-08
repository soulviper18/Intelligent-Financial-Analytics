import { useState } from "react";
import ErrorBanner from "../components/common/ErrorBanner";

export default function Dashboard() {
  const [form, setForm] = useState({
    userId: "",
    amount: "",
    currency: "USD",
  });

  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function submitTransaction(e) {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const res = await fetch("http://localhost:8080/transactions", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          userId: Number(form.userId),
          amount: Number(form.amount),
          currency: form.currency,
        }),
      });

      if (!res.ok) {
        throw new Error("Backend error");
      }

      const data = await res.json();
      setResult(data);
    } catch (err) {
      setError("Transaction failed. Backend not reachable.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <section
      id="dashboard"
      className="min-h-screen bg-black text-white px-6 py-24"
    >
      <div className="max-w-5xl mx-auto grid grid-cols-1 lg:grid-cols-2 gap-10">

        {/* LEFT — FORM */}
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-8">
          <h2 className="text-2xl font-bold mb-6">
            Transaction Console
          </h2>

          {error && <ErrorBanner message={error} />}

          <form onSubmit={submitTransaction} className="space-y-5">
            <div>
              <label className="text-sm text-gray-400">User ID</label>
              <input
                type="number"
                name="userId"
                required
                value={form.userId}
                onChange={handleChange}
                className="w-full mt-1 p-3 rounded-lg bg-black border border-white/10 focus:border-red-500 outline-none"
              />
            </div>

            <div>
              <label className="text-sm text-gray-400">Amount</label>
              <input
                type="number"
                name="amount"
                required
                value={form.amount}
                onChange={handleChange}
                className="w-full mt-1 p-3 rounded-lg bg-black border border-white/10 focus:border-red-500 outline-none"
              />
            </div>

            <div>
              <label className="text-sm text-gray-400">Currency</label>
              <select
                name="currency"
                value={form.currency}
                onChange={handleChange}
                className="w-full mt-1 p-3 rounded-lg bg-black border border-white/10 focus:border-red-500 outline-none"
              >
                <option>USD</option>
                <option>EUR</option>
                <option>INR</option>
              </select>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 rounded-xl bg-gradient-to-r from-red-600 to-orange-600 font-bold hover:scale-[1.02] transition disabled:opacity-50"
            >
              {loading ? "ANALYZING..." : "ANALYZE TRANSACTION"}
            </button>
          </form>
        </div>

        {/* RIGHT — RESULT */}
        <div className="flex items-center justify-center">
          {!result && (
            <p className="text-gray-500 text-center">
              Submit a transaction to see fraud analysis.
            </p>
          )}

          {result && (
            <div
              className={`w-full rounded-2xl p-8 border backdrop-blur-xl ${
                result.status === "REJECTED"
                  ? "bg-red-900/20 border-red-500 text-red-400"
                  : "bg-green-900/20 border-green-500 text-green-400"
              }`}
            >
              <h3 className="text-2xl font-bold mb-2">
                {result.status}
              </h3>
              <p className="text-sm mb-4">
                Transaction ID: {result.id}
              </p>
              <p className="font-mono text-lg">
                Amount: {result.amount} {result.currency}
              </p>
            </div>
          )}
        </div>

      </div>
    </section>
  );
}
