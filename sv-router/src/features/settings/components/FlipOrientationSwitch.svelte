<script lang="ts">
	import { Switch } from '@skeletonlabs/skeleton-svelte';

	type Props = {
		flipOrientation: boolean;
	};
	let { flipOrientation = $bindable() }: Props = $props();
</script>

<div class="rounded-lg p-4">
	<div class="grid w-full grid-cols-1 gap-4 md:grid-cols-[1fr_auto]">
		<!-- Left column -->
		<div class="flex flex-col gap-1">
			<label for="flipOrientation" class="preset-typo-subtitle">Flip Orientation</label>
			<p id="flipOrientationHelp" class="preset-typo-caption">
				Adjust if the board flips to show the opposite perspective
			</p>
		</div>

		<!-- Right column -->
		<Switch checked={flipOrientation} onCheckedChange={() => (flipOrientation = !flipOrientation)}>
			<Switch.Control id="flipOrientation">
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
			onclick={() => (flipOrientation = false)}
			disabled={flipOrientation === false}
		>
			Default
		</button>
	</div>
</div>
