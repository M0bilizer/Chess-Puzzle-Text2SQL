import { PersistedState } from 'runed';

export type Preferences = {
	theme: 'dark' | 'light';
	flipOrientation: boolean;
	computerMoveDelay: number;
	muted: boolean;
};
export const preferencesState = new PersistedState<Preferences>('preferences', {
	theme: 'dark',
	flipOrientation: false,
	computerMoveDelay: 250,
	muted: false
});
