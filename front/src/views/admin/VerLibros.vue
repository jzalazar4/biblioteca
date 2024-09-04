<template>
  <head>
    <meta name='viewport' content='width=device-width, initial-scale=1'>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />

  </head>
  <div>


    <div class="float-left">
      <button  @click="exportCSV" class="btn btn-light">Exportar CSV <i class='fas fa-file-csv' style='font-size:24px'></i></button>

    </div>
    <div class="float-right">
      <v-btn @click="openAddModal">Nuevo</v-btn>   
    </div>
  
  <br>

  <form>
    <div class="filter-title mb-3">
   
      
    </div>
      <div class="row">
        <div class="col-md-2">
          <label for="titulo">Título:</label>
          <input type="text" class="form-control" id="titulo" v-model="buscar.title">
        </div>
        <div class="col-md-2">
          <label for="autor">Autor:</label>
          <input type="text" class="form-control" id="autor" v-model="buscar.author">
        </div>
        <div class="col-md-2">
          <label for="genero">Género:</label>
          <input type="text" class="form-control" id="genero" v-model="buscar.genre">
        </div>
        <div class="col-md-2">
          <label for="genero">Stock:</label>
          <input type="text" class="form-control" id="stock" v-model="buscar.cant">
        </div>
        <div class="col-md-2 d-flex align-items-end justify-content-end">
          <button type="reset" class="botn btn-info" @click="resetForm">Cancelar</button>
          <button @click.prevent="searchBooks" class="boton1 btn-success">Buscar</button>
          
        </div>
      </div>
    </form>
    <br>
    <table class="table table-condensed table-sm">
      <thead>
       
        <tr class="title-arrow-wrapper">
          <th v-for="col in columns" :key="col" @click="sortTable(col)">{{ col }} 
            <i v-if="col === sortColumn" :class="ascending ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(libro, index) in sortedPaginatedLibros" :key="libro.id" data-toggle="collapse" 
        :data-target="`#demo${index}`" class="accordion-toggle">

          <td @click="showModal(libro)">{{ libro.id }}</td>
          <td @click="showModal(libro)">{{ libro.titulo }}</td>
          <td @click="showModal(libro)">{{ libro.autor }}</td>
          <td @click="showModal(libro)">{{ libro.genero }}</td>
          <td @click="showModal(libro)">{{ libro.stock }}</td>
          <td @click="showModal(libro)">{{ libro.estado }}</td>
         <td>
          <div class="d-grid gap-6 d-md-block">
          <v-btn size="x-small" icon @click="openEditModal(libro)" >
            <box-icon name='edit-alt' color='#d8bf0e' title="Editar"></box-icon>
          </v-btn >
        <v-btn size="x-small" icon @click="deleteItem(libro)">
          <box-icon name='trash' type='solid' color='#c12424' title="Eliminar"></box-icon>
      </v-btn >
    </div>
         </td>
        </tr>
     
      </tbody>
      <tfoot>
  <tr>
    <td>
      <div class="pagination-controls">
        <!--currentPage y  @change="updatePageSize(currentPage)"-->
        <select v-model="perPage"  @change="updatePagination"  class="form-select">
          <option value="5">5</option>
          <option value="10">10</option>
          <option value="15">15</option>
          <option value="20" >20</option>
        </select>
   
      </div>
    </td>
    <td colspan="5">
      <button @click="prevPage" :disabled="currentPage === 1" class="btn btn-secondary mx-1">
        <i class="fas fa-chevron-left "></i> Anterior
      </button>
      <button @click="nextPage" :disabled="currentPage === totalPages" class="btn btn-secondary mx-1">
        Siguiente <i class="fas fa-chevron-right"></i>
      </button>
      <div class="pagination-info">
        <span>Página {{ currentPage }} de {{ totalPages }}</span>
      </div>
    </td>
  </tr>
</tfoot>

    </table>

    <!--MOdal para editar libro -->
    <v-overlay
            v-model="editModalVisible"
            z-index="10" 
            opacity="0.5" 
            color="white"
          >
      <v-dialog v-model="editModalVisible" max-width="800px">
      <v-card>
        <v-card-title>
          <span class="headline">Editar libro</span>
          <v-spacer></v-spacer>
        
        </v-card-title>

        <v-card-text>
          <v-form>
            <v-row>
              <v-col cols="12" md="6">
                <v-text-field label="Título" v-model="selectedLibro.titulo"></v-text-field>
                <v-text-field label="Autor" v-model="selectedLibro.autor"></v-text-field>
                <v-select
                  v-model="selectedLibro.estado"
                  :items="['Disponible', 'No Disponible']"
                  label="Estado"
                ></v-select>
                <v-text-field label="Género" v-model="selectedLibro.genero"></v-text-field>
                <v-text-field label="Stock" v-model="selectedLibro.stock" type="number"></v-text-field>
                <v-text-field label="ISBN" v-model="selectedLibro.isbn"></v-text-field>
              </v-col>
          
              <v-col cols="12" md="6">
                <v-text-field label="Páginas" v-model="selectedLibro.pag" type="number"></v-text-field>

                <v-text-field label="Imagen (URL)" v-model="selectedLibro.imagen"></v-text-field>
                <v-textarea label="Sinopsis" v-model="selectedLibro.sinopsis"></v-textarea>
                <v-text-field label="Año de publicación" v-model="selectedLibro.fecha" type="number"></v-text-field>
                <v-text-field label="Rating" v-model="selectedLibro.rating" type="number" step="0.1"></v-text-field>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-btn text @click="hideEditModal">Cancelar</v-btn>
          <v-btn color="primary" @click.prevent="updateBook(selectedLibro.id)">Guardar cambios</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-overlay>
    <!---Agregar-->
    <v-overlay
            v-model="addModalVisible"
            z-index="10" 
            opacity="0.5" 
            color="white"
    > 
    <v-dialog v-model="addModalVisible" max-width="800px">
      <v-card>
        <v-card-title>
          <span class="headline">Agregar libro</span>
          <v-spacer></v-spacer>
        
        </v-card-title>

        <v-card-text>
          <v-form>
            <v-row>
              <v-col cols="12" md="6">
                <v-text-field   :rules="[rules.required]" label="Título" v-model="newLibro.titulo"></v-text-field>
                <v-text-field   :rules="[rules.required]" label="Autor" v-model="newLibro.autor"></v-text-field>
                <v-select
                  v-model="newLibro.estado"
                  :rules="[rules.required]"
                  :items="['Disponible', 'No Disponible']"
                  label="Estado"
                ></v-select>
                <v-text-field   :rules="[rules.required]" label="Género" v-model="newLibro.genero"></v-text-field>
                <v-text-field  :rules="[rules.required]" label="Stock" v-model="newLibro.stock" type="number"></v-text-field>
                <v-text-field   :rules="[rules.required]" label="ISBN"  type="number" v-model="newLibro.isbn"></v-text-field>
              </v-col>
              <v-col cols="12" md="6">
                <v-text-field   :rules="[rules.required]" label="Páginas" v-model="newLibro.pag" type="number"></v-text-field>
                <v-text-field label="Imagen (URL)" v-model="newLibro.imagen"></v-text-field>
                <v-textarea label="Sinopsis" v-model="newLibro.sinopsis"></v-textarea>
                <v-text-field   :rules="[rules.required]" label="Año de publicación" v-model="newLibro.fecha" type="number"></v-text-field>
                <v-text-field label="Rating" v-model="newLibro.rating" type="number" step="0.1"></v-text-field>
              </v-col>
            </v-row>
          </v-form>
        </v-card-text>

        <v-card-actions>
          <v-btn text @click="hideAddModal">Cancelar</v-btn>
          <v-btn color="primary" @click.prevent="addBook">Agregar libro</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    </v-overlay>
         <!---Modal para ver mas info del libro-->
   
         <v-overlay
          v-model="showModalState"
          z-index="10" 
          opacity="0.5" 
          color="white"
        > 
          <v-dialog v-model="showModalState" max-width="500px">
            <v-card>
              <v-card-title>
                <span class="headline">Más información</span>
                <v-spacer></v-spacer>
          
              </v-card-title>

              <v-card-text>
                <v-list class="justify-center">
                  <v-list-item >
                    <v-list-item-content>
                      <v-list-item-title><b>Páginas:</b> {{ selectedLibro.pag }}</v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item>
                   <v-list-item-content>
                      <v-list-item-title><b>Año de publicación:</b> {{ selectedLibro.fecha }}</v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-content>
                      <v-list-item-title><b>Rating:</b> {{ selectedLibro.rating }}</v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-content>
                      <v-list-item-title><b>ISBN:</b> {{ selectedLibro.isbn }}</v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                 <v-list-item>
                  <v-list-item-content>
                     <p><b>Sinopsis:</b> {{ selectedLibro.sinopsis }}</p>
                  </v-list-item-content>
                 </v-list-item>
                  
                  <v-list-item>
                    <v-list-item-content>
                      <p><b>Imagen:</b> {{ selectedLibro.imagen }}</p>
                    </v-list-item-content>
                  </v-list-item>
                  
                </v-list>
              </v-card-text>

              <v-card-actions>
                <v-btn text @click="hideModal">Cerrar</v-btn>
              </v-card-actions>
            </v-card>
          </v-dialog>
        </v-overlay>

   <!--Modal eliminar-->
   <v-overlay
          v-model="deleteModalVisible"
          z-index="10" 
          opacity="0.5" 
          color="white"
        >
        <v-dialog v-model="deleteModalVisible" max-width="500px">
          <v-card>
            <v-card-title>
              <span class="headline">Eliminar libro</span>
              <v-spacer></v-spacer>
            
            </v-card-title>

            <v-card-text>
              <p>¿Estás seguro de que deseas eliminar el libro "{{ selectedLibro.titulo }}"?</p>
            </v-card-text>

            <v-card-actions>
              <v-btn text @click="hideDeleteModal">Cancelar</v-btn>
              <v-btn color="red" @click="confirmDelete(selectedLibro.id)">Eliminar</v-btn>
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
  </div>

</template>

<script>
import axios from 'axios';
import _ from 'lodash';
import moment from 'moment';
export default {
  data() {
    return {
      libros: [],
      rules: {
        required: value => !!value || 'Campo obligatorio',
      },
      columns: ['ID', 'Título', 'Autor', 'Género', 'Stock', 'Estado'],
      sortColumn: null,
      ascending: true,

      addModalVisible: false,
      newLibro: {
        titulo: '',
        autor: '',
        estado: '',
        genero: '',
        imagen: '',
        sinopsis: '',
        fecha: '',
        rating: '',
        stock: '',
        isbn: '',
        pag: '',
      },
      buscar: {
        cant: null,
        title: '',
        author: '',
        genre: '',
      },
      pageSizes: [5, 10, 15, 20],

      pageSizeOptions: {
        1: 5,
        2: 10,
        3: 15,
        4: 20,
   
    },
      selectedLibro: {},
      copyLibro: {},

      showModalState: false,
      editModalVisible: false,

      deleteModalVisible: false,
      selectedLibroToDelete: {},

      currentPage: 1,
      perPage: 5,

      showSnackbar: false,
      snackbarColor: '',
      snackbarMessage: '',

    }
  },
  mounted() {
    axios.get('book/list')
     .then(response => {
        this.libros = response.data.filter(libro => libro.estado !== 'Archivado');
        this.selectedLibro= this.libros;
      })
     .catch(error => {
        console.error(error);
      });
      this.resetForm();
  },

  computed: {
    totalPages() {
      return Math.ceil(this.libros.length / this.perPage);
    },
    sortedPaginatedLibros() {
    return this.sortedLibros.slice((this.currentPage - 1) * this.perPage, this.currentPage * this.perPage);
    },
  sortedLibros() {
      if (!this.sortColumn) return this.libros;

      // asignar los nombres de los encabezados con los valores de lbros
      const columnMap = {
        'ID': 'id',
        'Título': 'titulo',
        'Autor': 'autor',
        'Páginas':'pag',
        'Género':'genero',
        'Stock':'stock',
        'Imagen':'imagen',
        'Sinopsis':'sinopsis',
        'Rating':'rating',
        'Estado':'estado',
        'ISBN':'isbn',
        'Año de publicación':'fecha',
        'Estado':'estado',
     
      };

      const sortField = columnMap[this.sortColumn];

      return [...this.libros].sort((a, b) => {
        const aValue = this.getNestedValue(a, sortField);
        const bValue = this.getNestedValue(b, sortField);

        /*
        if (this.sortColumn === 'Año de publicación') {
          const dateA = moment(aValue, 'DD-MM-YYYY').toDate().getFullYear;
          const dateB = moment(bValue, 'DD-MM-YYYY').toDate().getFullYear;
          return this.ascending ? dateA - dateB : dateB - dateA;
        }*/

        if (aValue < bValue) return this.ascending ? -1 : 1;
        if (aValue > bValue) return this.ascending ? 1 : -1;
        return 0;
      });
    },
    paginatedLibros() {
    const startIndex = (this.currentPage - 1) * this.perPage;
    const endIndex = startIndex + this.perPage;
    return this.sortedLibros.slice(startIndex, endIndex);
  }
  },
  methods: {

    async fetchBooks() {
      try {
        const response = await axios.get('/book/list');
        this.libros = response.data.filter(libro => libro.estado !== 'Archivado');;
      } catch (error) {
        console.error(error);
      }
    },

    async searchBooks() {
      const params = {};
      if (this.buscar.title) {
        params.titulo = this.buscar.title.toLowerCase();
      }
      if (this.buscar.author) {
        params.autor = this.buscar.author.toLowerCase(); 
      }
      if (this.buscar.genre) {
        params.genero = this.buscar.genre.toLowerCase(); 
      }
      if (this.buscar.cant !== null) {
        params.stock = this.buscar.cant;
      }

      try {
        const response = await axios.get('/book/search', { params });
        this.libros = response.data; 
        const libros = response.data.filter(libro => libro.estado !== 'Archivado');
        this.libros = libros; 
        this.resetPagination();

      } catch (error) {
        console.error(error);
      }
    },
    
    async updateBook(id) {
      try {
        const libro = {
          titulo : this.selectedLibro.titulo,
          autor: this.selectedLibro.autor,
          pag: this.selectedLibro.pag,
          genero: this.selectedLibro.genero,
          rating: this.selectedLibro.rating,
          estado: this.selectedLibro.estado,
          sinopsis: this.selectedLibro.sinopsis,
          imagen: this.selectedLibro.imagen,
          stock: this.selectedLibro.stock,
          fecha: this.selectedLibro.fecha,
          isbn: this.selectedLibro.isbn,
        }
        const response = await axios.put(`book/update/${id}`, libro);
       
        if(response.status === 200) {
          this.showSuccessSnackbar("Libro actualizado.");
          this.editModalVisible = false;
          location.reload();
        } else {
          this.showErrorSnackbar("No se ha actualizado. Intente más tarde.");
        }
      } catch (error) {
        console.error(error);
        this.message = "Error al actualizar el libro.";
        this.snackbar = true;
      }
    },
  
    async addBook() {
      try {
        const libro = {
          titulo: this.newLibro.titulo,
          autor: this.newLibro.autor,
          genero: this.newLibro.genero,
          pag: this.newLibro.pag,
          isbn: this.newLibro.isbn,
          imagen: this.newLibro.imagen,
          sinopsis: this.newLibro.sinopsis,
          estado: this.newLibro.estado,
          stock: this.newLibro.stock,
          rating: this.newLibro.rating,
          fecha: this.newLibro.fecha,
        };

        const response = await axios.post("book/save", libro);

        if (response.status === 200) {
          this.showSuccessSnackbar("Libro agregado.");
          this.libros.push(libro);
          this.addModalVisible = false;
          location.reload();
        } else {
          this.showErrorSnackbar("Error al agregar el libro.");
        }
      } catch (error) {
        console.error(error);
        this.showErrorSnackbar("Algo ha fallado.");
      }

    },

    async refreshBooks() {
      try {
        const response = await axios.get('book/activeList');
        this.libros = response.data;
        this.resetPagination(); 
      } catch (error) {
        console.error(error);
      }
    },

    resetPagination() {
    this.currentPage = 1; 
  },async confirmDelete(id) {
  try {
    const response = await axios.delete(`book/delete/${id}`);

    if (response.status === 204) {
      this.showSuccessSnackbar("Libro eliminado.");
      this.hideEditModal = true; // Oculta modal
      location.reload();
    } else if (response.status === 409) { 
      this.showErrorSnackbar("No se puede eliminar el libro, se encuentra prestado."); 
      this.showErrorSnackbar("Error desconocido. Intente más tarde."); 
    }
  } catch (error) {
    if (error.response && error.response.data) {
      this.showErrorSnackbar("No se puede eliminar el libro, se encuentra prestado."); 
    } else {
      this.showErrorSnackbar("Error. Intente más tarde."); 
    }
  }
},
    sortTable(column) {
      if (this.sortColumn === column) {
        this.ascending = !this.ascending;
      } else {
        this.sortColumn = column;
        this.ascending = true;
      }
    },

    getNestedValue(obj, path) {
      return path.split('.').reduce((o, key) => (o && o[key] !== 'undefined') ? o[key] : null, obj);
    },

    async resetForm() {
  
      this.buscar = {
        title: '',
        author: '',
        genre: '',
        cant: null
      };
      axios.get('book/activeList')
     .then(response => {
        this.libros = response.data;
        this.selectedLibro= this.libros;
      })
     .catch(error => {
        console.error(error);
      });
  },

    openAddModal() {
      this.addModalVisible = true;
    },

    hideAddModal() {
      this.addModalVisible = false;
    },

    deleteItem(libro) {
      this.selectedLibro = libro;
      this.deleteModalVisible = true;
    },

    hideDeleteModal() {
      this.deleteModalVisible = false;
      this.selectedLibroToDelete = {};
    },

    showModal(libro) {
      this.selectedLibro = libro;
      this.showModalState = true;
    },

    hideModal() {
      this.showModalState = false;
    },

    openEditModal(libro) { 
      //copio libro para selected por si modifica
      // y en copylibro por si cierra sin guardar o modifica sin guardar
      this.selectedLibro = {...libro}; //copiar 
      this.copyLibro = {...libro};
      this.editModalVisible = true;
    },
    
    hideEditModal(){
      this.editModalVisible = false;
      this.selectedLibro = {...this.copyLibro}; //resetea al original
    },

    updatePageSize(pageNumber) {
    this.perPage = this.pageSizeOptions[pageNumber];
    this.currentPage = 1; 
  },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.paginatedLibros = this.sortedLibros.slice((this.currentPage - 1) * this.perPage, this.currentPage * this.perPage);
      }
    },

    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        this.paginatedLibros = this.sortedLibros.slice((this.currentPage - 1) * this.perPage, this.currentPage * this.perPage);
      }
    },
    formatDate(date) {
    const momentDate = moment.utc(date, 'YYYY-MM-DDTHH:mm:ss.sssZ');
    const year = momentDate.year();
    const month = momentDate.month() + 1;
    const day = momentDate.date();

    return `${day}-${month}-${year}`;
    },

    async exportCSV(){
      try {
        const response = await axios.get("/book/exportCsv", { responseType: 'blob' });
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'libros-biblioteca.csv');
        document.body.appendChild(link);
        link.click();
      } catch(error) {
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

.table-actions {
  padding: 10px;
  margin-bottom: 10px;
}
.col-md-2 {
  font-size: 15px;
}
.float-right {
  float: right;
}
.boton{
  text-align: center;
  border: none;
  border-radius: 5px;
  background-color: #0da351;
  color: #fff;
  cursor: pointer;
  padding: 8px;
}
.boton:hover{
  background-color: #40ca7e;
}
.botn{
  background-color: #d4d6d5;
  cursor: pointer;
  text-align: center;
  border: none;
  border-radius: 5px;
  padding: 8px;
  margin-right: 5px;
  margin-left:10px;
}
.botn:hover{
  background-color: #888;
}
.boton1{

  cursor: pointer;
  text-align: center;
  border: none;
  border-radius: 5px;
  padding: 8px;
}
.boton1:hover{
  background-color: #40ca7e;
}
.title-arrow-wrapper {
  display: contents;
  align-items: center;
  width: 100%;
  padding: 0;
  font-size: medium
}

.title {
  flex: 1;

}

.arrow {
  display: inline-block;
  vertical-align: middle;
  font-size: 18px;
  font-weight: bold;
  margin-left: 5px;
}
.arrow-up::before {
  content: "\2191"; 
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.arrow-down::before {
  content: "\2193";
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}
.table {
  font-size: 0.9rem;
}
.table td, .table th {
  padding: 0.25rem;
}
.table tr {
  cursor: pointer;
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 5px;
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.pagination-controls {
  display: flex;
  align-items: left;
}

.form-select {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  width: 60px;

}
.cancel {

  text-align: center;
  border: none;
  border-radius: 5px;
  background-color: #ccc;
  color: #fff;
  cursor: pointer;
  padding: 8px;
}
.btn {
  text-align: center;
  border: none;
  border-radius: 5px;
  background-color: #337ab7;
  color: #fff;
  cursor: pointer;
}

.btn-secondary {
  background-color: #4f4cff;
}

.btn-secondary:hover {
  background-color: #4176c5;
}

.btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pagination-info {
  margin-left: 10px;
  font-size: 13px;

}

.fas {
  font-size: 12px;
  margin: 0 5px;
}
.modal {
  display: none;
  position: fixed;
  z-index: 1;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgba(0, 0, 0, 0.4);
}

.modal.show {
  display: block;
}

.modal-dialog {
  margin: 10px auto;
  max-width: 400px;
  border-radius: 10px;
  padding: 10px;

  background-color: #f9f9f9;
  border: 1px solid #888;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
}
.modal-dialog2 {
  margin: 10px auto;
  max-width: 800px;
  border-radius: 10px;
  padding: 10px;
  background-color: #f9f9f9;
  border: 1px solid #888;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
}
.modal-content {
  padding: 20px;
}

.modal-header {
  padding: 10px;
  border-bottom: 1px solid #ddd;
}

.modal-body {
  padding: 20px;
  word-break: break-word;
  overflow-y: auto;
}
.modal-body li{
 display: block; 
}

.modal-footer {
  padding: 10px;
  border-top: 1px solid #ddd;
}
</style>