<script lang="ts">
	import type { HTMLAnchorAttributes, HTMLButtonAttributes } from 'svelte/elements';

	type Props = (Omit<HTMLAnchorAttributes, 'href'> & HTMLButtonAttributes) & {
		href: string | undefined;
	};

	let { href, children, ...rest }: Props = $props();

	let tag = $derived(href ? 'a' : 'button');

	let elementProps = $derived({
		href,
		disabled: !href ? true : undefined,
		...rest
	});
</script>

<svelte:element this={tag as string} {...elementProps}>
	{@render children?.()}
</svelte:element>
