FROM oven/bun:1.3.14-alpine AS build

WORKDIR /app
COPY package*.json ./
RUN bun ci
COPY . ./
RUN bun run build

FROM caddy:2.11.4-alpine
WORKDIR /app
COPY Caddyfile ./

# we need gettext for the envsubst
RUN apk add --no-cache gettext

RUN caddy fmt Caddyfile --overwrite
COPY --from=build /app/dist ./dist

EXPOSE 80

# for runtime env
CMD ["sh", "-c", "envsubst < /app/dist/index.html > /app/dist/index.html.tmp && mv /app/dist/index.html.tmp /app/dist/index.html && exec caddy run --config Caddyfile --adapter caddyfile"]
