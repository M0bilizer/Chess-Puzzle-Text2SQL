import { ModelEnum } from '$lib/enums/modelEnum';

export type SearchMetadata = {
	query: string;
	model: ModelEnum | null;
	maskedQuery: string | null;
	sql: string | null;
};
