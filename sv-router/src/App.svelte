<script lang="ts">
	import { Router } from 'sv-router';
	import { p } from './router';
</script>

<a href={p('/')}>Home</a>
<a href={p('/puzzle')}>Puzzle</a>
<a href={p('/design')}>Design</a>

<Router />
