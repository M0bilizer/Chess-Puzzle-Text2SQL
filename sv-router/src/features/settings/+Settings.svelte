<script lang="ts">
	import { preferencesState } from './preferences-state';
	import ComputerMoveDelayRadio from './components/ComputerMoveDelayRadio.svelte';
	import AnimationSpeedRadio from './components/AnimationSpeedRadio.svelte';
	import MutedSwitch from './components/MutedSwitch.svelte';
	import WaitForAnimationSwitch from './components/WaitForAnimationSwitch.svelte';
	import FlipOrientationSwitch from './components/FlipOrientationSwitch.svelte';
	import ContentPage from '@/common/components/ContentPage.svelte';

	const preferences = preferencesState.current;
</script>

<ContentPage class="space-y-6">
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
</ContentPage>
