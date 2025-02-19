<script lang="ts">
	import Fa6SolidChess from 'virtual:icons/fa6-solid/chess';
	import { clearCurrentGame } from '$lib/stores/currentGameStore';
	import { tearDown } from '$lib/stores/jumpStore';
	import { saveGame } from '$lib/utils/storeUtils';

	function clearGame() {
		saveGame();
		tearDown();
		clearCurrentGame();
	}
</script>

<button on:click={() => clearGame()} class="flex flex-col items-center justify-center gap-0">
	<Fa6SolidChess class="size-6" />
	<span class="text-xs font-bold tracking-tighter">Text2SQL</span>
</button>
