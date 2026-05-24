<script lang="ts">
	import MainOnlyPage from '@/common/components/MainOnlyPage.svelte';
	import { preferencesState } from './preferences-state';
	import ComputerMoveDelayRadio from './components/ComputerMoveDelayRadio.svelte';
	import AnimationSpeedRadio from './components/AnimationSpeedRadio.svelte';
	import MutedSwitch from './components/MutedSwitch.svelte';
	import WaitForAnimationSwitch from './components/WaitForAnimationSwitch.svelte';
	import FlipOrientationSwitch from './components/FlipOrientationSwitch.svelte';

	const preferences = preferencesState.current;
</script>

<MainOnlyPage>
	<main class="space-y-6 pt-12">
		<header class="space-y-4">
			<h1 class="text-left h1">Settings</h1>
			<p>Adjust your preferences</p>
		</header>
		<hr class="hr border-surface-200-800" />
		<fieldset class="space-y-4">
			<FlipOrientationSwitch bind:flipOrientation={preferences.flipOrientation} />
			<MutedSwitch bind:muted={preferences.muted} />
			<ComputerMoveDelayRadio bind:computerMoveDelay={preferences.computerMoveDelay} />
			<WaitForAnimationSwitch bind:waitForAnimation={preferences.waitForAnimation} />
			<AnimationSpeedRadio bind:animationSpeed={preferences.animationSpeed} />
		</fieldset>
	</main>
</MainOnlyPage>
