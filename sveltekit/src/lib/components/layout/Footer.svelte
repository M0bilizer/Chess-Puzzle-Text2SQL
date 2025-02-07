<script lang="ts">
	const leftFooterData = [
		{ left: 'Author:', right: 'Raki' },
		{ left: 'SID:', right: '57140124' },
		{ left: 'Organization:', right: 'City University of Hong Kong' }
	];
	const link = 'https://github.com/M0bilizer/Chess-Puzzle-Text2SQL';
</script>

<footer class="border-t-[1px] border-surface-500/20 bg-surface-50-950 sm:p-10">
	<div class="container mx-auto flex flex-col items-center justify-between sm:flex-row">
		<div class="left">
			<table class="table">
				<tbody>
					{#each leftFooterData as row}
						<tr class="my-0 border-none py-0">
							<td class="!py-0 text-right text-sm text-primary-500">
								<span>{row.left} </span>
							</td>
							<td class="!py-0">
								<span>{row.right}</span>
							</td>
						</tr>
					{/each}
				</tbody>
			</table>
		</div>
		<div class="right">
			<a href={link} class="text-sm text-primary-500 hover:text-primary-700 hover:underline"
				>View source code</a
			>
		</div>
	</div>
</footer>

<style>
	.left {
		display: flex;
		align-items: center;
		gap: 1.5rem;
	}

	.right {
		display: flex;
		align-items: center;
	}
</style>
