import move_mp3 from '$assets/Move.mp3';
import capture_mp3 from '$assets/Capture.mp3';

export function playMoveSound() {
	const audio = new Audio(move_mp3); // Create a new Audio object each time
	audio.play().catch();
}

export function playCaptureSound() {
	const audio = new Audio(capture_mp3); // Create a new Audio object each time
	audio.play().catch();
}
