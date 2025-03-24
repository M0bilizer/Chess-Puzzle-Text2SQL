import { handler } from './build/handler.js';
import express from 'express';

const app = express();

// Use the SvelteKit handler for all requests
app.use(handler);

const PORT = process.env.PORT || 3000;
const KOTLIN_SPRING_URL = process.env.KOTLIN_SPRING_URL;
const NODE_VERSION = process.version.match(/^v(\d+\.\d+)/)[1]
app.listen(PORT, () => {
	console.log(
		`
==================================================================================================================

  \\+/    \\^/   (V)   ("\\   [-]    _   ┏┓┓             ┓    ┏┳┓     ┏┓┏┓┏┓┓     _     [-]   ("\\   (V)   \\^/   \\+/
  ) (    ) (   ) (   ) '   | |   ( )  ┃ ┣┓┏┓┏┏  ┏┓┓┏┓┓┃┏┓   ┃ ┏┓┓┏╋┏┛┗┓┃┃┃    ( )    | |   ) '   ) (   ) (   ) (
 /___\\  /___\\ /___\\ /___\\ /___\\ /___\\ ┗┛┛┗┗ ┛┛  ┣┛┗┻┗┗┗┗    ┻ ┗ ┛┗┗┗━┗┛┗┻┗┛  /___\\  /___\\ /___\\ /___\\ /___\\ /___\\
                                                ┛

  :: Node Version        : ${NODE_VERSION}
  :: Server Port         : ${PORT}
  :: API Destination URL : ${KOTLIN_SPRING_URL}

==================================================================================================================
		`)
});
