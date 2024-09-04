<template>
    <head>
    <meta name='viewport' content='width=device-width, initial-scale=1'>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">

  </head>
  <div>
    <v-snackbar
        v-model="showSnackbar"
        :color="snackbarColor"
        :timeout="3000"
      >
        {{ snackbarMessage }}
    </v-snackbar>
  <div class="table-actions">
    <div class="float-left"  style="margin-bottom: 10px;">
      <button  @click="exportCSV" class="btn btn-light">Exportar CSV 
        <i class='fas fa-file-csv' style='font-size:24px' ></i>
      </button>

    </div>
    <div class="float-right"  style="margin-bottom: 10px;">
      <v-btn   @click="openAddDialog" >Nuevo
            </v-btn>
    </div>
  </div>
    <table class="table table-condensed table-sm">
      <thead>
        <tr class="title-arrow-wrapper">
          <th @click="sort('id')">ID
            <i v-if="sortColumn === 'id'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th @click="sort('nombre')">Nombre
            <i v-if="sortColumn === 'nombre'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th @click="sort('apellido')">Apellido
            <i v-if="sortColumn === 'apellido'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th @click="sort('telefono')">Teléfono
            <i v-if="sortColumn === 'telefono'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th @click="sort('email')">Email
            <i v-if="sortColumn === 'email'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
          <th @click="sort('role')">Rol
      
            <i v-if="sortColumn === 'role'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>
         
         <!-- <th @click="sort('estado')">Estado
            <i v-if="sortColumn === 'estado'" :class="sortOrder === 'asc' ? 'fas fa-arrow-up' : 'fas fa-arrow-down'"></i>
          </th>-->
          
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(user, index) in sortedPaginatedUsers" 
        :key="user.id"
        data-toggle="collapse" 
        :data-target="`#demo${index}`" 
        class="accordion-toggle">

          <td>{{ user.id }}</td>
          <td>{{ user.nombre }}</td>
          <td>{{ user.apellido }}</td>
          <td>{{ user.telefono }}</td>
          <td>{{ user.email }}</td>
          <td >{{ user.role }}</td>
        <!---  <td>{{ user.estado }}</td>-->
          
          <td>
            <v-btn  size="x-small" icon @click="openChangeRoleDialog(user)" style="margin-right:5px;">
              <box-icon name='user-check' color='#1b8117' title="Modificar rol"></box-icon>
            </v-btn>

            <v-btn  size="x-small" icon @click="openUpdateDialog(user)" style="margin-right:5px;">
              <box-icon name='edit-alt' color='#d8bf0e' title="Editar"></box-icon>
            </v-btn>
            
            <v-btn  size="x-small" icon @click="openDeleteDialog(user)" style="margin-right:5px;">
              <box-icon name='trash' type='solid' color='#c12424' title="Eliminar"></box-icon>
            </v-btn>
      
           
          </td>
          <td>
           
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
          <td colspan="8" class="float-center">
            
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

                        <!---agregar-->
  <v-overlay
        v-model="addModal"
        z-index="10" 
        opacity="0.5" 
        color="white"
      >
    <v-dialog v-model="addModal" max-width="400" overlay-opacity="0">
      <v-card>
        <v-card-title>Nuevo usuario</v-card-title>
        <v-card-text>
          <v-form>
            <v-text-field v-model="newUser.nombre" label="Nombre" :rules="[rules.required]"></v-text-field>
            <v-text-field v-model="newUser.apellido" label="Apellido" :rules="[rules.required]"></v-text-field>
            <v-text-field 
            type="phone" 
            v-model="newUser.telefono" 
            :rules="[
              v => !!v || 'Por favor, rellene este campo.',
              v => /^\d+$/.test(v) || 'Teléfono debe ser un número'
            ]"
            label="Teléfono" >
          </v-text-field>
           
            <v-text-field 
            v-model="newUser.email" 
            label="Email" 
            :rules="[
              v => !!v || 'Por favor, rellene este campo.',
              v => /.+@.+\..+/.test(v) || 'El email debe ser válido'
            ]">
          </v-text-field>
            
            <v-text-field
              v-model="newUser.password"
              label="Contraseña"
              ref="passwordInput"
              :type="passwordVisible ? 'text' : 'password'"
              :append-icon="passwordVisible ? 'fas fa-eye' : 'fas fa-eye-slash'"
              @click:append.prevent="passwordVisible = !passwordVisible"
              :rules="[rules.required]"
            ></v-text-field>
                  
            <v-select 
              v-model="newUser.role" 
              :items="['ADMIN', 'USER']"
              label="Rol"
              :rules="[rules.required]">
          </v-select>

          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn text @click="addModal = false">Cancelar</v-btn>
          <v-btn color="primary" @click="addUser">Aceptar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-overlay>
                  <!-- Actualizar user-->
  <v-overlay
          v-model="updateDialog"
          z-index="10" 
          opacity="0.5" 
          color="white"
        >
    <v-dialog v-model="updateDialog" max-width="600" overlay-opacity="0">
      <v-card>
        <v-card-title>Actualizar usuario</v-card-title>
        <v-card-text>
          <v-form>
            <v-text-field v-model="userToUpdate.nombre" label="Nombre" :rules="[rules.required]"></v-text-field>
            <v-text-field v-model="userToUpdate.apellido" label="Apellido" :rules="[rules.required]" ></v-text-field>
            <v-text-field type="phone" v-model="userToUpdate.telefono" label="Teléfono" :rules="[rules.required]"></v-text-field>
            <v-text-field v-model="userToUpdate.email" label="Email" :rules="[rules.required]"></v-text-field>
            
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn text @click="updateDialog = false">Cancelar</v-btn>
          <v-btn color="primary" @click="updateUser(userToUpdate.id)">Actualizar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-overlay>
  
                  <!-- Cambiar rol -->
  <v-overlay
          v-model="changeRoleDialog"
          z-index="10" 
          opacity="0.5" 
          color="white"
        >
  <v-dialog v-model="changeRoleDialog" max-width="400" >
    <v-card>
      <v-card-title>Modificar rol</v-card-title>
      <v-card-text>
        <v-select v-model="userToChangeRole.role" :items="roles" label="Rol" ></v-select>
      </v-card-text>
      <v-card-actions>
        <v-btn text @click="changeRoleDialog = false">Cancelar</v-btn>
        <v-btn color="primary" @click="cambiarRol(userToChangeRole.id)">Aceptar</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  </v-overlay>
            <!-- Eliminar -->
  <v-overlay
          v-model="deleteDialog"
          z-index="10" 
          opacity="0.5" 
          color="white"
        >
  <v-dialog v-model="deleteDialog" max-width="400" >
    <v-card>
      <v-card-title>Eliminar usuario</v-card-title>
      <v-card-text>
        ¿Desea eliminar a {{ userToDelete.nombre }} de los usuarios?
      </v-card-text>
      <v-card-actions>
        <v-btn text @click="deleteDialog = false">Cancelar</v-btn>
        <v-btn color="error" @click="confirmDelete(userToDelete.id)">Aceptar</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  </v-overlay>
  </div>


</template>

<script>
import axios from 'axios';
import store from '@/store/store';
import _ from 'lodash';
export default {
  data() {
    return {
      users: [],
      showOverlay: false,
    
      newUser: {
        nombre: '',
        apellido: '',
        email: '',
        telefono: '',
        role: '',
        password: ''
      },
      show3: false,
      rules: {
        required: value => !!value || 'Por favor, rellene este campo.',
       
      },
      dialog: false,
      updateDialog: false,
      addModal: false,
      changeRoleDialog: false,
      deleteDialog: false,
      userToUpdate: {},
      userToChangeRole: {},
      userToDelete: {},
      roles: ['ADMIN','USER'],

      selectedUser: {},
      copyUser: {},
      selectedId: null,
      
      showModalState: false,
      editModalVisible: false,
      addModalVisible: false,
      deleteModalVisible: false,
      modalRol: false,

      //columns: ['ID', 'Nombre', 'Apellido', 'Email', 'Teléfono', 'Confirmado', 'Rol', 'Modificar rol'],
      sortColumn: 'id',
        sortOrder: 'asc',
        ascending:true,

        passwordVisible:false,

      selectedUserToAdd: {},
      selectedUserToDelete: {},
      selectedUserToEdit: {},

      selectedUserRole: {},

      currentPage: 1,
      perPage: 5,

      showSnackbar: false,
      snackbarColor: '',
      snackbarMessage: '',

      loading: false,

    }
  },
  mounted() {
 
    axios.get("user/list")
      .then(response => 
      { this.users = response.data.filter(user => user.estado !=='Inactivo');
     })
      .catch(error => {
      console.log(error);
    })
  },

  computed: {
    totalPages() {
      return Math.ceil(this.users.length / this.perPage);
    },
    sortedUsers(){
      return this.users.slice().sort((a, b) => {
        let aValue = _.get(a, this.sortColumn);
        let bValue = _.get(b, this.sortColumn);

        if (this.sortOrder === 'asc') {
          return aValue > bValue ? 1 : aValue < bValue ? -1 : 0;
        } else {
          return aValue < bValue ? 1 : aValue > bValue ? -1 : 0;
        }
      });
    },

  sortedPaginatedUsers() {
    return this.sortedUsers.slice((this.currentPage - 1) * this.perPage, this.currentPage * this.perPage);
  },

  },
  methods: {
 
    async updateUser(id) {
      try {
   
        const usuario = {
          nombre : this.userToUpdate.nombre,
          apellido: this.userToUpdate.apellido ,
          telefono: this.userToUpdate.telefono ,
          email: this.userToUpdate.email ,
     
        }
        console.log(usuario);
        const response = await axios.put(`user/update/${id}`, usuario);
        this.loading = true;

        if(response.status === 200) {
          this.showSuccessSnackbar("Usuario actualizado");          

          const index = this.users.findIndex(user => user.id === id);
          this.users.splice(index, 1, usuario);
          store.commit('updateUser', this.selectedUser);
          this.updateDialog = false;
          location.reload();

        } else {
          console.error(response);
          this.showErrorSnackbar("Error al actualizar");
        }
       
      }catch(error){
        console.error(error);
        this.showErrorSnackbar("Algo ha fallado");
      }

    },
    async addUser() {
      try {
        console.log(this.newUser.role);

        const usuario = {
          nombre: this.newUser.nombre,
          apellido: this.newUser.apellido,
          telefono: this.newUser.telefono,
          email: this.newUser.email,
          role: this.newUser.role,
          password: this.newUser.password,
        };
        console.log(usuario);
        const response = await axios.post("user/saveUser", usuario);

        if (response.status === 200) {
          this.showSuccessSnackbar("Nuevo usuario agregado.");

          this.loading = true;

          //guardar al store
          this.users.push(usuario);
   
          this.loading = false;
          this.addModal = false;
          location.reload();
        } else {
          throw new Error(`Error al guardar el nuevo usuario: ${response.status}`);
        }
      } catch (error) {
        this.showErrorSnackbar("Error al guardar el nuevo usuario.");

        console.error(error);
        this.loading = false;
      }
    },
    async cambiarRol(id){
      console.log(id);
      const response = await axios.put(`user/role/${id}`)
        .then(response => {
          this.showSuccessSnackbar("Rol modificado");

          this.loading= true;
          this.changeRoleDialog = false;
          this.loading = false;
        })
        .catch(error => {
          console.error(error);
          this.showErrorSnackbar("Error al modificar el rol del usuario");
        });
    },
    async confirmDelete(id) {
      try {
        const response = await axios.delete(`user/delete/${id}`);

        if (response.status === 204) {
          this.showSuccessSnackbar("Usuario eliminado.");
          this.deleteDialog = false;
          location.reload();
        } else if (response.status === 200) {
          this.showSuccessSnackbar("Usuario inactivado.");
          this.deleteDialog = false;
          location.reload();
        } else if (response.status === 409) {
          this.showErrorSnackbar(response.data.message); // Mostrar mensaje de error personalizado
        }

      } catch (error) {
        if (error.response && error.response.data) {
          const { errorCode, message } = error.response.data;
          switch (errorCode) {
            case 'BAD_REQUEST':
              this.showErrorSnackbar(message);
              break;
            case 'INTERNAL_SERVER_ERROR':
              this.showErrorSnackbar(message);
              break;
            default:
              this.showErrorSnackbar("Algo ha fallado, el usuario puede tener registrado un préstamo.");
          }
        } else {
          console.error(error);
          this.showErrorSnackbar("Algo ha fallado");
        }
      }
    },
    async exportCSV(){
      try {
        const response = await axios.get("/user/exportCsv", { responseType: 'blob' });
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'usuarios-biblioteca.csv');
        document.body.appendChild(link);
        link.click();
      } catch(error) {
        console.log(error);
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

    hideAddModal() {
      this.addModalVisible = false;
      this.addModal = false;
      this.loading = false;
    },

    verPassword() {
      this.passwordVisible = !this.passwordVisible;
    },
   // modals
   openAddDialog() {
    this.addModal = true;
  },
   openUpdateDialog(user) {
    this.userToUpdate = user;
    this.updateDialog = true;
  },
  openChangeRoleDialog(user) {
    this.userToChangeRole = user;
    this.changeRoleDialog = true;
  },
  openDeleteDialog(user) {
    this.userToDelete = user;
    this.deleteDialog = true;
  },
   //

    updatePagination() {
      this.currentPage = 1;
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
  border: #0da351;
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