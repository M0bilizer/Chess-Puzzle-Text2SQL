interface ImportMetaEnv {
	// set the railway's internal network IPv4 here
	// only used by Caddy
	readonly BACKEND_URL: string;
	// backend api
	readonly VITE_API_URL: string;
	// if this is hosted at rakichazcaballero/chess-puzzle-textsql, set this as "chess-puzzle-text2sql"
	readonly BASE_URL?: string;
}

interface ImportMeta {
	readonly env: ImportMetaEnv;
}
