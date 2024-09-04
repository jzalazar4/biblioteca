import router from '../router';
import Vuex from 'vuex';
import createPersistedState from 'vuex-persistedstate';
import axios from 'axios';
import moment from 'moment';

const API_URL = 'http://localhost:8082/api/v1/';

const store = new Vuex.Store({
  plugins: [createPersistedState()],
  state: {
    isAuthenticated: false,
    responseLogin: '',
    error: '',
    user: null,
    id: null,
    token: localStorage.getItem('token') || '',
    isAdmin: false,
    isUser: false, 
    verPag: false,
    logged: false,
    expiresAt: localStorage.getItem('expiresAt') || '',
    refreshToken: localStorage.getItem('refreshToken') || '',
  },
  getters: {
    isAuthenticated: state => state.isAuthenticated,
    responseLogin: state => state.responseLogin,
    error: state => state.error,
    user: state => state.user,
    isAdmin: (state) => state.user && state.user.role === 'ADMIN',

    //isAdmin: state => state.user.role === 'ADMIN',
    idUser: state => state.user.id,
    isUser: state => state.isUser,
    verPag: state => state.verPag,
    userLogged: (state) => state.user.logged,
    expiresAt: state => state.token.expiresAt,
  },
  mutations: {
    updateExpiresAt(state, expiresAt) {
      state.expiresAt = expiresAt;
      localStorage.setItem('expiresAt', expiresAt);
    },
    resetUser(state) {
      state.user = {};
      state.isAuthenticated = false;
      state.isAdmin = false;
      state.isUser = false;
    },
    setAuthentication(state, status) {
    state.isAuthenticated = status;
    },
    setUser(state, user) {
      if (user) {
        user.isAdmin = user.role === "ADMIN";
        user.isUser = user.role === "USER";
        console.log(user.role);
      }
      state.user = user;
    },
    updateUser(state, user) {
    state.user = user;
    },
    setResponseLogin(state, value) {
    state.responseLogin = value;
    },
    setUserInLocalStorage(state, value) {
    localStorage.setItem('userLog', JSON.stringify(value));
    },
    setError(state, error) {
    state.error = error;
    },
    setToken(state, token) {
    state.token = token;

    localStorage.setItem('token', token);
    },
    setExpiresAt(state, expiresAt) {
      state.expiresAt = expiresAt;
      localStorage.setItem('expiresAt', expiresAt);
    },
    clearToken(state) {
    state.token = '';
    localStorage.removeItem('token');
    delete axios.defaults.headers.common['Authorization'];

    },
    setRefreshToken(state, refreshToken) {
    state.refreshToken = refreshToken;
    localStorage.setItem('refreshToken', refreshToken);
    },
    clearRefreshToken(state) {
    state.refreshToken = '';
    localStorage.removeItem('refreshToken');
    },
    clearUser(state) {
    state.user = null;
    localStorage.removeItem('user');
    },
    setIsAdmin(state, status) {
    state.isAdmin = status;
    },
    setViewingAsUser(state, status) {
    state.isViewingAsUser = status;
    },
    setIsUser(state, status) {
    state.isUser = status;
    },
    setUserLogged(state, status) {
    state.user.logged = status;
    },

  },
  actions: {
    async login({ commit }, userData) {
      try {
        const response = await axios.post(`${API_URL}login`, userData);
        const { token, refreshToken, user } = response.data;
        commit('setUser', user);
        commit('setAuthentication', true);
        commit('setToken', token);
        commit('setRefreshToken', refreshToken);
        commit('setUserLogged', true);
        const expiresAt = moment().add(15, 'minutes').unix();
           // Guardar token en localStorage
        localStorage.setItem('token', token);
        commit('setExpiresAt', expiresAt);
        localStorage.setItem('refreshToken', refreshToken);
        
        if (user.role === "ADMIN") {
        //  localStorage.setItem('isAdmin', true);
       //   commit('isAdming', user);
          router.push("/main");
        } else {
          router.push("/");
        }
        return true;

      } catch (error) {
        commit('setError', 'Email o contraseña incorrectos');
        if (error.response) {
          if (error.response.status === 403) {
            commit('setError', "Cuenta no verificada. Revise su email");
          } else if (error.response.status === 401) {
            commit('setError', "Email o contraseña incorrectos");
          } else {
            commit('setError', "Ha ocurrido un error");
          }
        } else {
          commit('setError', "Ha ocurrido un error");
        }
        return false;

      }
  
    },

  async refreshToken({ commit, state }) {
  try {
      const response = await axios.post(`${API_URL}refresh-token`, {
        refreshToken: state.refreshToken,
      });
      const { token, refreshToken } = response.data;
      commit('setToken', token);
      commit('setRefreshToken', refreshToken);
      const expiresAt = moment().add(1, 'hour').unix();
      commit('updateExpiresAt', expiresAt); // se actualiza la expiracion
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } catch (error){
        commit('setAuthentication', false);
        commit('clearToken');
        commit('clearRefreshToken');
        commit('setUser', null);
        console.error('No se pudo actualizar el token', error);
    }
    },
    async getUser({ commit, state }) {
      try {
        const token = localStorage.getItem('token');
        const userId = state.user?.id;

        if (!userId) {
          throw new Error("User ID no definido.");
        }

        const response = await axios.get(`${API_URL}user/${userId}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
      //nuevo
      const user = response.data; 
        user.isAdmin = user.role === "ADMIN"; 
        commit('setUser', user); 
          // commit('setUser', response.data);
          } catch (error) {
            console.error(error);
          }
        },
    logout({ commit }) {
    commit('resetUser', true);
    commit('setAuthentication', false);
    commit('clearToken');
    commit('clearRefreshToken');
    router.push('/');
    },
    },
  }
);
export default store;