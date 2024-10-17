'use client';

import { useState } from 'react';

type puzzleType = {
  id: number;
  puzzleId: string;
  fen: string;
  moves: string;
  rating: number;
  ratingDeviation: number;
  popularity: number;
  nbPlays: number;
  themes: string;
  gameUrl: string;
  openingTags: string;
};

export default function Home() {
  const [SQLValue, setSQLValue] = useState('');
  const [LLMValue, setLLMValue] = useState('');
  const [puzzles, setPuzzles] = useState([]);
  const [LLMResponse, setLLMResponse] = useState('');
  const [error, setError] = useState('');

  const handleSQLSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    try {
      const res = await fetch('http://localhost:8080/api/query', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ query: SQLValue }),
      });

      if (!res.ok) {
        setSQLValue('');
        setError('Network response was not ok');
        throw new Error('Network response was not ok');
      }

      setError('');
      const data = await res.json();
      setPuzzles(data);
    } catch (error: unknown) {
      if (error instanceof TypeError) {
        console.error('Network Error:', error);
        setError('Could not receive response');
      }

      console.error('Error:', error);
    }

    setSQLValue('');
  };

  const handleLLMSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    try {
      const res = await fetch('http://localhost:8080/api/llm', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ query: LLMValue }),
      });

      if (!res.ok) {
        setLLMValue('');
        setError('Network response was not ok');
        throw new Error('Network response was not ok');
      }

      setError('');
      const data = await res.json();
      setLLMResponse(data);
    } catch (error: unknown) {
      if (error instanceof TypeError) {
        console.error('Network Error:', error);
        setError('Could not receive response');
      }

      console.error('Error:', error);
    }

    setLLMValue('');
  };

  return (
    <div className="w-1/3 h-full ml-auto mr-auto pl-4 rounded-lg flex flex-col items-center pt-40 gap-4">
      <h1>Chess Puzzle</h1>
      <div className="flex border-gray-200 border p-4 gap-4">
        <form onSubmit={handleSQLSubmit} className="flex flex-col gap-5">
          <label htmlFor="sql">SQL</label>
          <input
            id="sql"
            type="text"
            value={SQLValue}
            className="border"
            onChange={(e) => setSQLValue(e.target.value)}
          />
          <button className="border rounded" type="submit">
            Submit
          </button>
        </form>
        <div className="border border-gray-200 p-2 flex flex-col">
          Result
          {puzzles && puzzles.map((puzzle: puzzleType) => <li key={puzzle.id}>{puzzle.puzzleId}</li>)}
        </div>
      </div>

      <div className="flex border-gray-200 border p-4 gap-4">
        <form onSubmit={handleLLMSubmit} className="flex flex-col gap-5">
          <label htmlFor="LLM">LLM</label>
          <input
            id="llm"
            type="text"
            value={LLMValue}
            className="border"
            onChange={(e) => setLLMValue(e.target.value)}
          />
          <button className="border rounded" type="submit">
            Submit
          </button>
        </form>
        <p className="border border-gray-200 p-2 flex flex-col">
          Result
          {LLMResponse}
        </p>
      </div>

      {error.length > 0 && <div className="bg-red-400 rounded py-4 px-12">{error}</div>}
    </div>
  );
}
