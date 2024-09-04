import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store/store'
import './assets/global.css'; 
import axios from './config/axiosConfig'

import tokenService from './store/token.service';


axios.defaults.baseURL = 'http://localhost:8082/api/v1/';


// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const vuetify = createVuetify ({
    components,
    directives,
    
    
});
if (store.getters.isAuthenticated) {
  tokenService.startTokenMonitoring();
}

const app = createApp(App);
app.use(router);
app.use(store);
app.use(vuetify);
app.config.globalProperties.$axios = axios;



app.mount('#app');
