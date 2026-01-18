import * as v from 'valibot';

export const EnvSchema = v.object({
	nodeEnv: v.optional(v.picklist(['development', 'production'])),
	baseUrl: v.pipe(
		v.string(),
		v.url('Base URL must be a valid URL'),
		v.minLength(1, 'Base URL cannot be empty')
	)
});

export type Env = v.InferOutput<typeof EnvSchema>;
