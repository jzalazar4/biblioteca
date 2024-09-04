<template>
  <div class="jumbotron">
    <div class="email-confirm">
      <h2 class="display-4">Confirmación de Correo Electrónico</h2>
      <hr class="my-4">
      <div class="lead">
        <p v-if="!emailSent">Se ha enviado un correo electrónico de confirmación a tu email. Por favor, revisa tu bandeja de entrada y sigue las instrucciones para confirmar tu cuenta.</p>
        <p v-else>Se ha enviado otro correo electrónico de confirmación a tu dirección de email.</p>
        <div v-if="error" class="error-message">Error al reenviar el correo electrónico de confirmación: {{ error }}</div>
        <form @submit.prevent="resendEmail">
        <div class="resend-email">
          <input
            type="email"
            v-model="useremail"
            placeholder="Ingresa tu email"
            required
          />
          <button type=submit>Reenviar email</button>
        
        </div>
      </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import store from '../../store/store';
export default {
  data() {
    return {
      emailSent: false,
      error: "",
      useremail:"",
     // updatedEmail: store.state.user?.email || "", // Obtén el email del usuario desde el store si está disponible
    };
  },
  methods: {
    async resendEmail() {
  this.error = "";
  try {
    console.log(this.useremail);
    const emailUser = this.useremail;
    console.log(emailUser);
    await axios.post("resendEmail?email=" + emailUser);
    this.emailSent = true;
    console.log("email enviado");
  } catch (error) {
    this.error = error.response?.data?.message || '';
    console.error('Error:', error);
  }
}


  }
};
</script>

<style scoped>
.email-confirm {
  max-width: 600px;
  margin: 0 auto;
}
.error-message {
  color: red;
}
.resend-email {
  margin-top: 20px;
}
</style>
