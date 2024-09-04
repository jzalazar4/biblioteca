<template>
    <div>
  
    <div id="container">
      <div id="wrapper" class="background-image">
        <div id="myModal" class="modal show">
          <div class="modal-dialog modal-login">
            <div class="modal-content">
              <div class="modal-header">
                <h4 class="modal-title">Reestablecer cuenta</h4>
              </div>
              <div class="modal-body">
                <form @submit.prevent="enviar">
                  <div class="form-group">
                    <div class="input-group">
                      <input
                        type="password"
                        v-model="password"
                        @input="resetErrors"
                        class="form-control"
                        name="username"
                        placeholder="Contraseña nueva"
                        required
                      />
                      <box-icon name='show'></box-icon>
                    </div>
                  </div>
                  <div class="form-group">
                    <div class="input-group">
                      <input
                        type="password"
                        v-model="password2"
                        @input="resetErrors"
                        class="form-control"
                        name="password"
                        placeholder="Confirmar contraseña"
                        required
                      />
                      <box-icon name='show'></box-icon>
                    </div>
               
                    <div v-if="errorMessage" class="error-message">
                      {{ errorMessage }}
                    </div>
                    <div v-if="error403" class="error-message">
                      Cuenta no verificada.<br>
                      <router-link :to="emailConfirm">Enviar email de confirmación</router-link>

                    </div>

                  </div>
                  <div class="form-group">
                    <button type="submit" class="btn btn-primary btn-block btn-lg">
                      Aceptar
                    </button>
                  </div>
                </form>
              </div>
              
          
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import store from '@/store/store';
import axios from 'axios';
import { BoxIconElement } from 'boxicons';
export default {
  data() {
    return {
      token: this.$route.params.token,
      email: "",
      password: "",
      password2:"",
      errorMessage: "",
      error403: false,
    };
  
  },
  methods: {
    async enviar() {
      this.resetErrors(); 
      if (this.password !== this.password2) {
          this.errorMessage = "Las contraseñas no coinciden";
          return;}
      
      try {
    
        await axios.post('reset-password', {
          token: this.token,
          password: this.password
        });
        this.$router.push('/login');

      //  localStorage.setItem('token', token);
       // localStorage.setItem('user', JSON.stringify(user));
/*

        if (user.role === "ADMIN") {
          store.commit('isAdmin', true);
          store.commit('setAuthentication', true);
          store.commit('setUser', user);
          this.$router.push('main'); //redirige a panel admin
        } else {
         
          store.commit('isUser', true);
          store.commit('setAuthentication', true);
          store.commit('setUser', user);
          this.$router.push('userProfile'); //redirigir          
        }
*/
   
        
      } catch (error) {
        if (error.response) {
          if (error.response.status === 403) {
            this.error403 = true;
            this.errorMessage = "Cuenta no verificada. Revise su email";
            console.log(this.token);
            localStorage.setItem('token',this.token);
         
          } else if (error.response.status === 401) {
            this.errorMessage = "Email o contraseña incorrectos";
          } else {
            this.errorMessage = "Ha ocurrido un error";
          }
        } else {
          this.errorMessage = "Ha ocurrido un error";
        }
      }
    },
    resetErrors() {
      this.errorMessage = '';
      this.error403 = false;
    },
  },
};
</script>
<style scoped>
body, html {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Varela Round', sans-serif;
  background: url('~@/assets/login.png') no-repeat center center fixed;
  background-size: cover;
}

#container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  

}

#wrapper {
  display: flex;
  margin-top: auto;
  justify-content: center;
  align-items: center;
  width: 1390px;
  height: 557px;
  background: url('~@/assets/login.png');
}

.modal-login {
  width: 350px;
}

.modal-login .modal-content {
  padding: 20px;
  border-radius: 10px; /* Redondear los bordes del contenido del modal */
  border: none;
}

.modal-login .modal-header {
  border-bottom: none;
  justify-content: center;
  text-align: center;
  margin-top: 30px;
}

.modal-login h4 {
  color: #636363;
  text-align: center;
  font-size: 26px;
  margin-top: 0;
}

.modal-login .modal-content {
  color: #999;
  margin-bottom: 15px;
  background: #fff;
  text-align: center;
  box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);
}

.modal-login .form-group {
  margin-bottom: 20px;
}

.modal-login label {
  font-weight: normal;
  font-size: 13px;
}

.modal-login .form-control {
  min-height: 38px;
  padding-left: 5px;
  box-shadow: none !important;
  border-width: 0 0 1px 0;
  border-radius: 5px; /* Redondear los bordes de los campos del formulario */
}

.modal-login .form-control:focus {
  border-color: #ccc;
}

.modal-login .btn, .modal-login .btn:active {        
  font-size: 16px;
  color: rgb(255, 255, 255);
  font-weight: normal;
  background: #21a31d !important;
  border-radius: 5px; /* Redondear los bordes del botón */
  border: none;
  width: 10vh;
  height: 5vh;
  min-width: 140px;
}

.modal-login .btn:hover, .modal-login .btn:focus {
  background: #179b81 !important;
}

.modal-login .hint-text {
  text-align: center;
  padding-top: 5px;
  font-size: 13px;
}

.modal-login .modal-footer {
  color: #999;
  border-color: #dee4e7;
  text-align: center;
  margin: 0 -25px -25px;
  font-size: 13px;
  margin-bottom: 20px;
}

.modal-login a {
  color: #606C38;
  text-decoration: none;
}	

.modal-login a:hover {
  text-decoration: underline;
}
</style>