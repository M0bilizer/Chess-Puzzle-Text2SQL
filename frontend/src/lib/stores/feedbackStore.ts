import { writable } from 'svelte/store';

export enum feedbackState {
	white,
	black,
	wrong,
	correct,
	won
}

export const feedbackStore = writable<feedbackState>(feedbackState.white);
