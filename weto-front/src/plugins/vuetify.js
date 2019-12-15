import Vue from 'vue'
import Vuetify from 'vuetify'
import 'vuetify/dist/vuetify.min.css'

const opts = {
  theme: {
    dark: false,
    themes: {
      light: {
        primary: '#4e008e',
        secondary: '#3a4b54',
        accent: '#102027',
        info: '#c3b9d7',
        success: '#7dcdbe',
        warning: '#ffdca5',
        error: '#ff6e89',
        grey: '#c8c8c8',
        blue: '#82c8f0',
        pink: '#f5a5c8'
      }
    }
  },
  icons: {
    iconfont: 'mdi'
  }
}
Vue.use(Vuetify)

export default new Vuetify(opts)