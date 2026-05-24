<script lang="ts">
	import { Switch } from '@skeletonlabs/skeleton-svelte';

	type Props = {
		waitForAnimation: boolean;
	};
	let { waitForAnimation = $bindable() }: Props = $props();
</script>

<div class="rounded-lg p-4">
	<div class="grid w-full grid-cols-1 gap-4 md:grid-cols-[1fr_auto]">
		<!-- Left column -->
		<div class="flex flex-col gap-1">
			<label for="waitForAnimation" class="preset-typo-subtitle">Wait for animation</label>
			<p id="waitForAnimationHelp" class="preset-typo-caption">
				Adjust whether the board waits for animations to complete
			</p>
		</div>

		<!-- Right column -->
		<Switch
			checked={waitForAnimation}
			onCheckedChange={() => (waitForAnimation = !waitForAnimation)}
		>
			<Switch.Control id="waitForAnimation">
				<Switch.Thumb />
			</Switch.Control>
			<Switch.HiddenInput />
		</Switch>
	</div>

	<!-- Default row -->
	<div class="flex w-full justify-end">
		<button
			class="btn preset-tonal-tertiary btn-sm"
			type="button"
			onclick={() => (waitForAnimation = true)}
			disabled={waitForAnimation === true}
		>
			Default
		</button>
	</div>
</div>
