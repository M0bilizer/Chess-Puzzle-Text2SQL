FROM node:22-alpine AS builder

WORKDIR /app

COPY package.json pnpm-lock.yaml ./

RUN npm install -g pnpm && pnpm install --prod

COPY .next .next
COPY public public
COPY styles styles
COPY package.json ./

RUN apk add curl

EXPOSE 3000
CMD ["pnpm", "start"]