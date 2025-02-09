import { toast } from '@zerodevx/svelte-toast';
import ToastComponent from '$lib/components/ToastComponent.svelte';

export function toastUtil(message: string, target: string) {
	toast.push({
		component: {
			src: ToastComponent,
			props: {
				message: message
			},
			sendIdTo: 'toastId'
		},
		theme: {
			'--toastPadding': '0',
			'--toastMsgPadding': '0',
			'--toastBackground': 'transparent',
			'--toastColor': 'transparent',
			'--toastBoxShadow': 'none',
			'--toastBorder': 'none',
			'--toastBarBackground': 'transparent',
			'--toastBtnContent': '',
			'--toastBarHeight': 0
		},
		initial: 0,
		dismissable: false,
		target: target
	});
}
