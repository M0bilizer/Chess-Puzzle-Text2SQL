# Sveltekit Frontend

This is a Sveltekit application developed to serve the frontend and allow users to play chess puzzles.

---

## Prerequisite

This project uses `bun`, an extremely fast Javascript package and project manager.
These are the system requirement

- Node.js 22: Ensure Node.js 22 is installed on your system.
- Docker (optional): If you want to run the application in a Docker container.

---

## Installation

1. If you don’t have `bun` installed, you can install it using `npm`:

```commandline
npm install -g bun
```

4. Install Dependencies

```commandline
bun install
```

---

## Running the application

### Locally

- Development

Run the Sveltekit application in development:

```commandline
bun dev
```

The application will be available at http://localhost:5173.

- Production

Preview the Sveltekit application in production by building:

```commandline
bun run build
```

then run

```commandline
bun run preview
```

The application will be available at http://localhost:4173.

### Using Docker

If you prefer to run the application in a Docker container, follow these steps:

1. Build the Docker Image:

```commandline
docker build -t my-sveltekit-app .
```

2. Run the Docker Container:

```commandline
docker run -p 5173:5173 my-sveltekit-app
```

The application will be available at http://localhost:5173.

---

## Other useful commands

- Lint the project using `prettier`:

```commandline
bun format
```

- Build the docker image with _typescript-sveltekit_ as it's name:

```commandline
bash build.sh
```

---

## Packages Used

| Packages                    | Description / Purpose                              |
| --------------------------- | -------------------------------------------------- |
| Sveltekit                   | Framework                                          |
| bun                         | Package Manager                                    |
| @skeletonlabs/skeleton@next | UI Components                                      |
| @zerodevx/svelte-toast      | Custom Svelte Toast                                |
| svelte-chess                | Chess component                                    |
| svelte-chessground          | Static Chess component                             |
| unplugin-icons              | Icon components                                    |
| @iconify-json/fa6-solid     | FontAwesome 6 Solid Icons (through unplugin-icons) |
| Express                     | Node.js server                                     |
| tailwind                    | css classes                                        |
| prettier                    | Code formatter                                     |
| eslint                      | Linter                                             |
