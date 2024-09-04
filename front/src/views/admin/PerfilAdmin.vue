<template>
    <div class="app-container">
      <head>  
        <meta name="viewport" content=
            "width=device-width, initial-scale=1.0" />
      </head>
  
        <div class="header"></div>
        <div class="container">
        <form @submit.prevent="updateProfile">
          <div class="row">
            <div class="row">
            <input type="button" id="editar" value="Editar" @click="editProfile">

          </div>
            <div class="col-25">

              <label>Nombre</label>
              <br>
              <input type="text" v-model="user.nombre" :disabled="isDisabled">
        
              <label>Email</label>
              <br>
              <input type="email" v-model="user.email" :disabled="isDisabled">
              
              <div class="row">
                  <input type="button" id="cancelar" :disabled="isDisabled" @click="cancelEdit" value="Cancelar">
                </div>
            </div>
            <div class="col-25">
              <label for="apellido">Apellido</label>
              <input type="text" v-model="user.apellido" :disabled="isDisabled">
  
              <label for="tel">Teléfono</label>
              <br>
              <input type="tel" v-model="user.telefono" :disabled="isDisabled">
              <br>
          <!---Enviar-->
              <div class="row">
            <input type="submit" value="Actualizar" :disabled="isDisabled">
          </div>
          
        
            </div>
            
          
          </div>
         
        </form>
    </div>
    <div class="footer"></div>
     
    </div>
  </template>
    
  <script>
    import store from '@/store/store';
    import axios from 'axios';
    
    export default {
      name: 'perfilAdmin',
    
      data() {
        return {
          isDisabled: true,
          mainUser:"",
          user: {
            nombre: "",
            apellido: "",
            telefono: "",
            email: "",
          },
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
          return store.getters.isAdmin;
        },
      },
      async mounted() {
        if (this.isAuthenticated) {
          await this.getUser();
        }
    
      },
      methods: {
        async updateProfile() {
        try {
          const token = localStorage.getItem('token');
          const id = this.userFromStore.id;
          console.log(this.user);
          
          await axios.put(`user/update/${id}`, this.user, {
            headers: {
              Authorization: `Bearer ${token}`,
              'Content-Type': 'application/json',
            },
          });
          this.message = "Usuario actualizado.";
          this.snackbar = true;
          store.commit('updateUser', this.user); // actualizar el usuario en el store
          this.isDisabled = true; // deshabilita el boton editar
        } catch (error) {
          console.log(error);
          this.message = "Error al actualizar el usuario.";
          this.snackbar = true;
        }
      },
        editProfile() {
          this.originalUser = { ...this.user };
          this.isDisabled = false;
        },
  
        cancelEdit() {
          this.user = { ...this.originalUser };
          this.isDisabled = true;
        },
        
      async getUser() {
          try {
            const token = localStorage.getItem('token');
            const id = this.userFromStore.id;
            const response = await axios.get(`user/${id}`, {
              headers: {
                Authorization: `Bearer ${token}`
              }
            });
            
            this.user = response.data;
            store.commit('updateUser', response.data); // actualizar el usuario en el store
    
          } catch (error) {
            console.error(error);
          }
        },
      },
    };
  </script>
    
    
  <style scoped>
    @import url('https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400;500;600;700&display=swap');
    
  html, body, .app-container {
    height: 100% auto;
    margin: 0;
    padding: 0;
    font-family: "Roboto",Helvetica,Arial,sans-serif;  
  }
  .app-container {
    display: contents;
    flex-direction: column;
    background-color: #f8f5f5;
    min-height: 100vh;
  }
  label {
    padding: 12px 12px 12px 0;
    display: inline-block;
  }
  
  /* Boton actualizar*/
  input[type=submit] {
    background-color: #04AA6D;
    color: white;
    padding: 10px 18px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    margin-top: 30px;
    float: center;
    font-size: 15px;
  }
  input[type=submit]:hover{
    background-color: #138338;
  }
  input[type=submit]:disabled {
    background-color: dimgray;
  }
  input[id=editar] {
    background-color: #4588c7;
    color: white;
    border: none;
    padding: 10px 18px;
    border-radius: 4px;
    cursor:pointer;
    
    position: fixed;
    font-size: 13px;
    font-weight: bold;
    width: fit-content;
  }
  input[id=editar]:hover{
    background-color: #295a80;
  }
  input[id=cancelar] {
    background-color: #b5bdc5;
    color: black;
    border: none;
    padding: 10px 18px;
    border-radius: 4px;
    cursor:pointer;
    margin-top: 30px;
    float: right;
    font-size: 15px;
    margin-right: -30px;
  }
  input[id=cancelar]:hover{
    background-color: #7f8386;
  }
  .container {
    border-radius: 5px;
    background-color: #ffffff;
    padding: 20px;
    margin-top: 50px;
    margin-left: 55px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    margin: 100px auto;
    position:relative;
    max-width: 800px;
    min-height: 50vh;
  
  }
  
  @media (max-width: 768px) {
    .container {
      margin: 50px auto 0;
  }}
  
  .col-25 {
    float: left;
    width: 25%;
    margin-top: 30px;
    margin-left: 90px;
    padding-right: 70px;
  }
  
  
  .row:after {
    content: "";
    display: table;
    clear: both;
  }
  
  @media screen and (max-width: 600px) {
    .col-25, .col-75, input[type=submit] {
      width: 100%;
      margin-top: 0;
    }
  }
  
  input[type=tel], input[type=email], input[type=text] {
    padding: 10px 30px;
  }
  
    
    
  </style>