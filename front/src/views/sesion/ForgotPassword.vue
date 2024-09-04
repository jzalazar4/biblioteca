<template>

<div class="container padding-bottom-3x mb-2 mt-5">
  <div class="row justify-content-center">
    <div class="col-lg-8 col-md-10">
      <div class="forgot">
        
        <h2>Olvide mi contraseña</h2>
      <p>Por favor, ingresa tu email para reestablecer tu contraseña.</p>
  
      	
      
      <form class="card mt-4" @submit.prevent="enviar">
        <div class="card-body">
          <div class="form-group">
            <label for="email-for-pass">Ingresa tu email  </label>
            <input class="form-control" type="text" id="email-for-pass" required v-model="user.email">
             
          </div>
          </div>
          <br>
          <div class="card-footer">
            <button class="btn btn-danger" type="button" @click="cancelar">
              Cancelar
            </button> 
            <button class="btn btn-success" type="submit">Enviar</button>
            
          </div>
        </form>
      </div>
    </div>
  </div>
</div>
</template>

<script>
//import store from './store/store';
import axios from 'axios';

export default {
  name: 'userProfile',
  data() {
    return {
      user: {
        email: "",
      },
    };
  },
  methods: {
     cancelar() {
      this.$router.push("signin");
    },
    async enviar() {
      try {
        const token = localStorage.getItem('token');
        console.log(token);
        await axios.post('forgot-password', JSON.stringify({ email: this.email }),
            {
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            })
        /*
        await axios.post('/forgot-password',{ email: this.user.email });*/
        console.log("email enviado");
      } catch (error){
        console.log(error);
      }
    }
  }
}
</script>

<style scoped>

body {
    background-position: center;
    background-color: #eee;
    background-repeat: no-repeat;
    background-size: cover;
    color: #505050;
    font-family: "Rubik",Helvetica,Arial,sans-serif;
    font-size: 14px;
    font-weight: normal;
    line-height: 1.5;
    text-transform: none;
}

.forgot{
	    background-color: #fff;
    padding: 12px;
    border: 1px solid #dfdfdf;
}

.padding-bottom-3x {
    padding-bottom: 72px !important;
}

.card-footer{
	background-color: #fff;
}

.btn{ 

	font-size: 13px;
}

.form-control:focus {
    color: #495057;
    background-color: #fff;
    border-color: #76b7e9;
    outline: 0;
    box-shadow: 0 0 0 0px #28a745;
}
               
</style>