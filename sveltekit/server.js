import { handler } from './build/handler.js';
import express from 'express';

const app = express();

// Use the SvelteKit handler for all requests
app.use(handler);

const PORT = process.env.PORT || 3000;
const KOTLIN_SPRING_URL = process.env.KOTLIN_SPRING_URL
app.listen(PORT, () => {
	console.log(`Server is running on http://localhost:${PORT}`);
	console.log(`API calls is sent to ${KOTLIN_SPRING_URL}`)
});
