<script lang="ts">
	type Props = {
		animationSpeed: number;
	};
	let { animationSpeed = $bindable() }: Props = $props();
</script>

<div class="rounded-lg p-4">
	<div class="grid w-full grid-cols-1 gap-4 md:grid-cols-[1fr_auto]">
		<!-- Left column -->
		<div class="flex flex-col gap-1">
			<label for="animationSpeed" class="preset-typo-subtitle">Animation Speed</label>
			<p id="animationSpeedHelp" class="preset-typo-caption">
				Adjust the pace of all interface animations and transitions.
			</p>
		</div>

		<!-- Right column -->
		<div
			class="flex flex-wrap items-center gap-4 md:justify-end"
			role="radiogroup"
			aria-labelledby="animationSpeed"
			aria-describedby="animationSpeedHelp"
		>
			<label class="flex items-center gap-1">
				<input
					type="radio"
					class="radio"
					name="animationSpeed"
					value={0}
					bind:group={animationSpeed}
				/>
				<span>None</span>
			</label>
			<label class="flex items-center gap-1">
				<input
					type="radio"
					class="radio"
					name="animationSpeed"
					value={100}
					bind:group={animationSpeed}
				/>
				<span>Quick</span>
			</label>
			<label class="flex items-center gap-1">
				<input
					type="radio"
					class="radio"
					name="animationSpeed"
					value={200}
					bind:group={animationSpeed}
				/>
				<span>Medium</span>
			</label>
			<label class="flex items-center gap-1">
				<input
					type="radio"
					class="radio"
					name="animationSpeed"
					value={400}
					bind:group={animationSpeed}
				/>
				<span>Slow</span>
			</label>
		</div>
	</div>

	<!-- Default row -->
	<div class="flex w-full justify-end">
		<button
			class="btn preset-tonal-tertiary btn-sm"
			type="button"
			onclick={() => (animationSpeed = 200)}
			disabled={animationSpeed === 200}
		>
			Default
		</button>
	</div>
</div>
