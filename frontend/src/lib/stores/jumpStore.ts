import { get, writable } from 'svelte/store';
import { currentGame } from '$lib/stores/currentGameStore';

export interface jumpState {
	latest: number;
	current: number;
	action: jumpAction;
}

export enum jumpAction {
	_init,
	reset,
	undo,
	redo,
	end,
	_tearDown
}

export const jump = writable<jumpState>({ latest: -1, current: -1, action: jumpAction._init });

export function init() {
	jump.set({
		latest: get(currentGame).game.moveIndex,
		current: get(currentGame).game.moveIndex,
		action: jumpAction._init
	});
}

export function reset() {
	if (!isInJump()) init();
	jump.update((state) => ({
		latest: state.latest,
		current: 0,
		action: jumpAction.reset
	}));
}

export function decrementJump() {
	if (!isInJump()) init();
	if (get(jump).current === 0) return;
	jump.update((state) => ({
		latest: state.latest,
		current: state.current - 1,
		action: jumpAction.undo
	}));
}

export function incrementJump() {
	if (!isInJump()) return;
	jump.update((state) => ({
		latest: state.latest,
		current: state.current + 1,
		action: jumpAction.redo
	}));
	if (get(jump).current === get(jump).latest) {
		tearDown();
	}
}

export function endJump() {
	if (!isInJump()) return;
	jump.update((state) => ({
		latest: state.latest,
		current: state.latest,
		action: jumpAction.end
	}));
	tearDown();
}

export function tearDown() {
	jump.set({ latest: -1, current: -1, action: jumpAction._tearDown });
}

export function isInJump() {
	return get(jump).latest !== -1;
}

export function isAtStart() {
	return get(jump).current === 0;
}
