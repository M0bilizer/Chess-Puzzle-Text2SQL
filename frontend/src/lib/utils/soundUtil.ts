import { Sound } from 'svelte-sound';
import move_mp3 from '$assets/Move.mp3';
import capture_mp3 from '$assets/Capture.mp3';

const move_sound = new Sound(move_mp3);
const capture_sound = new Sound(capture_mp3);

export function playCaptureSound() {
	capture_sound.play();
}

export function playMoveSound() {
	move_sound.play();
}
