<template>
  <head>
    <meta name='viewport' content='width=device-width, initial-scale=1'>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
  </head>
  <div>
    <NavBar />
    <div id="container">
      <div id="wrapper" class="background-image">
        <div id="myModal" class="modal fade">
          <div class="modal-dialog modal-login">
            <div class="modal-content">
              <div class="modal-header">
                <h4 class="modal-title">Registrarse</h4>
              </div>
              <div class="form-group text-center">
                <form @submit.prevent="enviar">
                  <!--NOMBRE-->
                  <v-text-field
                    v-model="nombre"
                    label="Nombre"
                    required
                    :rules="[v => !!v || 'Este campo es necesario']"
                  ></v-text-field>

                  <!--APELLIDO-->
                  <v-text-field
                    v-model="apellido"
                    label="Apellido"
                    required
                    :rules="[v => !!v || 'Este campo es necesario']"
                  ></v-text-field>

                  <!--TELEFONO-->
                  <v-text-field
                    v-model="telefono"
                    label="Teléfono"
                    required
                    :rules="[
                      v => !!v || 'Este campo es necesario',
                      v => /^\d+$/.test(v) || 'Teléfono debe ser un número'
                    ]"
                  ></v-text-field>

                  <!--EMAIL-->
                  <v-text-field
                    v-model="email"
                    label="Email"
                    required
                    :rules="[
                      v => !!v || 'Este campo es necesario',
                      v => /.+@.+\..+/.test(v) || 'El email debe ser válido'
                    ]"
                  ></v-text-field>

                  <!--PASSWORD-->
                  <v-text-field
                    v-model="password"
                    label="Contraseña"
                    :type="passwordVisible ? 'text' : 'password'"
                    required
                    :rules="[
                      v => !!v || 'Este campo es necesario'
                    ]"
                    :append-icon="passwordVisible ? 'fas fa-eye' : 'fas fa-eye-slash'"
                    @click:append="verPassword"
                  ></v-text-field>

                  <!--PASSWORD2-->
                  <v-text-field
                    v-model="password2"
                    label="Repetir contraseña"
                    :type="passwordVisible2 ? 'text' : 'password'"
                    required
                    :rules="[
                      v => !!v || 'Este campo es necesario',
                      v => v === password || 'Contraseñas no coinciden'
                    ]"
                    :append-icon="passwordVisible2 ? 'fas fa-eye' : 'fas fa-eye-slash'"
                    @click:append="verPassword2"
                  ></v-text-field>

                  <v-btn type="submit" color="success" block>Aceptar</v-btn>

                  <p v-if="!passwordMatch" class="error-message">{{ errorMessage }}</p>
                </form>
              </div>
              <div class="modal-footer">
                <v-progress-circular
                  v-if="loading"
                  indeterminate
                  color="primary"
                  height="4"
                />
                <p>Tengo una cuenta <a href="signin"> Iniciar sesión</a></p>
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
  import axios from 'axios';
  import store from '@/store/store';
  export default {
    name: 'SignUp',

    data() {
      return {
      loading: false,

      email: "",
      password:"",
      nombre: "",
      apellido: "",
      telefono: "",

      showSnackbar: false,
      snackbarColor: '',
      snackbarMessage: '',

      password2: "",
      errorMessage: "",
      passwordMatch: true,
      passwordVisible: false,
      passwordVisible2:false,
    }},
  methods: {
    async verificar() {

      const passwordMatch = this.password.localeCompare(this.password2, undefined, { sensitivity: 'case' }) === 0;
      return { passwordMatch, errorMessage: passwordMatch ? '' : 'Las contraseñas no coinciden' };
    /*  if (this.password.localeCompare(this.password2, undefined, { sensitivity: 'case' }) === 0) {
        console.log("Contraseñas coinciden");
        this.passwordMatch = true;
        return true;
      } else {
        this.passwordMatch = false;
        this.errorMessage = "Las contraseñas no coinciden";
        return false;
      }*/
    },
  /*  async enviar() {
    const { passwordMatch, errorMessage } = await this.verificar();

    if (passwordMatch) {
      this.loading = true;

      try {
        const response = await axios.post(
          "signup",
          JSON.stringify({
            nom: this.nombre,
            ape: this.apellido,
            tel: this.telefono,
            userpassword: this.password,
            useremail: this.email
          }),
          { headers: { "Content-Type": "application/json" } }
        );
        this.loading=true;
        console.log(response);
        
        if (response && response.data) {
          this.loading = false;
          this.showSuccessSnackbar("Usuario registrado");
          this.showSuccessSnackbar("Se ha enviado un email. Verifique su correo.")

          const token = response.data.token;
          const user = response.data.user;

          
          //localStorage.setItem('user', JSON.stringify(user));
          
          store.commit('setToken', token);
          store.commit('setUser', user);
          store.commit('setAuthentication', true);
          //store.commit('setIsUser', true);
          //          this.loading=false;
          localStorage.setItem('token', token);
          this.$router.push("signin");
        } else {
          console.log("No hay response.data");
          this.showErrorSnackbar("Algo ha fallado.");
        }
      } catch (error) {
        this.showErrorSnackbar("No se pudo registrar. Intente más tarde.")
        console.error("Fallo al registrar:", error.message);
      } finally {
        this.loading = false;
    }
    } else {
      console.log(errorMessage); 

    }
  },*/
  async enviar() {
    const { passwordMatch, errorMessage } = await this.verificar();

    if (passwordMatch) { 
        this.loading = true;

        try {
            const response = await axios.post(
                "signup",
                JSON.stringify({
                    nom: this.nombre,
                    ape: this.apellido,
                    tel: this.telefono,
                    userpassword: this.password,
                    useremail: this.email
                }),
                { headers: { "Content-Type": "application/json" } }
            );
            this.loading = false;
            console.log(response); 

            if (response && response.data) {
                this.showSuccessSnackbar("Usuario registrado. Verifique su correo para activar la cuenta.");
                
              
                this.$router.push("signin");
            } else {
                this.showErrorSnackbar("Algo ha fallado.");
            }
        } catch (error) {
            this.showErrorSnackbar("No se pudo registrar. Intente más tarde.");
            console.error("Fallo al registrar:", error.message);
        } finally {
            this.loading = false; 
        }
    } else {
        console.log(errorMessage); 
    }
  },

  verPassword() {
      this.passwordVisible = !this.passwordVisible;
    },
  verPassword2(){
    this.passwordVisible2 = !this.passwordVisible2;
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
}

}
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
  
  background-color: #606C38 ;
  /*(background: url('~@/assets/login.png');*/
}

.modal-login {
  width: 400px;
  margin-top: 20px;
  margin-bottom: 10px;
}

.modal-login .modal-content {
  padding: 15px;
  border-radius: 10px; 
  border: none;
  justify-content: center;
}

.modal-login .modal-header {
  border-bottom: none;
  justify-content: center;
  text-align: center;
  margin-top: 10px;
  margin-bottom: 10px;
}

.modal-login h4 {
  color: #636363;
  text-align: center;
  font-size: 26px;
  margin-top: 0;
}
/*
input[name=password] {
  width: auto;

}
input[name=email] {
  width:250px;
}*/

.modal-login .modal-content {
  color: #999;
  margin-bottom: 15px;
  background: #fff;
  text-align: center;
  box-shadow: 0px 2px 2px rgba(116, 112, 112, 0.3);
}

.modal-login .form-group {
  margin-bottom: 10px;
  border-color: #636363;
}
.form-group:hover {
  border-color: #636363 !important;
}
.modal-login label {
  font-weight: normal;
  font-size: 11px;
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
  justify-content: center;
  max-width: fit-content;
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
  height: 6vh;
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
.form-control {
  display: block;
  margin: 0 auto;
}
.form-group {
  justify-content: center;
  align-items: center;
}
.modal-login a:hover {
  text-decoration: underline;
}
</style>