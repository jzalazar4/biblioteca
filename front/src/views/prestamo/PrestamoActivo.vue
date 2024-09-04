<template>
  <head>
    <meta name='viewport' content='width=device-width, initial-scale=1'>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
  </head>
  <div>
   
    <div class="float-left" style="margin-bottom: 10px;">
      <button  @click="exportCSV" class="btn btn-light">Exportar CSV <i class='fas fa-file-csv' style='font-size:24px'></i></button>

    </div>
    <div class="float-right">
      <v-btn @click="openAddModal()" >Nuevo</v-btn>   
    </div>

  <br>
    <table class="table table-condensed table-sm">
      <thead>
       
        <tr  class="title-arrow-wrapper">
 
          <th @click="sort('id')">ID
            <i v-if="sortColumn === 'id'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th @click="sort('user.apellido')">Usuario
            <i v-if="sortColumn === 'user.apellido'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th @click="sort('libro.titulo')">Libro
            <i v-if="sortColumn === 'libro.titulo'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th @click="sort('finicio')">Fecha préstamo
            <i v-if="sortColumn === 'finicio'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
      
          <th></th>
     
        </tr>
      </thead>
      <tbody>
        <tr v-for="(prestamo, index) in  sortedPaginatedPrestamos" :key="prestamo.id" data-toggle="collapse"
         :data-target="`#demo${index}`" class="accordion-toggle">
          <td>{{ prestamo.id }}</td>
          <td>{{ prestamo.user.nombre }} {{prestamo.user.apellido}}</td>
          <td>{{ prestamo.libro.titulo }}</td>
          <td>{{ prestamo.finicio }}</td>

         <td>
          
          <div class="d-grid gap-6 d-md-block">
            <v-btn size="x-small" icon  @click="openReturnModal(prestamo)" style="margin-right:5px;">
              <box-icon name='book-reader' color='#58b739' ></box-icon>
            </v-btn>
     
            <v-btn  size="x-small" icon @click="openUpdateDialog(prestamo)" style="margin-right:5px;">
              <box-icon name='edit-alt' color='#d8bf0e' title="Editar"></box-icon>
            </v-btn>
            <!---            <v-btn  size="x-small" icon @click="deleteItem(prestamo)" style="margin-right:5px;">
              <box-icon name='trash' type='solid' color='#c12424' title="Eliminar"></box-icon>
            </v-btn>-->

    </div>
         </td>
        </tr>
     
      </tbody>
      <tfoot>
    
        <tr>
          <td>
          
              <div class="pagination-controls">
                <select v-model="perPage" @change="updatePagination" class="form-select">
                  <option value="5">5</option>
                  <option value="10">10</option>
                  <option value="15">15</option>
                  <option value="20">20</option>
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
                                       <!-- Devolver libro -->
    <v-overlay
          v-model="returnModalVisible"
          z-index="10" 
          opacity="0.5" 
          color="white"
        > 
    <v-dialog v-model="returnModalVisible" max-width="400">
      <v-card>
        <v-card-title>Devolver libro</v-card-title>
        <v-card-text>¿Estás seguro de que deseas devolver el libro?</v-card-text>
        <v-card-actions>
          <v-btn @click="hideReturnModal">Cancelar</v-btn>
          <v-btn color="primary" @click.prevent="returnBook">Devolver</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    </v-overlay>
                                  <!--Agregar nuevo-->
    <v-overlay
      v-model="addModalVisible"
      z-index="10" 
      opacity="0.5" 
      color="white"
        >
      <v-dialog v-model="addModalVisible" max-width="600">
        <v-card>
          <v-card-title>Nuevo préstamo</v-card-title>
          <v-card-text>
            <form>

              <v-select
                v-model="newPrestamo.user"
                item-title="nombre"
                :items="listUser"
                label="Usuario"
                return-object
                @change="selectUser($event)"
              />

              <v-select
                v-model="newPrestamo.book"
                label="Libro"
                item-title="titulo"
                :items="booksInStock"
                return-object
                @change="selectLibro($event)"
              />
                    <v-text-field
                      type="date"
                      label="Fecha de préstamo"
                      v-model="newPrestamo.finicio"
                    ></v-text-field>

        
            </form>
          </v-card-text>
          <v-card-actions>
            <v-btn @click="hideAddModal">Cancelar</v-btn>
            <v-btn color="primary" @click.prevent="addPrestamo()">Agregar</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-overlay>
    <!---Eliminar
    <v-overlay
        v-model="deleteModalVisible"
        z-index="10" 
        opacity="0.5" 
        color="white"
      >
      <v-dialog v-model="deleteModalVisible" max-width="400" >
        <v-card>
          <v-card-title>Eliminar usuario</v-card-title>
          <v-card-text>
            ¿Desea eliminar a {{ selectedPrestamo.user.nombre }} de los usuarios?
          </v-card-text>
          <v-card-actions>
            <v-btn text @click="hideDeleteModal">Cancelar</v-btn>
            <v-btn color="error" @click.prevent="confirmDelete(selectedPrestamo.id)">Aceptar</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-overlay>
-->
   
    <!-- Actualizar user-->
    <v-overlay
            v-model="updateDialog"
            z-index="10" 
            opacity="0.5" 
            color="white"
          >
      <v-dialog v-model="updateDialog" max-width="600px">
        <v-card>
          <v-card-title>Editar préstamo</v-card-title>
          <v-card-text>
            <form>
              <v-select
                v-model="selectedPrestamo.user"
                :items="users"
              return-object

                :item-text="(user) => `${user.nombre} ${user.apellido}`"
                item-value="id"
                item-title="nombre"
                label="Usuario"
                :value="selectedPrestamo.user"
              >
                {{  selectedPrestamo.user.nombre }} {{ selectedPrestamo.user.apellido }}
            </v-select>
              <v-select
                v-model="selectedPrestamo.libro"
                :items="books"
                :item-text= "(libro) => `${libro.titulo}`"
                item-value="id"
                item-title="titulo"
                label="Libro"
              return-object

                :value="selectedPrestamo.libro"
              >
                {{ selectedPrestamo.libro.titulo}}
            </v-select>
              <v-text-field
                v-model="selectedPrestamo.finicio"
                label="Fecha préstamo"
                type="date"
              ></v-text-field>
        
            </form>
          </v-card-text>
          <v-card-actions>
            <v-btn text @click="hideEditModal">Cancelar</v-btn>
            <v-btn color="primary" @click.prevent="updatePrestamo(selectedPrestamo.id)">Guardar cambios</v-btn>
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
import store from '@/store/store';
import moment from 'moment';
import _ from 'lodash';

export default {
  data() {
    return {
      prestamos: [],
      users: [],
      books: [],
     
      returnModalVisible: false,
      addModalVisible: false,
      newPrestamo: {
        userId: null,
        libroId: null,
        finicio: null,
      },
      
      //columns: ['ID', 'Usuario', 'Libro', 'Fecha préstamo'],
      sortColumn: 'id',
      sortOrder: 'asc',
      ascending:true,

      selectedUser: null,
      selectedLibro: null,
      selectedPrestamo: {},
      copyPrestamo: {},
      selectedId: null,
      updateDialog: false,

      showModalState: false,
      editModalVisible: false,
      selectedPrestamo: {},

      deleteModalVisible: false,
      selectedPrestamoToDelete: {},

      currentPage: 1,
      perPage: 5,

      showSnackbar: false,
      snackbarColor: '',
      snackbarMessage: '',

    }
  },
  mounted() {
    //Prestamos
    axios.get('prestamo/list')
     .then(response => {
        this.prestamos = response.data;
        this.selectedPrestamo= this.prestamos;

      })
     .catch(error => {
        console.error(error);
      });
      //usuarios
      axios.get('userlist')
      .then(response => {
        this.users = response.data;
      })
      .catch(error => {
        console.error(error);
      });
      //libros
      axios.get('book/list')
      .then(response => {
        this.books = response.data;
        
      })
      .catch(error => {
        console.error(error);
      });
  },

  computed: {
    totalPages() {
      return Math.ceil(this.prestamos.length / this.perPage);
    },
    paginatedUsers() {
      return this.prestamos.slice(
        (this.currentPage - 1) * this.perPage,
        this.currentPage * this.perPage
      );
    },
     listUser() {
      return this.users.filter(user => user.role !== 'ADMIN' && user.estado !=='Inactivo');
     },
     // filtra libros para ver con stock > 0
     booksInStock() {
        return this.books.filter(book => book.stock > 0 && book.estado !== 'Archivado');
     },
     sortedPrestamos() {
      return this.prestamos.slice().sort((a, b) => {
        // lodash para ordenar las columnas de menor a mayor
        let aValue = _.get(a, this.sortColumn);
        let bValue = _.get(b, this.sortColumn);

        // ordena segun fechas
        if (this.sortColumn === 'finicio') {
          aValue = moment(aValue, 'dd-MM-yyyy').toDate();
          bValue = moment(bValue, 'dd-MM-yyyy').toDate();
        }  
        
        if (this.sortOrder === 'asc') {
          return aValue > bValue ? 1 : aValue < bValue ? -1 : 0;
        } else {
          return aValue < bValue ? 1 : aValue > bValue ? -1 : 0;
        }
      });    
  },
  sortedPaginatedPrestamos() {
    return this.sortedPrestamos.slice((this.currentPage - 1) * this.perPage, this.currentPage * this.perPage);
    },
},
  methods: {
    async updatePrestamo(id) {
      try {
       // const id = this.selectedPrestamo.id;
        const fecha = this.formatDate(new Date(this.selectedPrestamo.finicio));
        console.log(typeof fecha);
    // const fecha = moment().format(this.selectedPrestamo.finicio, 'MMMM Do YYYY, h:mm:ss a'); console.log(typeof fecha);
       const prestamo = {
         // user : this.selectedUser,
          //libro : this.selectedLibro,
          user : this.selectedPrestamo.user,
          libro: this.selectedPrestamo.libro,
          finicio : fecha,
        };

        const response = await axios.put(`prestamo/actualizar/${id}?fecha=${this.selectedPrestamo.finicio}`, prestamo);
        
        if(response.status === 200) {
        
        this.showSuccessSnackbar("Préstamo actualizado");
        this.hideEditModal(); //oculta modal 
        //store.commit('updateUser', this.selectedUser);

        location.reload();
        } else {
          console.error(response);
          this.showErrorSnackbar("Algo ha fallado en la actualización");
        }
       
      }catch(error){
        console.error(error);
        this.showErrorSnackbar("Algo ha fallado");
      }

    },
    async addPrestamo() {
      const fecha = this.formatDate(new Date(this.newPrestamo.finicio));
      try {
        const prestamo = {
          user: this.newPrestamo.user,
          libro: this.newPrestamo.book,
          finicio: fecha,
          estado: 'Activo',
        };
        
        const response = await axios.post("prestamo/save", prestamo);

        if (response.status === 200) {
          this.showSuccessSnackbar("Préstamo agregado exitosamente.");
          this.addModalVisible = false;
          location.reload();
        }
      } catch (error) {
        if (error.response && error.response.data) {
          const { errorCode, message } = error.response.data;

          switch (errorCode) {
            case 'LIBRO_NO_DISPONIBLE':
              this.showErrorSnackbar("El libro no está disponible para préstamo.");
              break;
            case 'MAX_PRESTAMOS':
              this.showErrorSnackbar("El usuario ya tiene cinco préstamos activos.");
              break;
            case 'LIBRO_YA_PRESTADO':
              this.showErrorSnackbar("El libro está actualmente prestado al usuario.");
              break;
            case 'USER_NOT_FOUND':
              this.showErrorSnackbar("Usuario no encontrado.");
              break;
            default:
              this.showErrorSnackbar("Ocurrió un error inesperado.");
              break;
          }
        } else {
          this.showErrorSnackbar("Error al realizar el préstamo.");
        }
      }
},

  async returnBook(){
    try{
      const id = this.selectedPrestamo.id;
      console.log("ID prestamo: ", id);
    
       const response = await axios.put(`prestamo/devolver/${id}`);
       
       if(response.status === 200) {
        this.showSuccessSnackbar("Libro devuelto");
        this.prestamos = this.prestamos.filter(prestamo => prestamo.id !== id);
        this.returnModalVisible = false;
        location.reload();
       } else {
        this.showErrorSnackbar("Intente más tarde");
       }
    
       
      }catch(error) {
          console.error(error);
          this.showErrorSnackbar("Algo ha fallado");
      }
  },
  /*
    async confirmDelete(id){
      try{
        //const id = this.selectedPrestamo.id;
       const response = await axios.delete(`book/delete/${id}`);

          this.showSuccessSnackbar("Préstamo eliminado");
          this.hideDeleteModal(); //oculta modal 
          location.reload();
      }catch(error) {
          console.error(error);
          this.showErrorSnackbar("Algo ha fallado");
      }
  },*/
  async exportCSV(){
        try {
          const response = await axios.get("/prestamo/exportCsv", { responseType: 'blob' });
          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', 'prestamosactivos-biblioteca.csv');
          document.body.appendChild(link);
          link.click();
          this.showSuccessSnackbar("Csv descargado");
        } catch(error) {
          console.log(error);
          this.showErrorSnackbar("No se pudo descargar el csv.");
        }
    },
    sort(column) {
        if (this.sortColumn === column) {
          this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
        } else {
          this.sortColumn = column;
          this.sortOrder = 'asc';
        }
    },
    
    formatDate(date) {
    const momentDate = moment.utc(date, 'YYYY-MM-DDTHH:mm:ss.sssZ');
    const year = momentDate.year();
    const month = momentDate.month() + 1;
    const day = momentDate.date();

    return `${day}-${month}-${year}`;
    },
    selectUser(userId) {
      const user = this.listUser.find(u => u.id === userId);
      this.newPrestamo.user = user;
    },

    selectLibro(libroId) {
      const libro = this.booksInStock.find(b => b.id === libroId);
      this.newPrestamo.libro = libro;
    },
    openAddModal() {
      this.newPrestamo = {
      finicio: null,
      user: null,
      libro: null,
      };
      this.addModalVisible = true;
      /*this.newPrestamo = {
        user: this.users.find(u => u.id === prestamo.user.id),
        libro: this.books.find(b => b.id === prestamo.libro.id),
        finicio: null,
      };*/
    },
    hideAddModal() {
      this.addModalVisible = false;
    },
    deleteItem(prestamo) {
      this.selectedPrestamo = prestamo;
      this.deleteModalVisible = true;
    },
    hideDeleteModal() {
      this.selectedPrestamoToDelete = {};
      this.deleteModalVisible = false;
    },

    showModal(prestamo) {
      this.selectedPrestamo = prestamo;
      this.showModalState = true;
    },
    hideModal() {
      this.showModalState = false;
    },
    openReturnModal(prestamo){
      this.selectedPrestamo = prestamo;
      this.returnModalVisible = true;
    }, 
     openUpdateDialog(prestamo) {
        this.selectedPrestamo = {
          id: prestamo.id,
          user: prestamo.user,
          libro: prestamo.libro,
          finicio: moment(prestamo.finicio, 'DD-MM-YYYY').format('YYYY-MM-DD'),
        };
        this.updateDialog = true;

      },

      hideEditModal(){
        this.selectedPrestamo = {}; //resetea al original
        this.updateDialog = false;
      },

    hideReturnModal(){
      this.returnModalVisible = false;
    
    },
    updatePagination() {
      this.currentPage = 1;
    },
    prevPage() {
      this.currentPage--;
    },
    nextPage() {
      this.currentPage++;
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

.d-flex {
  display: flex;
}
.justify-content-between {
  justify-content: center;
}

.table-actions {
  padding: 10px;
  border-bottom: 1px solid #ddd;
  margin-bottom: 10px;
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