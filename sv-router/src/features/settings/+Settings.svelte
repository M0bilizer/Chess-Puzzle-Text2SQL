<script lang="ts">
	import ContentPage from '@/common/components/ContentPage.svelte';

	import RadioGroupField from './components/RadioGroupField.svelte';
	import SwitchField from './components/SwitchField.svelte';
	import { preferencesState } from './preferences-state';

	const preferences = preferencesState.current;
	let enableSound = $state(!preferences.muted);
	let interruptAnimation = $state(!preferences.waitForAnimation);

	$effect(() => {
		preferences.muted = !enableSound;
	});

	$effect(() => {
		preferences.waitForAnimation = !interruptAnimation;
	});
</script>

<ContentPage class="space-y-6">
	<header class="space-y-4">
		<h1 class="text-left h1">Settings</h1>
		<p>Adjust your preferences</p>
	</header>
	<hr class="hr border-surface-200-800" />
	<fieldset class="space-y-12 p-4">
		<SwitchField
			title="Flip Orientation"
			helptext="Switches the board to show the view from the opposite color's side."
			name="flipOrientation"
			bind:value={preferences.flipOrientation}
			defaultValue={false}
		/>
		<SwitchField
			title="Enable Sound"
			helptext="Plays sound effects for piece movement and captures."
			name="enableSound"
			bind:value={enableSound}
			defaultValue={true}
		/>
		<RadioGroupField
			title="Next Move Delay"
			helptext="Pause before the next move appears. Recommended: 64ms or higher."
			name="computerMoveDelay"
			bind:value={preferences.computerMoveDelay}
			options={[
				{ value: 0, label: 'Immediate' },
				{ value: 64, label: '64ms' },
				{ value: 128, label: '128ms' },
				{ value: 512, label: '512ms' }
			]}
			defaultValue={64}
		/>
		<SwitchField
			title="Skip On-Going Animations On New Moves"
			helptext="When you make a move during an ongoing animation, the current animation cancels and your new move plays instantly."
			name="interruptAnimation"
			bind:value={interruptAnimation}
			defaultValue={false}
		/>
		<RadioGroupField
			title="Animation Speed"
			helptext="How fast pieces move on the board."
			name="animationSpeed"
			bind:value={preferences.animationSpeed}
			options={[
				{ value: 0, label: 'Instant' },
				{ value: 100, label: 'Quick' },
				{ value: 200, label: 'Medium' },
				{ value: 400, label: 'Slow' }
			]}
			defaultValue={200}
		/>
	</fieldset>
</ContentPage>
