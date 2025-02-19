#!/bin/bash

rm -rf build #necessary for bun run build
bun run build
docker build -t typescript-sveltekit .