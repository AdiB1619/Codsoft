/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{js,jsx}',
  ],
  theme: {
    extend: {
      // =========================================================================
      // Color Palette — SDD Section 9.2
      // =========================================================================
      colors: {
        primary: {
          DEFAULT: '#1E3A5F',  // deep navy — buttons, navbar, headings, active nav
          hover:   '#2C4F7C',  // hover/focus state for primary elements
        },
        accent: {
          DEFAULT: '#0D9488',  // teal — links, secondary buttons, active filters
          gold:    '#C9A227',  // sparing use — logo mark, achievement highlights
        },
        success: '#16A34A',    // success toasts, ACTIVE status badge
        warning: '#D97706',    // warning toasts, SUSPENDED status badge
        danger:  '#DC2626',    // destructive actions, error toasts, validation errors
        // neutral-* maps to Tailwind's built-in slate scale (no override needed)
      },

      // =========================================================================
      // Typography — SDD Section 9.3
      // Fonts are loaded via @import in index.css; declared here for Tailwind utils
      // =========================================================================
      fontFamily: {
        heading: ['Sora', 'sans-serif'],
        body:    ['Lexend', 'sans-serif'],
        mono:    ['JetBrains Mono', 'monospace'],
      },

      // =========================================================================
      // Max-width cap — SDD Section 9.5
      // =========================================================================
      maxWidth: {
        content: '1280px',
      },

      // =========================================================================
      // Motion — SDD Section 9.8
      // Transitions kept short and purposeful
      // =========================================================================
      transitionDuration: {
        hover:  '150',   // hover state color/shadow
        modal:  '150',   // modal open/close
        toast:  '200',   // toast slide-in/fade
      },
    },
  },
  plugins: [],
}
