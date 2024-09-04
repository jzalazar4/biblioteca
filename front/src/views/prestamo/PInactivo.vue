<template>
  <head>
    <meta name='viewport' content='width=device-width, initial-scale=1'>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
  </head>
  
    <div class="float-left"  style="margin-bottom: 10px;">
      <button  
        @click="exportCSV" 
        class="btn btn-light">
        Exportar CSV 
        <i class='fas fa-file-csv' style='font-size:24px'></i>
      </button>
      <br>
    </div>
    <br>
      <table class="table table-condensed table-sm">
        <thead>
          <tr class="title-arrow-wrapper">
       
          <th @click="sort('id')">ID
            <i v-if="sortColumn === 'id'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th @click="sort('user.nombre')">Usuario
            <i v-if="sortColumn === 'user.nombre'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th @click="sort('libro.titulo')">Libro
            <i v-if="sortColumn === 'libro.titulo'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th @click="sort('finicio')">Fecha préstamo
            <i v-if="sortColumn === 'finicio'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th @click="sort('ffin')">Fecha devolución
            <i v-if="sortColumn === 'ffin'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th></th>
        </tr>

        </thead>
        <tbody>
          <tr 
            v-for="(prestamo, index) in paginatedPrestamos" 
              :key="prestamo.id"
              data-toggle="collapse"
              :data-target="`#demo${index}`" 
              class="accordion-toggle">

            <td>{{ prestamo.id }}</td>
            <td>{{ prestamo.user.nombre }} {{prestamo.user.apellido}}</td>
            <td>{{ prestamo.libro.titulo }}</td>
            <td>{{ prestamo.finicio }}</td>
            <td> {{prestamo.ffin }}</td>
  
            <td>

              <v-btn  size="x-small" icon @click="openUpdateDialog(prestamo)" style="margin-right:5px;">
              <box-icon name='edit-alt' color='#d8bf0e' title="Editar"></box-icon>
            </v-btn>

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
   
  <!-- Editar modal -->
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
              :item-text="(user) => `${user.nombre} ${user.apellido}`"
              item-value="id"
              item-title="nombre"
              label="Usuario"
              return-object
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
            <v-text-field
              v-model="selectedPrestamo.ffin"
              label="Fecha devolución"
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

</template>
  
<script>
  import axios from 'axios';
  import moment from 'moment';
  import store from '@/store/store';

  export default {
    data() {
      return {
        prestamos: [],
        users: [],
        books: [],
  
        returnModalVisible: false,
        addModalVisible: false,
        newPrestamo: {
          finicio: '',
        },
  
        selectedUser: null,
        selectedLibro: null,
        selectedPrestamo: {},
        copyPrestamo: {},
        selectedId: null,
        
        updateDialog: false,

        showModalState: false,
        editModalVisible: false,
  
        currentPage: 1,
        perPage: 5,
  
        showSnackbar: false,
        snackbarColor: '',
        snackbarMessage: '',
  
        sortColumn: 'id',
        sortOrder: 'asc',
        ascending:true,
      }
    },
    mounted() {
      //Prestamos
      axios.get('inactivo/list')
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
        .catch(error => console.error(error));
        //libros
      axios.get('book/list')
        .then(response => {
          this.books = response.data;
         
        })
        .catch(error => console.error(error));
  },
  
  computed: {
    formattedFinicio: {
      get() {
        return moment(this.selectedPrestamo.finicio).format('YYYY-MM-DD'); 
      },
      set(newValue) {
        this.selectedPrestamo.finicio = newValue;
      } 
    },
    formattedFfin: {
      get() {
        return moment(this.selectedPrestamo.ffin).format('YYYY-MM-DD');
      },
      set(newValue) {
        this.selectedPrestamo.ffin = newValue; 
      }
    },
    paginatedPrestamos() {
      const startIndex = (this.currentPage - 1) * this.perPage;
      const endIndex = startIndex + this.perPage;
      return this.sortedPrestamos.slice(startIndex, endIndex);
    },

    sortedPrestamos() {
      return this.prestamos.slice().sort((a, b) => {
        // lodash para ordenar las columnas de menor a mayor
        let aValue = _.get(a, this.sortColumn);
        let bValue = _.get(b, this.sortColumn);

        // ordena segun fechas
        if (this.sortColumn === 'finicio' || this.sortColumn === 'ffin') {
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
    totalPages() {
      return Math.ceil(this.prestamos.length / this.perPage);
    },

    },
    methods: {
      async updatePrestamo(id) {
        try {
       // const id = this.selectedPrestamo.id;
        console.log(id);
          //const fechaInicio = moment(this.selectedPrestamo.finicio, 'YYYY-MM-DD').format('DD-MM-YYYY');
          //const fechaDev = moment(this.selectedPrestamo.ffin, 'YYYY-MM-DD').format('DD-MM-YYYY');
          const fechaInicio = this.formatDate(new Date(this.selectedPrestamo.finicio));
          const fechaDev = this.formatDate(new Date(this.selectedPrestamo.ffin));
          
         const prestamo = {
    
            user : this.selectedPrestamo.user,
            libro: this.selectedPrestamo.libro,
            ffin: fechaDev,
            finicio: fechaInicio,
            //ffin: this.selectedPrestamo.ffin,
            //finicio : this.selectedPrestamo.finicio,
          };
          console.log(prestamo);
          const response = await axios.put(`inactivo/update/${id}`, prestamo);
          
          if(response.status === 200) {
            this.showSuccessSnackbar("Préstamo actualizado");
            this.hideEditModal(); //oculta modal   
            location.reload();

          } else {
            console.error(response);
            this.showErrorSnackbar("Algo ha fallado, intente más tarde.");

          }
         
        }catch(error){
          console.error(error);
          this.showErrorSnackbar("Algo ha fallado, intente más tarde.");

        }
      },
      async exportCSV(){
        try {
          const response = await axios.get("/inactivo/exportCsv", { responseType: 'blob' });
          const url = window.URL.createObjectURL(new Blob([response.data]));
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', 'prestamosinactivos-biblioteca.csv');
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
        const day = momentDate.date() + 1;
      
        return `${day}-${month}-${year}`;
      },

      openEditModal(prestamo) {
        this.selectedPrestamo = {
          id: prestamo.id,
          user: prestamo.user,
          libro: prestamo.libro,
          //ser: this.users.find(u => u.id === prestamo.user.id), // Aquí buscamos el usuario en la lista cargada
          //libro: this.books.find(b => b.id === prestamo.libro.id), // Aquí buscamos el libro en la lista cargada
          finicio: moment(prestamo.finicio, 'DD-MM-YYYY').format('YYYY-MM-DD'),
          ffin: moment(prestamo.ffin, 'DD-MM-YYYY').format('YYYY-MM-DD'),
          estado: prestamo.estado
        };
        this.editModalVisible = true;

      },

      hideEditModal(){
        this.selectedPrestamo = {}; //resetea al original
        this.updateDialog = false;
      },

      updatePagination() {
        this.currentPage = 1;
      },
      openUpdateDialog(prestamo) {
        this.userToUpdate = {...prestamo};
        this.selectedPrestamo.finicio =  moment(prestamo.finicio, 'DD-MM-YYYY').format('YYYY-MM-DD'),
        this.selectedPrestamo.ffin = moment(prestamo.ffin, 'DD-MM-YYYY').format('YYYY-MM-DD'),
        this.selectedPrestamo.id = prestamo.id;
        this.selectedPrestamo.user = this.users.find(u => u.id === prestamo.user.id);
        this.selectedPrestamo.libro = this.books.find(b => b.id === prestamo.libro.id);
        this.updateDialog = true;

      },
    
      prevPage() {
        this.currentPage--;
      },

      nextPage() {
        this.currentPage++;
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
  .table {
    font-size: 0.9rem;
  }
  .table td, .table th {
    padding: 0.25rem;
  }
  .table tr {
    cursor: pointer;
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
  @media (max-width: 768px) {
  .modal-dialog2 {
    margin-left: 0; 
  }
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
    margin-left: 250px;
    background-color: #f9f9f9;
    border: 1px solid #888;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  }
  .modal-content {
    padding: 20px;
    z-index: 1050;
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