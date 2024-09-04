<template>
 <head>
    <meta name='viewport' content='width=device-width, initial-scale=1'>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
  </head>
  <div>
    <NavBar />
    <div id="container">
      <div id="wrapper" class="background-image">
        <div id="myModal" class="modal show">
          <div class="modal-dialog modal-login">
            <div class="modal-content">
              <div class="modal-header">
                <h4 class="modal-title">Iniciar sesión</h4>
              </div>
              <div class="modal-body">
                <form @submit.prevent="login">
                  <v-text-field
                    v-model="user.email"
                    type="email"
                    label="Email"
                    required
                    :rules="[v => !!v || 'Este campo es necesario', v => /.+@.+\..+/.test(v) || 'Email debe ser válido']"
                  ></v-text-field>

                  <v-text-field
                    ref="passwordInput"
                    :type="passwordVisible ? 'text' : 'password'"
                    v-model="user.password"
                    label="Contraseña"
                    required
                    style="font-weight: normal; "
                    :rules="[v => !!v || 'Este campo es necesario']"
                    :append-icon="passwordVisible ?  'fas fa-eye' : 'fas fa-eye-slash'"
                    @click:append="verPassword"
                    ></v-text-field>

                  <div class="modal-footer">
                    <v-progress-circular
                      v-if="loading"
                      indeterminate
                      color="primary"
                      height="4"
                    />
                  </div>

                  <div v-if="errorMessage" class="error-message">
                    {{ errorMessage }}
                  </div>

                  <v-btn type="submit" color="success" block>Ingresar</v-btn>
                </form>
              </div>

              <div class="modal-footer">
                ¿No tenés cuenta?
                <router-link :to="'signup'">Registrate</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <v-snackbar
        v-model="showSnackbar"
        :color="snackbarColor"
        :timeout="3000"
      >
        {{ snackbarMessage }}
    </v-snackbar>
  </div>

</template>

<script>
import NavBar from '@/components/NavBar.vue';
import store from '@/store/store';
import axios from 'axios';
export default {
  data() {
    return {
      user: {
        email: "",
        password: "",
      },
      errorMessage: "",
      error403: false,
      passwordVisible:false,

      showSnackbar: false,
      snackbarColor: '',
      snackbarMessage: '',
    };
  },
  computed: {
    isAuthenticated() {
      return store.getters.isAuthenticated;
    },
    userFromStore() {
      return store.getters.user;
    },
    isAdmin() {
      return store.getters.user.isAdmin;
    },
  },
  methods: {
    async login() {
      this.resetErrors(); 
      this.loading = true;
      try {
        const response = await store.dispatch('login', this.user);

        if (response) { // Si la respuesta es true, el login fue exitoso
          this.showSuccessSnackbar("Bienvenido/a");

          // Verifica si el usuario es admin o no y redirige según corresponda
          if (this.isAdmin) {
            this.$router.push('/main'); // Redirige a /main si es admin
          } else {
            this.$router.push('/'); // Redirige a la página principal si no es admin
          }
        } else {
          this.showErrorSnackbar("Ocurrió un error. Verifique su información.");
        }

      } catch (error) {
        this.handleLoginError(error);
      } finally {
        this.loading = false;
      }
    },
  
    handleLoginError(error) {
      if (error.response) {
        if (error.response.status === 403) {
          this.error403 = true;
          this.errorMessage = "Cuenta no verificada. Revise su email";
        } else if (error.response.status === 401) {
          this.errorMessage = "Email o contraseña incorrectos";
        } else {
          this.errorMessage = "Ha ocurrido un error";
        }
      } else {
        this.errorMessage = "Ha ocurrido un error";
      }
    },
    resetErrors() {
      this.errorMessage = '';
      this.error403 = false;
    },
    verPassword() {
      this.passwordVisible = !this.passwordVisible;
    },
    async verificarUser() {
        try {
          console.log(this.email);
         await  axios.post(`user/${id}`, this.email);
          
        } catch (error) {
          this.errorMessage = "Ha ocurrido un error. ¿Esta registrado?";
        }
    },
    showSuccessSnackbar(message) { 
        this.snackbarColor = 'success'; 
        this.snackbarMessage = message; 
        this.showSnackbar = true;
      },
 
      showErrorSnackbar(message) { 
        this.snackbarColor = 'error'; 
        this.snackbarMessage = message; 
        this.showSnackbar = true;
      },

  },
};
</script>

<style scoped>


body, html {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Roboto', sans-serif;
  /*background: url('~@/assets/login.png') no-repeat center center fixed;*/
  background-size: cover;
  background-color: #606C38 ;
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
  height: 604px;
  background-color: #606C38 ;
  /*(background: url('~@/assets/login.png');*/
}

.modal-login {
  width: 350px;
}

.modal-login .modal-content {
  padding: 20px;
  border-radius: 10px; 
  border: none;
}

.modal-login .modal-header {
  border-bottom: none;
  justify-content: center;
  text-align: center;
  margin-top: 30px;
  margin-bottom: 30px;
}

.modal-login h4 {
  color: #636363;
  text-align: center;
  font-size: 26px;
  margin-top: 0;
}
input[name=password] {
  width: 250px !important;

}
input[name=email] {
  width:250px;
}

.modal-login .modal-content {
  color: #999;
  margin-bottom: 15px;
  background: #fff;
  text-align: center;
  box-shadow: 0px 2px 2px rgba(116, 112, 112, 0.3);
}

.modal-login .form-group {
  margin-bottom: 20px;
  border-color: #636363;
}
.form-group:hover {
  border-color: #636363 !important;
}
.modal-login label {
  font-weight: normal;
  font-size: 13px;
}

.modal-login .form-control {
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 10px;
}

.modal-login .form-control:focus {
  border-color: #aaa;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}
.input-group {
  position: relative;
}

.input-group-addon {
  position: absolute;
  right: 5px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
}

.input-group-addon .box-icon {
  font-size: 18px;
  color: #666;
}

.modal-login .btn, .modal-login .btn:active {        
  font-size: 16px;
  color: rgb(255, 255, 255);
  font-weight: normal;
  background: #21a31d !important;
  border-radius: 5px;
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

