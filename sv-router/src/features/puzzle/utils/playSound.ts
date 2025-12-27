import move_mp3 from '../assets/Move.mp3';
import capture_mp3 from '../assets/Capture.mp3';

export function playSound(isCapture: boolean) {
	if (isCapture) playCaptureSound();
	else playMoveSound();
}

function playMoveSound() {
	const audio = new Audio(move_mp3);
	audio.play().catch();
}

function playCaptureSound() {
	const audio = new Audio(capture_mp3);
	audio.play().catch();
}
