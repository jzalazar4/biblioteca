<template>
<v-app>
<head>      
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
</head>
    <NavBar />
        <v-main class="text-center">
            <v-container>
                <v-row>
                    <v-col cols="9">
                    <v-card>
                        <v-card-title>Mis libros pedidos</v-card-title>
                        
                        <v-card-text>
                            <v-container>
                                <v-card 
                                v-for="prestamo in prestamos" 
                                :key="prestamo.id"
                                class="book-card mb-4">
                                <v-row >
                                    <v-col cols="2">
                                        <v-img
                                        :src="prestamo.libro.imagen"
                                        contain
                                        max-width="50%"
                                        max-height="150%"
                                        ></v-img>
                                    </v-col>
                                    <v-col>
                                        <v-card-title style="font-size: 15px;">
                                            {{ prestamo.libro.titulo }}
                                        </v-card-title>
                                        <v-card-subtitle>
                                            {{ prestamo.libro.autor }},
                                            {{ prestamo.libro.genero }}
                                        </v-card-subtitle>
                                    </v-col>
                                    <v-btn 
                                    class="float-right" 
                                    small 
                                    color="info"
                                    @click="openDialog(prestamo)"
                                    >
                                    Devolver
                                </v-btn>
                                </v-row>
                                </v-card>    
                            </v-container>
                        </v-card-text>
                    </v-card>
                </v-col>
            </v-row>
            </v-container>
            <v-overlay 
                v-model="openDevolverModal" 
                z-index="10" 
                opacity="0.5" 
                color="white">
                <v-dialog 
                    v-model="openDevolverModal" 
                    max-width="500" 
                    overlay-opacity="0"
                    class="v-dialog"
                    content-class="transparent-dialog"
                    ref="borrowBookModal">
                    <v-card>
                    <v-card-title>Devolver libro</v-card-title>
                    <v-card-text>
                        ¿Estás seguro de que quieres devolver el libro "<b>{{ selectedPrestamo.libro.titulo }}</b>"?
                    </v-card-text>
                    <v-card-actions>
                        <v-btn @click="closeModalDevolucion">Cancelar</v-btn>
                        <v-btn @click.prevent="confirmDevolucion">Aceptar</v-btn>
                    </v-card-actions>
                    </v-card>
                </v-dialog>
            </v-overlay>
  
        </v-main>
        <v-snackbar
            v-model="showSnackbar"
            :color="snackbarColor"
            :timeout="3000"
            >
            {{ snackbarMessage }}
            </v-snackbar>
</v-app>
</template>
    
<script>
import store from '@/store/store';
import NavBar from '@/components/NavBar.vue';
import axios from 'axios';
export default {
    components: {
        NavBar,
    },
    data(){
        return{
            openDevolverModal: false,
            prestamos: [],
            selectedPrestamo: null,
            showSnackbar: false,
            snackbarColor: '',
            snackbarMessage: '',
        }
    },
    computed: {
        userFromStore() {
            return store.getters.user;
        }
    },
    mounted() {
        const id = this.userFromStore.id;
        axios.get("/prestamo/userList", {  
            params: {
                id: id
        }}).
        then(response => {
            this.prestamos = response.data;
            console.log(response);
        }).catch(error => {
            console.log(error);
        });
    },
    methods: {
        openDialog(prestamo) {
            this.selectedPrestamo = prestamo; // guardar el préstamo completo
            this.openDevolverModal = true;
        },
        closeModalDevolucion() {
            this.openDevolverModal = false;
        },
        async confirmDevolucion() {
        const id = this.selectedPrestamo.id; 
        const token = localStorage.getItem('token');
        try {
           const response = await axios.put(`prestamo/devolver/${id}`, {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
            });
            if(response.status === 200) {
                this.showSuccessSnackbar("Libro devuelto");
                location.reload();
            } else {
                this.showErrorSnackbar("Ocurrio un error al devolver el libro. Intente más tarde");
                console.log(error);
            }
         
         
        } catch (error) {
            this.showErrorSnackbar("Ocurrio un error al devolver el libro. Intente más tarde");
            console.log(error);
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
}
};
</script>
<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@800&family=Poppins&display=swap');

body{
    background-color:#eee;
}
* {
    padding: 0;
    margin: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif
}
.float-right {
    margin-top:22px;
    margin-right:20px;
    padding:10px;
}
.text-center{
    margin-left:250px;
}

</style>