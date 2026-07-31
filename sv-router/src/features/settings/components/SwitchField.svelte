<script lang="ts">
	import { Switch } from '@skeletonlabs/skeleton-svelte';

	import DefaultButton from './DefaultButton.svelte';

	type Props = {
		title: string;
		helptext: string;
		name: string;
		value: boolean;
		defaultValue: boolean;
	};
	let { title, helptext, name, value = $bindable(), defaultValue }: Props = $props();
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
	<Switch checked={value} onCheckedChange={() => (value = !value)}>
		<Switch.Control id={name}>
			<Switch.Thumb />
		</Switch.Control>
		<Switch.HiddenInput />
	</Switch>
</div>
