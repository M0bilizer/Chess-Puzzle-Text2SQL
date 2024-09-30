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
  const [inputValue, setInputValue] = useState('');
  const [puzzles, setPuzzles] = useState([]);

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    try {
      const res = await fetch('http://localhost:8080/api/test', {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
      });

      if (!res.ok) {
        throw new Error('Network response was not ok');
      }

      const data = await res.json();
      setPuzzles(data);
    } catch (error: unknown) {
      console.error('Error:', error);
    }

    setInputValue('');
  };

  return (
    <div className="w-1/3 h-full ml-auto mr-auto pl-4 rounded-lg flex flex-col items-center justify-center">
      <h1>Chess Puzzle</h1>
      <form onSubmit={handleSubmit} className="flex flex-col gap-5">
        <input type="text" value={inputValue} className="border" onChange={(e) => setInputValue(e.target.value)} />
        <button className="border rounded" type="submit">
          Submit
        </button>
      </form>

      {puzzles && puzzles.map((puzzle: puzzleType) => <li key={puzzle.id}>{puzzle.puzzleId}</li>)}
    </div>
  );
}
