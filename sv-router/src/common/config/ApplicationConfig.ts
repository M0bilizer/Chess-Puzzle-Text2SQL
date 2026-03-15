import { Result } from 'typescript-result';
import * as v from 'valibot';
import { ConfigurationError } from '@/common/types/error';

export const EnvSchema = v.object({
	nodeEnv: v.optional(v.picklist(['development', 'production'])),
	baseUrl: v.string(),
	apiUrl: v.pipe(
		v.string(),
		v.url('Base URL must be a valid URL'),
		v.minLength(1, 'Base URL cannot be empty')
	)
});
export type Env = v.InferOutput<typeof EnvSchema>;

export function loadConfigFromVite(): Record<string, string> {
	return {
		baseUrl: import.meta.env.BASE_URL,
		apiUrl: import.meta.env.VITE_API_URL
	};
}

export function loadApplicationConfiguration(
	viteEnv: Record<string, string>
): Result<Env, ConfigurationError> {
	const result = v.safeParse(EnvSchema, viteEnv);
	if (!result.success) {
		return Result.error(new ConfigurationError(result.issues));
	}
	return Result.ok(result.output);
}
