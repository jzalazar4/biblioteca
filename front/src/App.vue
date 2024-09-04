<template>
  <div id="app">
     <router-view> </router-view>

  </div>
</template>

<script>

import store from './store/store';
import tokenService from './store/token.service';


export default {
  name: 'App',
  components: {
  },
  data() {
    return {
    
    };
  },

  computed: {
    isUser() {
      return store.getters.isAuthenticated && !store.getters.isAdmin;
    },
    isAdmin() {
      return store.getters.isAuthenticated && store.getters.isAdmin;
    },
    user() {
      return store.getters.user;
    },

  },
  mounted() {
  },
  methods: {
  }
};
</script>
<style scoped>
.header {
  width: 100%;
  height: 45px; 
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 1rem;
  background-color: #f8f3d3;
  z-index: 1000;
  margin: 0;
}
</style>
