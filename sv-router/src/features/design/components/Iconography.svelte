<script lang="ts">
	import TablerChessQueenFilled from '~icons/tabler/chess-queen-filled';
	import TablerArrowRight from '~icons/tabler/arrow-right';
	import TablerHeart from '~icons/tabler/heart';
	import TablerCheck from '~icons/tabler/check';
	import TablerMenu2 from '~icons/tabler/menu-2';
	import TablerCalendar from '~icons/tabler/calendar';
	import TablerSearch from '~icons/tabler/search';
	import TablerUserCircle from '~icons/tabler/user-circle';
	import { AppBar } from '@skeletonlabs/skeleton-svelte';
</script>

<div class="flex flex-col gap-4">
	<h2 class="font-extrabold">Tabler icon is used for iconography</h2>

	<div class="grid grid-cols-[2fr_1fr_2fr] gap-6">
		<!-- Queen -->
		<div
			class="block max-w-md divide-y divide-surface-200-800 overflow-hidden card border border-surface-200-800 preset-filled-surface-100-900 card-hover"
		>
			<header class="mb-3 flex items-center justify-start gap-2 p-4">
				<TablerChessQueenFilled class="text-2xl text-primary-300" />
				<h4 class="h4">Queen</h4>
			</header>
			<ul class="space-y-1.5 p-4 text-sm">
				<li class="flex items-start">
					<span>Most powerful piece - combines rook and bishop movement</span>
				</li>
				<li class="flex items-start">
					<span>Each player starts with one queen</span>
				</li>
				<li class="flex items-start">
					<span>Worth approximately 9 pawns</span>
				</li>
			</ul>
		</div>

		<div class="flex items-center gap-4">
			<button type="button" class="btn preset-filled">
				<span>Button</span>
				<TablerArrowRight width="18" />
			</button>
			<button type="button" class="btn-icon preset-filled">
				<TablerHeart size={16} />
			</button>
			<button type="button" class="chip-icon preset-filled">
				<TablerCheck size={16} />
			</button>
		</div>

		<AppBar>
			<AppBar.Toolbar class="grid-cols-[auto_1fr_auto]">
				<AppBar.Lead>
					<button type="button" class="btn-icon btn-icon-lg hover:preset-tonal">
						<TablerMenu2 class="size-6" />
					</button>
				</AppBar.Lead>
				<AppBar.Headline>
					<p class="font-serif text-2xl">Puzzle</p>
				</AppBar.Headline>
				<AppBar.Trail>
					<button type="button" class="btn-icon hover:preset-tonal">
						<TablerSearch class="size-6" />
					</button>
					<button type="button" class="btn-icon hover:preset-tonal">
						<TablerCalendar class="size-6" />
					</button>
					<button type="button" class="btn-icon hover:preset-tonal">
						<TablerUserCircle class="size-6" />
					</button>
				</AppBar.Trail>
			</AppBar.Toolbar>
		</AppBar>
	</div>
</div>
