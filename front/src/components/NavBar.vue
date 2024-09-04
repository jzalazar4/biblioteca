<template>
<head>
  <meta name="viewport" content=
          "width=device-width, initial-scale=1.0" />
          <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">

</head>
<body>
  <div class="nav">
  <input type="checkbox" id="nav-check">
  <div class="nav-header">
    <a href="/">
    <box-icon name='book-open' class="nav-logo"></box-icon>
  </a>
    <a href="/"  class="nav-title">Biblioteca</a>
  </div>
  <div class="nav-btn">
    <label for="nav-check">
      <span></span>
      <span></span>
      <span></span>
    </label>
  </div>
  
  <div class="nav-links">
  <a href="/">Inicio</a>
  <a href="/tablebooks">Catálogo</a>
  <div class="btn-group " v-if="!isAuthenticated">
   <router-link :to="'signin'">
    <button v-if="!isAuthenticated" type="button" class="btn btn-primary rounded btn-animate login mx-1 " 
            data-toggle="tooltip" data-placement="bottom" title="Login">
      Login
    </button>
   </router-link>
  <router-link :to="'signup'">
    <button v-if="!isAuthenticated" type="button" class="btn btn-success rounded btn-animate sign-up mx-1" 
            data-toggle="tooltip" data-placement="bottom" title="Sign Up">
      Sign Up
    </button>
  </router-link>
  </div>
  <!---si es usuario-->
 
    <div class="dropdown" v-if="!isAdmin && isAuthenticated">
      <a href="/userProfile" class="dropbtn" v-if="!isAdmin">Perfil</a>
      <div class="dropdown-content">
        <a href="/userProfile">Perfil</a>
        <a href="/userLibros">Mis libros</a>
        <a href="javascript:void(0)" @click.prevent="logout" v-if="!isAdmin">Cerrar sesión</a>
      </div>
    </div>

  <!--si es admin-->
  <a href="/main" v-if="isAdmin">Admin panel</a>

</div>

</div>
</body>  
</template>
  
<script>
import store from '@/store/store';
import { BoxIconElement } from 'boxicons';

export default {
  name: 'NavBar',
  computed: {
    isAuthenticated() {
      return store.getters.isAuthenticated;
    },
    user() {
      return store.getters.user;
    },
    isAdmin() {
      return store.getters.isAdmin;
    },
 
  },

  methods: {
    async logout() {
      store.dispatch('logout');
      this.$router.push('/');
     
    },

    responsiveMenu() {
      var x = document.getElementById("navigation");
      if (x.className === "navigation") {
        x.className += " responsive";
      } else {
        x.className = "navigation";
      }
    },
 
  },
};
</script>
  
<style scoped>

/*nueva navbar*/
* {
  box-sizing: border-box;
}

body {
  margin: 0px;
  font-family: 'Roboto', sans-serif;
}

.nav {
  height: auto;
  width: 100%;
  background-color: #fcf6f6;
  position: relative;
  display: flex;
  font-family: 'Roboto', sans-serif;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.nav-logo {
  width: 33px; 
  height: 35px;
  margin-left: 10px;
  padding: 1px;
  margin-bottom: 4px;
  vertical-align: middle;
  margin-right: 2px;
}

.nav-title {
  font-size: 27px;
  font-weight: bold;
  color: #201e1e;
  text-decoration: none;
  vertical-align: middle;
}

.nav-header {
  display: flex;
  align-items: center;
}

.nav-btn {
  display: none;
}

.nav-links {
  display: flex;
  align-items: right;
  font-size: 16px;
  z-index: 1; 

}

.nav-links a {
  display: inline-block;
  padding: 13px 10px;
  text-decoration: none;
  color: #201e1e;
  align-items: right;

}

.nav-links a:hover {
  background-color: rgba(175, 167, 167, 0.3);
}

.nav-links .btn-group :hover {
  background-color: #fcf6f6;
  color: #201e1e;
}

.btn-group a {
  display: flex;
  padding: 4.5px;
  align-items: right;
  
}

.btn-group button {
  border-radius: 4px;
  height: 40px;
  transition: background-color 0.3s ease-in-out, color 0.3s ease-in-out;
}

#nav-check {
  display: none;
}

@media (max-width:600px) {
  .nav-btn {
    display: inline-block;
    position: absolute;
    right: 0px;
    top: 0px;
  }
  
  .nav-btn label {
    display: inline-block;
    width: 50px;
    height: 50px;
    padding: 13px;
  }
  
  .nav-btn label:hover,
  #nav-check:checked ~ .nav-btn label  {
    background-color: rgba(0, 0, 0, 0.3);
  }
  
  .nav-btn label span {
    display: block;
    width: 25px;
    height: 10px;
    border-top: 2px solid #eee;
  }
  
  .nav-links {
    position: absolute;
    display: block;
    width: 100%;
    background-color: #333;
    height: 0px;
    transition: all 0.3s ease-in;
    overflow-y: hidden;
    top: 50px;
    left: 0px;
  }
  
  .nav-links a {
    display: block;
    width: 100%;
  }
  
  #nav-check:not(:checked) ~ .nav-links {
    height: 0px;
  }
  
  #nav-check:checked ~ .nav-links {
    height: calc(100vh - 50px);
    overflow-y: auto;
  }
 
}
.dropdown {
  position: relative;
}

.dropdown .dropbtn {
  display: flex;
  color: #505050;
  text-align: center;
  text-decoration: none;
}

.dropdown .dropdown-content {
  display: none;
  position: absolute;
  background-color: #f9f9f9;
  min-width: 160px;
  box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
  z-index: 1;
}

.dropdown:hover .dropdown-content {
  display: block;
}

.dropdown-content a {
  color: black;
  padding: 12px 16px;
  text-decoration: none;
  display: block;
  text-align: left;
}
.dropdown-content {

  right: 0; 
}
.dropdown-content a:hover {
  background-color: #DDA15E;
}
/** */
</style>