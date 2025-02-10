import { toast } from '@zerodevx/svelte-toast';
import ToastInfo from '$lib/components/toast/ToastInfo.svelte';
import ToastFailure from '$lib/components/toast/ToastFailure.svelte';

const settings = {
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
	dismissable: false
};

export function toastInfo(message: string, target: string) {
	toast.push({
		component: {
			// eslint-disable-next-line @typescript-eslint/ban-ts-comment
			// @ts-expect-error
			src: ToastInfo,
			props: {
				message: message
			},
			sendIdTo: 'toastId'
		},
		target: target,
		...settings
	});
}

export function toastFailure(message: string, target: string, query: string) {
	toast.push({
		component: {
			// eslint-disable-next-line @typescript-eslint/ban-ts-comment
			// @ts-expect-error
			src: ToastFailure,
			props: {
				message: message,
				query: query
			},
			sendIdTo: 'toastId'
		},
		target: target,
		...settings
	});
}
