<template>
  <v-app>
    <NavBar />
    <v-main> 
      <v-container>
        <v-row>
          <!-- sidebar -->
          <v-col cols="3">
            <v-card>
              <v-card-title>Filtrar</v-card-title>
              <v-card-text>
                <v-expansion-panels>
                  <!-- Autores -->
                  <v-expansion-panel>
                    <v-expansion-panel-header  class="text-h6"><b>Autores</b></v-expansion-panel-header>
                    <v-expansion-panel-content>
                      <v-list  density="compact" max-height="150px">
                        <v-list-item 
                        style="font-size: 12px; padding: 5px"
                        v-for="(author, index) in authors" 
                        :key="index" 
                        @click="selectedAuthor = author">
                          <v-list-item-content>
                            <v-list-item-title>{{ author }}</v-list-item-title>
                          </v-list-item-content>
                        </v-list-item>
                      </v-list>
                    </v-expansion-panel-content>
                  </v-expansion-panel>
                  <br>
                  <!-- Géneros -->
                  <v-expansion-panel>
                    <v-expansion-panel-header class="text-h6"><b>Géneros</b></v-expansion-panel-header>
                    <v-expansion-panel-content>
                      <v-list density="compact" max-height="150px">
                        <v-list-item 
                        style="font-size: 12px; padding: 5px"
                        v-for="(genre, index) in genres" 
                        :key="index"
                        @click="selectedGenre = genre">
                          <v-list-item-content>
                            <v-list-item-title>{{ genre }}</v-list-item-title>
                          </v-list-item-content>
                        </v-list-item>
                      </v-list>
                    </v-expansion-panel-content>
                  </v-expansion-panel>
                </v-expansion-panels>
              </v-card-text>
            </v-card>
          </v-col>

          <!-- content -->
          <v-col cols="9">
            <v-card>
              <v-card-title>Catálogo de libros disponibles</v-card-title>
              <v-card-text>
              
                <v-container>
                  <v-card
                  v-for="libro in paginacion"
                    :key="libro.titulo"
                    class="book-card"
                  >
                    <v-row no-gutters>
                      <v-col cols="4">
                        <v-img 
                          :src="libro.imagen ? libro.imagen: 'https://scholastic.asia/sites/all/themes/scholastic_asia/images/default-book.png'" 
                          max-width="100%" 
                          max-height="250px" 
                          contain 
                          style="margin-top: 40px; 
                          margin-bottom: 40px;"
                          ></v-img>
                      </v-col>
                      <v-col cols="8">
                        <v-card-title class="title-big">{{ libro.titulo }}</v-card-title>
                        <v-card-subtitle>{{ libro.autor }}, {{ libro.fecha }}</v-card-subtitle>
                        <v-card-text>
                          <p><b>{{ libro.genero }} </b></p>
                          <p>{{ libro.sinopsis }}</p>
                          <p>{{ libro.pag }} Páginas - ISBN {{ libro.isbn }}</p>
                          <p>
                            <v-card-actions class="float-left">
                              <star-rating
                                v-model:rating="libro.rating"
                                v-bind:increment="0.1"
                                v-bind:max-rating="5"
                                v-bind:star-size="30"
                                read-only
                              ></star-rating>
                            </v-card-actions>
                            <!-- Botones -->
                            <button
                              type="button"
                              :id="'borrow-' + libro.titulo"
                              @click="borrowBook(libro)"
                              class="btn btn-info float-right"
                            >
                              Pedir
                            </button>
                          </p>
                        </v-card-text>
                      </v-col>
                    </v-row>
                  </v-card>
                </v-container>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>

        <v-pagination
          v-model="currentPage"
          :length="totalPages"
          :total-visible="5"
          class="justify-center"
          v-if="isAuthenticated"
          id="pag"
          @input="loadMoreBooks()"
        ></v-pagination>

  
      </v-container>
    </v-main>
      <v-overlay
        v-model="showOverlay"
        z-index="10" 
        opacity="0.5" 
        color="white"
        class="overlay-login"
      >
      <v-dialog 
        v-model="showOverlay"
        class="login-dialog"
        overlay-opacity="0"
      >
      <v-card>
        <v-card-title>Oops</v-card-title>
        <v-card-text>
          Si querés ver más libros de la lista, podés iniciar sesión o crear una cuenta.
        </v-card-text>
        <v-card-actions>
          <v-btn @click="login" class="btn btn-info">Iniciar sesión</v-btn>
          <v-btn @click="signup" class="btn btn-success">Registrarse</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-overlay>

    <v-overlay
      v-model="openModalPrestamo"
      z-index="10" 
      opacity="0.5" 
      color="white"
      >
      
        <v-dialog 
        v-model="openModalPrestamo" 
        max-width="500" 
        overlay-opacity="0"
        content-class="transparent-dialog"
        ref="borrowBookModal">
        <v-card>
          <v-card-title>Realizar préstamo</v-card-title>
          <v-card-text>
            ¿Estás seguro de que quieres pedir prestado el libro "<b>{{ selectedLibro.titulo }}</b>"?
          </v-card-text>
          <v-card-actions>
            <v-btn @click="openModalPrestamo = false">Cancelar</v-btn>
            <v-btn @click.prevent="confirmPrestamo">Aceptar</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-overlay>
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
import NavBar from '@/components/NavBar.vue';
import StarRating from 'vue-star-rating';
import axios from 'axios';
import moment from 'moment';
import store from '@/store/store';
export default {
  components: {
    NavBar,
    StarRating
  },
  data() {
    return {
      authors: [],
      genres: [],
      ratings: [],
      selectedLibro: null,
      openModalPrestamo: false,
      selectedAuthor: null,
      selectedGenre: null,
      currentPage: 1,
      pageSize: 10,
      totalPages: 0,
      showOverlay: false,
      showSnackbar: false,
      snackbarColor: '',
      snackbarMessage: '',
      search: '',
      allLibros:[],

      libros: [
      {
        id:1,
        titulo: "Brave New World",
        autor: "Aldous Huxley",
        paginas: 283,
        isbn: 16335101,
        año: 1958,
        rating: 3.93,
        sinopsis:"La novela describe un mundo en el que finalmente se han cumplido los peores vaticinios: triunfan los dioses del consumo y la comodidad; y el orbe se organiza en diez zonas en apariencia seguras y estables. Sin embargo; este mundo ha sacrificado valores humanos esenciales; y sus habitantes son procreados in vitro a imagen y semejanza de una cadena de montaje.",
        genero: "Ciencia Ficción",
        imagen: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1575509280i/5129.jpg"
      },
     
      {
        id:3,
        titulo: "After the Funeral",
        autor: "Agatha Christie",
        paginas: 195,
        isbn: 1812530,
        año: 1953,
        rating: 3.84,
        imagen: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1384881992i/527744.jpg",
        sinopsis:"El dueño de una mansión victoriana muere repentinamente y su hermana está convencida de que fue un asesinato. Cuando Cora es asesinada salvajemente con un hacha, el comentario extraordinario que hizo el día anterior en el funeral de su hermano Richard adquiere de repente un significado escalofriante. En la lectura del testamento de Richard, se oye claramente a Cora decir: 'Se ha silenciado muy bien, ¿no es así? Pero fue asesinado, ¿no?'. Desesperado, el abogado de la familia recurre a Hércules Poirot para desentrañar el misterio.",
        genero: "Suspenso",
        
      },
     
      {
        id:7,
        titulo: "Cuentos de Eva Luna",
        autor: "Isabel Allende",
        paginas: 412,
        isbn: 130037,
        año: 1988,
        rating: 3.96,
        genero: "Ficción",
        imagen: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1347514454i/781914.jpg",
        sinopsis:"Una niña solitaria se enamora del amante de su madre y practica misteriosas ceremonias rituales; una mujer permanece medio siglo enc errada en un sótano; víctima de un caudillo celoso; en el fragor de una batalla; un hombre viola a una muchacha y mata a su padre.",
      },
     
      {
        id:9,
        titulo: "Oracle Night",
        autor: "Paul Auster",
        paginas: 305,
        isbn: 3011932,
        año: 2004,
        rating: 3.77,
        genero: "Suspenso",
        imagen:"https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1328287302i/10006.jpg",
        sinopsis: "Varios meses después de recuperarse de una enfermedad casi fatal, el novelista Sidney Orr entra en una papelería en Brooklyn y compra un cuaderno azul. Es el 18 de septiembre de 1982 y durante los siguientes nueve días Orr vivirá bajo el hechizo de este libro en blanco, atrapado dentro de un mundo de premoniciones inquietantes y eventos desconcertantes que amenazan con destruir su matrimonio y socavar su fe en la realidad.",
      },
      {
        id:10,
        titulo: "Time and Again",
        autor: "Jack Finney",
        paginas: 474,
        isbn: 6887879,
        imagen: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1393198563i/40526.jpg",
        sinopsis: "Transportado desde mediados del siglo XX a la ciudad de Nueva York en el año 1882, Si Morley camina por la elegante Ladies' Mile de Broadway, queda encantado con el tintineo de las campanillas de los trineos en Central Park y resuelve un misterio del siglo XX al descubrir sus raíces en el siglo XIX. Al enamorarse de una hermosa joven, finalmente se ve obligado a elegir entre su vida en el presente y en el pasado.",
        año: 1970,
        rating: 3.98,
        genero: "Ciencia Ficción"
      }
      ]
    };
  },
  mounted() {

    this.totalPages = Math.ceil(this.libros.length / this.pageSize);

    // ver si esta loggeado
    if(this.isAuthenticated) {
      this.verTotalLibros();
    }else{
      this.allLibros = this.libros;
    }
  },
  computed: {
    filteredLibros() {
    return this.libros.filter(libro => {
      if (this.selectedAuthor && libro.autor !== this.selectedAuthor) return false;
      if (this.selectedGenre && libro.genero !== this.selectedGenre) return false;
      if (this.search && !(
        libro.titulo.toLowerCase().includes(this.search.toLowerCase()) ||
        libro.autor.toLowerCase().includes(this.search.toLowerCase()) ||
        libro.genero.toLowerCase().includes(this.search.toLowerCase())
      )) return false;
      return true;
    });
  },
    isAuthenticated() {
      return store.getters.isAuthenticated;
    },
    userFromStore() {
        return store.getters.user;
    },
    paginacion() {
      const startIndex = (this.currentPage - 1) * 5;
      const endIndex = startIndex + 5;
      return this.filteredLibros.slice(startIndex, endIndex);
    },
    authors() {
    return [...new Set(this.libros.map(libro => libro.autor))];
    },
    genres() {
      return [...new Set(this.libros.map(libro => libro.genero))];
    },
    
  },
  watch: {
    selectedAuthor() {
      this.selectedGenre = null;
      this.selectedRating = null;
    },
    selectedGenre() {
      this.selectedAuthor = null;
      this.selectedRating = null;
    },
  
},
  methods: {
    async confirmPrestamo() {
    try {
      const token = localStorage.getItem('token');
      const userStore = this.userFromStore;
      const fecha = this.formatDate(new Date());
      const prestamo = {
        libro: this.selectedLibro,
        user: userStore,
        finicio: fecha,
        ffin: null,
      };

      const response = await axios.post("prestamo/save", prestamo, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.status === 200) {
    
        this.showSuccessSnackbar("Préstamo realizado");
        this.openModalPrestamo = false;

      } else {
        
        this.handleError(response);
      }
    } catch (error) {
      if (error.response) {

        this.handleError(error.response);
      } else if (error.request) {
        console.error(error.request);
        this.showErrorSnackbar('Error en la conexión. Intente más tarde.');
      } else {
        console.error('Error:', error.message);
        this.showErrorSnackbar('Error desconocido. Intente más tarde.');
      }
    }
  },
  handleError(response) {
  if (response && response.data && response.data.errorCode) {
    if (response.data.errorCode === "USER_NOT_FOUND") {
      this.showErrorSnackbar("El usuario no fue encontrado. Por favor, verifique la información.");
    } else if (response.data.errorCode === "LIBRO_NODISPONIBLE") {
      this.showErrorSnackbar("El libro no está disponible para préstamo.");
    } else if (response.data.errorCode === "MAX_PRESTAMOS") {
      this.showErrorSnackbar("El usuario ya tiene cinco libros prestados activos.");
    } else {
      
      this.showErrorSnackbar(response.data.errorMessage || 'Error desconocido. Intente más tarde.');
    }
  } else {
    
    this.showErrorSnackbar('Error desconocido. Intente más tarde.');
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
  formatDate(date) {
    const momentDate = moment.utc(date, 'YYYY-MM-DDTHH:mm:ss.sssZ');
    const year = momentDate.year();
    const month = momentDate.month() + 1;
    const day = momentDate.date();

    return `${day}-${month}-${year}`;
  },
  borrowBook(libro){
    console.log(libro);

    if(this.isAuthenticated ){
      this.showOverlay= false;
      this.selectedLibro = libro;
      this.openModalPrestamo = true;
    } else {
      //si no esta autenticado
      this.showOverlay = true;
      
    }
  },
  updateFilter(){
    this.filteredLibros;
    this.authors = [...new Set(this.libros.map(libro => libro.autor))];
    this.genres = [...new Set(this.libros.map(libro => libro.genero))];
  },
  verTotalLibros(){
    axios.get("book/list")
      .then(response => {
        this.allLibros = response.data;
        this.libros = [ ...response.data]; 
        this.totalPages = Math.ceil(this.allLibros.length / 5);
        this.updateFilter();
      })
      .catch(error => {
        console.log(error);
      });
},
    loadMoreBooks(page) {
      if (!this.isAuthenticated && page >1) {
    this.showOverlay = true;
  } else {
      const startIndex = (page - 1) * 5;
      const endIndex = startIndex + 5;
      this.libros = this.allLibros.slice(startIndex, endIndex);
  }
},

  closeModalPrestamo(){
    this.openModalPrestamo = false;
  },
  login() {
    this.showOverlay= false;
    this.$router.push("signin");
  },
  signup() {
    this.showOverlay = false;
    this.$router.push("signup");
  }
  },
};
</script>
<style>
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
.icon-hover:hover {
  border-color: #3b71ca !important;
  background-color: white !important;
}

.v-expansion-panels{
  flex-direction: column;
    height: 100%;
}
.book-card {
  margin-bottom: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
.transparent-dialog {
  background-color: transparent !important;
  box-shadow: none !important;
}

.title-big {
  font-size: 1.5rem;
  font-weight: bold;
}
.icon-hover:hover i {
  color: #3b71ca !important;
}
.container {
    position: relative;
    min-height: 800px;
    overflow-y: auto
}

.container::-webkit-scrollbar {
    width: 6px;
    border-radius: 3px
}

.container::-webkit-scrollbar-thumb {
    background-color: #ffa500;
    border-radius: 3px
}

.container.bg-sec-light {
    background-color: #e7e7e7
}

#navbar-toggler-icon {
    left: 7%;
    height: 40px;
    width: 40px;
    text-align: center;
    background: #1b1b1b;
    border-radius: 3px;
    cursor: pointer;
    z-index: 5;
    transition: left 0.4s ease
}

#navbar-toggler-icon.click {
    position: absolute;
    left: 210px;
    top: 0;
    background-color: #747474
}

#navbar-toggler-icon.click span:before {
    content: '\f00d'
}

.sidebar {
    position: absolute;
    width: 220px;
    height: 100%;
    background-color: #161616;
    color: #fff;
    transform: translateX(-100%) scale(0);
    left: 0;
    transition: transform .2s ease-in-out
}

.sidebar.show {
    transform: translateX(0%) scale(1)
}

#side-nav {
    float: left;
    width: 18%
}

#content {
    float: right;
    width: 82%
}

a {
    text-decoration: none;
    color: #222
}

#content .user-select-none {
    user-select: none
}

nav ul .brand img {
    object-fit: contain;
    width: 100px;
    height: 70px
}

nav ul {
    height: 100%;
    width: 100%;
    list-style: none;
    padding: 0;
    margin-bottom: 0
}

nav ul li {
    line-height: 50px
}

nav ul li a {
    color: #e5e5e5;
    text-decoration: none;
    display: block;
    width: 100%;
    padding-left: 20px;
    border-left: 4px solid transparent
}

nav ul li a:hover {
    color: #fff;
    background-color: #2e2e2e
}

nav ul li.active a {
    color: #ffff;
    background-color: #747474;
    border-left: 4px solid #ffa500
}
.transparent-dialog {
  background-color: white !important; 
  border-radius: 8px;
}

.login-dialog .v-card {
  background-color: white;
}

</style>