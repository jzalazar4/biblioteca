import axios from 'axios';
import store from './store';
import { jwtDecode } from 'jwt-decode';
//const API_URL = 'http://localhost:8082/api/v1/';
import moment from 'moment-timezone';

const tokenService = {
  monitorInterval: null, 

  startTokenMonitoring() {
    this.checkTokenExpiration(); // verifica el token cuando hace login
    // contador verifica cada minuto si expiro el token
    this.monitorInterval = setInterval(() => {
      this.checkTokenExpiration();
    }, 60 * 1000);
  },

  async checkTokenExpiration() {
    const token = store.state.token;
    const expiresAt = store.state.expiresAt;
    const currentTime = Math.floor(Date.now() / 1000); // hora actual en milisegundos

    if (expiresAt - currentTime <= 300) { // 5 antes de expirar pregunta al usuario de extender la sesion o no
      const response = window.confirm("Su sesión va a expirar en 5 minutos. ¿Desea extender la sesión?");
      if (response) {
        try {
          const res = await axios.post('http://localhost:8082/api/v1/refresh-token', {
            token: token
          }, {
            params: {
              token: token
            }
          });
          const newToken = res.data.token;
          const newDecoded = jwtDecode(newToken);
          
          // actualizo el token y la hora de expiracion
          store.commit('setToken', newToken);
          store.commit('updateExpiresAt', newDecoded.exp);

          console.log("Token actualizado");

          // vuelve a monitorear 
          this.restartTokenMonitoring();
        } catch (error) {
          console.error("Error al actualizar el token:", error);
          this.logoutUser();
        }
      } else {
        this.logoutUser();
      }
    }
  },

  async checkNewTokenExpiration() {
    const newExpiresAt = store.state.expiresAt;
    const currentTime = Math.floor(Date.now() / 1000);

    if (newExpiresAt - currentTime <= 3600) { // 1 hora antes de expirar
      try {
        const res = await axios.post('http://localhost:8082/api/v1/refresh-token', {
          token: store.state.token
        });
        const newToken = res.data.token;
        const newDecoded = jwtDecode(newToken);

        store.commit('setToken', newToken);
        store.commit('updateExpiresAt', newDecoded.exp);

        console.log("Token actualizado");

        // vuelve a monitorear pero ahora el token dura 1 hora
        this.restartTokenMonitoring();
      } catch (error) {
        console.error("Error al actualizar el token:", error);
        this.logoutUser();
      }
    }
  },

  restartTokenMonitoring() {
    clearInterval(this.monitorInterval); // borra el tmeporizador anterior
    this.startTokenMonitoring(); // inicia el nuevo temporizador pero con 1 hora de margen
  },

  logoutUser() {
    store.dispatch('logout');
    clearInterval(this.monitorInterval); // limpia el monitor cuando hace logout
  }

};
export default tokenService;
