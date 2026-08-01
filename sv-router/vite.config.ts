import { svelte } from '@sveltejs/vite-plugin-svelte';
import tailwindcss from '@tailwindcss/vite';
import { playwright } from '@vitest/browser-playwright';
import * as path from 'node:path';
import Icons from 'unplugin-icons/vite';
import { defineConfig, loadEnv } from 'vite';
import runtimeEnv from 'vite-plugin-runtime-env';

export default defineConfig(({ mode }) => {
	const env = loadEnv(mode, process.cwd(), '');
	return {
		plugins: [
			tailwindcss(),
			svelte(),
			Icons({
				compiler: 'svelte'
			}),
			runtimeEnv()
		],
		resolve: {
			alias: {
				'@': path.resolve(import.meta.dirname, './src')
			}
		},
		base: env.VITE_BASE_PATH ? `/${env.VITE_BASE_PATH}/` : '/',
		test: {
			expect: { requireAssertions: true },
			projects: [
				{
					extends: './vite.config.ts',
					test: {
						name: 'client',
						browser: {
							enabled: true,
							provider: playwright(),
							instances: [{ browser: 'chromium', headless: true }]
						},
						include: ['src/**/*.svelte.{test,spec}.{js,ts}'],
						exclude: ['src/lib/server/**']
					}
				},
				{
					extends: './vite.config.ts',
					test: {
						name: 'server',
						environment: 'node',
						include: ['src/**/*.{test,spec}.{js,ts}'],
						exclude: ['src/**/*.svelte.{test,spec}.{js,ts}']
					}
				}
			]
		}
	};
});
