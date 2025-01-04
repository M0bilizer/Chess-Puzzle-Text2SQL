import { KOTLIN_SPRING_URL } from '../constants/endpoints';
import type { PuzzleType } from '../types/puzzle';

export const fetchSQLData = async (query: string): Promise<PuzzleType[]> => {
	const res = await fetch(`${KOTLIN_SPRING_URL}/queryPuzzle`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ query })
	});

	if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);

	// Parse the response and extract the "data" field
	const responseData = await res.json();
	if (responseData.status !== 'success') {
		throw new Error(`API error: ${responseData.status}`);
	}

	return responseData.data; // Return only the "data" field
};

export const fetchLLMData = async (query: string): Promise<string> => {
	const res = await fetch(`${KOTLIN_SPRING_URL}/debug/llm`, {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({ query })
	});

	if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);

	// Parse the response and extract the "data" field
	const responseData = await res.json();
	if (responseData.status !== 'success') {
		throw new Error(`API error: ${responseData.status}`);
	}

	return responseData.data; // Return only the "data" field
};
