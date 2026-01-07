import { defineConfig, presetAttributify, presetWind4, transformerVariantGroup } from 'unocss'

export default defineConfig({
  presets: [
    presetWind4(),
    presetAttributify(),
  ],
  transformers: [
    transformerVariantGroup(),
  ],
  theme: {
    fontFamily: {
      serif: ['Average', 'sans-serif'],
      sans: ['Average Sans', 'sans'],
    }
  }
})
