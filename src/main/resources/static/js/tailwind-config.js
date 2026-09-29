tailwind.config = {
  theme: { extend: {
    colors: {
      surface: "#fcf9f3", "on-surface": "#1c1c18", "on-surface-variant": "#57423b", outline: "#8a726a",
      primary: "#9f3c16", "on-primary": "#ffffff", "primary-container": "#bf542c",
      "surface-container-lowest": "#ffffff", "surface-container-low": "#f6f3ed",
      "surface-container": "#f0eee8", "surface-container-high": "#ebe8e2", "surface-variant": "#e5e2dc",
      error: "#ba1a1a", "error-container": "#ffdad6", "on-error-container": "#93000a",
      tertiary: "#805200", "tertiary-fixed-dim": "#fcba5f", "secondary-fixed": "#d9e3f6", "on-secondary-fixed": "#121c2a"
    },
    spacing: { "margin-mobile": "1rem", "space-xs": "0.25rem", "space-sm": "0.5rem", "space-md": "1rem", "space-lg": "1.5rem" },
    fontFamily: { headline: ["Merriweather", "serif"], body: ["Plus Jakarta Sans", "sans-serif"] },
    fontSize: {
      "label-sm": ["10px", { lineHeight: "14px", letterSpacing: "0.04em", fontWeight: "600" }],
      "label-md": ["12px", { lineHeight: "16px", letterSpacing: "0.02em", fontWeight: "500" }],
      "label-lg": ["14px", { lineHeight: "20px", letterSpacing: "0.01em", fontWeight: "600" }],
      "body-sm": ["12px", { lineHeight: "18px" }], "body-md": ["14px", { lineHeight: "22px" }],
      "title-md": ["16px", { lineHeight: "24px", fontWeight: "600" }],
      "headline-sm": ["18px", { lineHeight: "26px", fontWeight: "700" }],
      "headline-lg": ["24px", { lineHeight: "32px", fontWeight: "700" }]
    },
    borderRadius: { DEFAULT: "0.25rem", lg: "0.5rem", xl: "0.75rem", full: "9999px" }
  } }
};