class ThemeStore {
    private initialMode = $state();
    isDarkMode = $derived(this.initialMode === 'dark');

    constructor(theme: "light" | "dark") {
      this.initialMode = theme;
    }

    setTheme(isDark: boolean) {
        const mode = isDark ? 'dark' : 'light';
        document.documentElement.setAttribute('data-mode', mode);
        localStorage.setItem('mode', mode);
        this.initialMode = mode;
    }
}

export const theme = new ThemeStore(localStorage.getItem('mode') as "light" | "dark" || 'dark');
