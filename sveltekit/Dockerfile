FROM oven/bun:alpine

WORKDIR /app

COPY bun.lockb ./
COPY package.json ./

RUN bun install --production

COPY build ./build
COPY server.js ./

RUN apk add curl

EXPOSE 3000

CMD ["bun", "server.js"]
