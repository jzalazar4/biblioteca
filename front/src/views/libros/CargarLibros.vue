<template>
  
    <div dense text class="info-sidebar">
      <h3>Información</h3>
      <p>{{ infoText }}
      <br>
        {{ advertencia }}
      </p>
    </div>
  <div class="file-upload">
    <div class="upload-area">
      <input type="file" @change="onFileChange" style="display: none" ref="fileInput" />
      <v-btn @click="$refs.fileInput.click()">Seleccionar archivo</v-btn>
      <ul v-if="selectedFiles.length > 0">
        <li v-for="(file, index) in selectedFiles" :key="index">
          {{ file.name }}
          <v-btn @click="removeFile(index)" icon>
            <box-icon name='x' color='#d81515' ></box-icon>
          </v-btn>
        </li>
      </ul>
      <v-btn @click="uploadFiles" :disabled="selectedFiles.length === 0 || uploading">Subir</v-btn>
      <v-progress-circular v-if="uploading" indeterminate color="primary"></v-progress-circular>
    </div>
  </div>
  <v-alert dense text type="info">
      Tamaño máximo de archivo: 2MB
    </v-alert>
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

export default {
  data() {
    return {
      selectedFiles: [],
      uploading: false,
      advertencia: null,
      showSnackbar: false,
      snackbarColor: '',
      snackbarMessage: '',
      infoText: 'Por favor, revisar los encabezados del archivo csv. Si no estan, o se encuentran en otro idioma, la información no se verá correctamente.',
    };
  },
  methods: {
    onFileChange(event) {
    const files = event.target.files;
    this.selectedFiles = []; // borra los archivos previos
    for (let i = 0; i < files.length; i++) {
      this.selectedFiles.push(files[i]);
    }
    this.advertencia = `Archivos seleccionados: ${this.selectedFiles.length}`;
  },

  removeFile(index) {
    this.selectedFiles.splice(index, 1);
    this.advertencia = `Archivos seleccionados: ${this.selectedFiles.length}`;
  },
  async uploadFiles() {
    this.uploading = true;
    if (this.selectedFiles.length === 0) {
      alert('Seleccione un archivo para subir.');
      this.uploading = false;
      return;
    }

    const formData = new FormData();
    this.selectedFiles.forEach(file => {
      formData.append('files', file);
    });

    try {
      this.advertencia = `Archivos seleccionados: ${this.selectedFiles.length}`;

      const response = await axios.post('book/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      
      // si la respuesta es 200 ok manda mensajes de exito
      if (response.status === 200) {
        //alert('Archivos cargados exitosamente.');
        this.showSuccessSnackbar("Archivos cargados");
        this.selectedFiles = []; // borra los archivos de la cola
      } else {
        throw new Error('Error en la carga de archivos.');
      }
    } catch (error) {
      console.error('Error al cargar los archivos:', error);
      this.showErrorSnackbar("Error al cargar los archivos");
      //alert('Error al cargar los archivos');
    } finally {
      this.uploading = false; 
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

  },
};
</script>

<style scoped>
.file-upload {
  margin: 20px;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  display: flex;
}

.upload-area {
  flex: 1;
  margin-right: 20px;
}

.info-sidebar {
  flex: 0 0 200px;
  padding: 20px;
  border-left: 1px solid #ccc;
}

.upload-area button {
  margin: 10px;
}

.v-progress-circular {
  margin: 20px;
}

ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

li {
  padding: 10px;
  border-bottom: 1px solid #ccc;
}

li:last-child {
  border-bottom: none;
}
</style>