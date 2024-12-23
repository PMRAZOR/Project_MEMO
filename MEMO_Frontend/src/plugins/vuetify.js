// Styles
import "@mdi/font/css/materialdesignicons.css";
import "@/styles/variables.css";
import "vuetify/styles";

// Vuetify
import { createVuetify } from "vuetify";
import { VBtn } from "vuetify/components";
import * as components from "vuetify/components";
import * as directives from "vuetify/directives";

export default createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: "dark",
    themes: {
      light: {
        colors: {
          appBarColor: "#1b1b1b",
        },
      },
      dark: {
        colors: {
          appBarColor: "#1b1b1b",
        },
      },
    },
  },
  aliases: {
    VBtnSecondary: VBtn,
    VBtnTertiary: VBtn,
    VBtnAppBar: VBtn,
    VBtnLogin: VBtn,
    VBtnRegist: VBtn,
  },
  defaults: {
    VBtn: {
      color: "black",
      variant: "plain",
    },
    VBtnAppBar: {
      color: "white",
      variant: "plain",
      size: "small",
    },
    VBtnLogin: {
      color: "white",
      variant: "elevated",
      size: "small",
      rounded: "xs",
    },
    VBtnRegist: {
      color: "grey-darken-1",
      variant: "elevated",
      size: "small",
      rounded: "xs",
    },
    VBtnSecondary: {
      color: "secondary",
      variant: "flat",
    },
    VBtnTertiary: {
      rounded: true,
      variant: "plain",
    },
  },
});
