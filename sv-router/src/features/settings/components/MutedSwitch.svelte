<script lang="ts">
	import { Switch } from '@skeletonlabs/skeleton-svelte';

	type Props = {
		muted: boolean;
	};
	let { muted = $bindable() }: Props = $props();
</script>

<div class="rounded-lg p-4">
	<div class="grid w-full grid-cols-1 gap-4 md:grid-cols-[1fr_auto]">
		<!-- Left column -->
		<div class="flex flex-col gap-1">
			<label for="muted" class="preset-typo-subtitle">Mute Sound</label>
			<p id="mutedHelp" class="preset-typo-caption">
				Adjust whether sound effects and audio are enabled
			</p>
		</div>

		<!-- Right column -->
		<Switch checked={muted} onCheckedChange={() => (muted = !muted)}>
			<Switch.Control id="muted">
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
			onclick={() => (muted = false)}
			disabled={muted === false}
		>
			Default
		</button>
	</div>
</div>
