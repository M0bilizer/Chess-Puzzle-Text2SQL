import { ModelEnum } from '$lib/enums/modelEnum';

export type SearchMetadata = {
	query: string;
	model: ModelEnum | null;
	sql: string | null;
};
