<script lang="ts">
	type Props = {
		computerMoveDelay: number;
	};
	let { computerMoveDelay = $bindable() }: Props = $props();
</script>

<div class="rounded-lg p-4">
	<div class="grid w-full grid-cols-1 gap-4 md:grid-cols-[1fr_auto]">
		<!-- Left column -->
		<div class="flex flex-col gap-1">
			<label for="computerMoveDelay" class="preset-typo-subtitle">Computer Move Delay</label>
			<p id="computerMoveDelayHelp" class="preset-typo-caption">
				Adjust the visual pause between computer moves
			</p>
		</div>

		<!-- Right column -->
		<div
			class="flex flex-wrap items-center gap-4 md:justify-end"
			role="radiogroup"
			aria-labelledby="computerMoveDelay"
			aria-describedby="computerMoveDelayHelp"
		>
			<label class="flex items-center gap-1">
				<input
					type="radio"
					class="radio"
					name="computerMoveDelay"
					value={0}
					bind:group={computerMoveDelay}
				/>
				<span>None</span>
			</label>
			<label class="flex items-center gap-1">
				<input
					type="radio"
					class="radio"
					name="computerMoveDelay"
					value={64}
					bind:group={computerMoveDelay}
				/>
				<span>Quick</span>
			</label>
			<label class="flex items-center gap-1">
				<input
					type="radio"
					class="radio"
					name="computerMoveDelay"
					value={128}
					bind:group={computerMoveDelay}
				/>
				<span>Medium</span>
			</label>
			<label class="flex items-center gap-1">
				<input
					type="radio"
					class="radio"
					name="computerMoveDelay"
					value={512}
					bind:group={computerMoveDelay}
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
			onclick={() => (computerMoveDelay = 64)}
			disabled={computerMoveDelay === 64}
		>
			Default
		</button>
	</div>
</div>
