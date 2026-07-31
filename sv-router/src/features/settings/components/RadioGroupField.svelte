<script lang="ts" generics="T">
	import DefaultButton from './DefaultButton.svelte';

	type Props<T> = {
		title: string;
		helptext: string;
		name: string;
		value: T;
		options: { label: string; helptext?: string; value: T }[];
		defaultValue: T;
	};
	let { title, helptext, name, value = $bindable(), options, defaultValue }: Props<T> = $props();
</script>

<div class="grid w-full grid-cols-1 gap-4 md:grid-cols-[1fr_auto]">
	<!-- Left column -->
	<div class="flex flex-col gap-1">
		<div class="flex items-center">
			<label for={name} class="preset-typo-subtitle">{title}</label>
			<DefaultButton onclick={() => (value = defaultValue)} disabled={value === defaultValue} />
		</div>
		<p>{helptext}</p>
	</div>

	<!-- Right column -->
	<div
		class="flex flex-wrap items-center gap-4 md:justify-end"
		role="radiogroup"
		aria-labelledby={name}
	>
		{#each options as option (option)}
			<label class="flex items-center gap-1">
				<input type="radio" class="radio" {name} value={option.value} bind:group={value} />
				<span>{option.label}</span>
			</label>
		{/each}
	</div>
</div>
