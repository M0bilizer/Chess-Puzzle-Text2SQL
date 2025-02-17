import { handler } from './build/handler.js';
import express from 'express';

const app = express();

// Use the SvelteKit handler for all requests
app.use(handler);

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
	console.log(`Server is running on http://localhost:${PORT}`);
});
