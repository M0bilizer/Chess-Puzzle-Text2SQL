# Builder Stage
FROM oven/bun:alpine AS builder

WORKDIR /app

COPY bun.lockb ./
COPY package.json ./

RUN bun install

COPY . .

RUN bun run build

FROM oven/bun:alpine

WORKDIR /app

COPY --from=builder /app/build ./build
COPY --from=builder /app/server.js ./server.js
COPY bun.lockb ./
COPY package.json ./

RUN bun install

RUN apk add --no-cache curl

EXPOSE 3000

CMD ["node", "server.js"]
