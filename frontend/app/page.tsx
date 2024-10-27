'use client';

import { useState } from 'react';
import axios, { AxiosError } from 'axios';
import { KOTLIN_SPRING_URL } from './constants';
import { ErrorBox } from '@/lib/ErrorBox';

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
  const [SQLResponse, setSQLResponse] = useState([]);
  const [SQLError, setSQLError] = useState('');

  const [LLMValue, setLLMValue] = useState('');
  const [LLMResponse, setLLMResponse] = useState('');
  const [LLMError, setLLMError] = useState('');

  const handleSQLSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    try {
      const res = await axios.post(`${KOTLIN_SPRING_URL}/query`, { query: SQLValue });
      setSQLError('');
      setSQLResponse(res.data);
    } catch (error) {
      setSQLResponse([]);
      if (axios.isAxiosError(error)) {
        setSQLError(error.response?.data);
      } else if (error instanceof Error) {
        setSQLError(error.message);
      } else {
        setSQLError('Error:' + error);
      }
    } finally {
      setSQLValue('');
    }
  };

  const handleLLMSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    try {
      const res = await axios.post(`${KOTLIN_SPRING_URL}/llm`, { query: LLMValue });
      setLLMError('');
      setLLMResponse(res.data);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        setLLMError(error.response?.data);
      } else if (error instanceof Error) {
        setLLMError('Error: ' + error.message);
      } else {
        setLLMError('Error: ' + error);
      }
    } finally {
      setLLMValue('');
    }
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
          {SQLResponse && SQLResponse.map((puzzle: puzzleType) => <li key={puzzle.id}>{puzzle.puzzleId}</li>)}
          {SQLError && <ErrorBox error={SQLError} />}
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
          {LLMError && <ErrorBox error={LLMError} />}
        </p>
      </div>
    </div>
  );
}
