import { get, writable } from 'svelte/store';
import { currentGame } from '$lib/stores/currentGameStore';

export interface jumpState {
	latest: number;
	current: number;
	action: 'undo' | 'redo' | 'init' | 'teardown';
}

export const jump = writable<jumpState>({ latest: -1, current: -1, action: 'init' });

export function init() {
	jump.set({
		latest: get(currentGame).game.moveIndex,
		current: get(currentGame).game.moveIndex,
		action: 'init'
	});
}

export function tearDown() {
	jump.set({ latest: -1, current: -1, action: 'teardown' });
}

export function decrementJump() {
	if (!isInJump()) init();
	if (get(jump).current === 0) return;
	jump.update((state) => ({
		latest: state.latest,
		current: state.current - 1,
		action: 'undo'
	}));
}

export function incrementJump() {
	if (!isInJump()) return;
	jump.update((state) => ({
		latest: state.latest,
		current: state.current + 1,
		action: 'redo'
	}));
	if (get(jump).current === get(jump).latest) {
		tearDown();
	}
}

export function isInJump() {
	return get(jump).latest !== -1;
}
