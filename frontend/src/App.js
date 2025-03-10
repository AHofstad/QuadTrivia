import React, { useState } from "react";

const API_BASE_URL = "http://localhost:8080/api";

export default function TriviaGame() {
  const [question, setQuestion] = useState(null);
  const [selectedAnswer, setSelectedAnswer] = useState(null);
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchQuestion = async () => {
    setLoading(true);
    setError(null);
    setResult(null);
    setSelectedAnswer(null);

    try {
      const response = await fetch(`${API_BASE_URL}/questions`, {
        method: "GET",
        credentials: "include",
      });
      if (!response.ok) throw new Error(await response.text());
      const data = await response.json();
      setQuestion(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const checkAnswer = async (answer) => {
    setSelectedAnswer(answer);
    try {
      const response = await fetch(`${API_BASE_URL}/checkAnswers`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ answer }),
      });
      if (!response.ok) throw new Error(await response.text());
      const isCorrect = await response.json();
      setResult(isCorrect ? "Correct! 🎉" : "Wrong! ❌");
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div style={{ textAlign: "center", padding: "20px" }}>
      <h1>Trivia Game</h1>
      <button onClick={fetchQuestion} disabled={loading} style={{ marginBottom: "10px" }}>
        {loading ? "Loading..." : "Get New Question"}
      </button>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {question && (
        <div style={{ border: "1px solid #ddd", padding: "20px", maxWidth: "500px", margin: "auto" }}>
          <h2>{question.question}</h2>
          <div>
            {question.answers.map((answer, index) => (
              <button
                key={index}
                style={{ display: "block", margin: "10px auto", padding: "10px", cursor: "pointer" }}
                onClick={() => checkAnswer(answer)}
                disabled={!!selectedAnswer}
              >
                {answer}
              </button>
            ))}
          </div>
          {result && <p style={{ fontWeight: "bold", marginTop: "10px" }}>{result}</p>}
        </div>
      )}
    </div>
  );
}
