import { PersistedState } from 'runed';

export type Preferences = {
	theme: 'dark' | 'light';
	flipOrientation: boolean;
	computerMoveDelay: number;
	waitForAnimation: boolean;
	animationSpeed: number;
	muted: boolean;
};
export const preferencesState = new PersistedState<Preferences>('preferences', {
	theme: 'dark',
	flipOrientation: false,
	computerMoveDelay: 0,
	waitForAnimation: true,
	animationSpeed: 200,
	muted: false
});
