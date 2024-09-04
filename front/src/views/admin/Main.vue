<template>
  <head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />

  </head>
  <div>
 
      <hr />
      <v-row class="contador">
        <v-col cols="12" md="3">
          <v-card hover color="teal-darken-1" title="Libros">
            <div class="display-6">{{ this.book }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" md="3">
          <v-card hover color="light-blue-lighten-2" title="Usuarios">
            <div class="display-6">{{ this.user }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" md="3">
          <v-card hover color="green-darken-1" title="Préstamos activos">
            <div class="display-6">{{ this.activo }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" md="3">
          <v-card hover color="grey-lighten-2" title="Total préstamos">
            <div class="display-6">{{ this.total }}</div>
          </v-card>
        </v-col>
      </v-row>
    <!-- Clima api -->
    <v-row>
  <v-col cols="6" md="6">
    <v-card class="weather-widget" color="blue-grey lighten-4" dark>
      <v-card-title>Clima en {{ city }}</v-card-title>
      <v-card-subtitle v-if="weather">
        <p>Temperatura: {{ weather.current.temp_c }}°C</p>
        <p>Tiempo: {{ weather.current.condition.text }}</p>
        <p>
          Viento: {{ weather.current.wind_kph }} km/h</p>
      </v-card-subtitle>
      <v-card-subtitle v-else>
        <p>Cargando información...</p>
      </v-card-subtitle>
    </v-card>
    <v-card style="height: 400px;">
      <v-card-title style="font-size: medium;">Últimos préstamos
        <v-btn color="primary" class="float-right" @click="$router.push('/prestamoactivo')">Ver más</v-btn>
      </v-card-title>
      <v-data-table
        :headers="headers"
        :items="prestamos"
        :items-per-page="5"
        hide-default-footer
        dense
      >
        <template v-slot:items="props">
          <td>{{ props.item.libro.titulo }}</td>
          <td>{{ props.item.user.nombre }} {{ props.item.user.apellido }}</td>
          <td>{{ props.item.finicio }}</td>
        </template>
      </v-data-table>

    </v-card>
  </v-col>
  <v-col cols="6" md="6">
    
    <v-card >
      <Pie :options="authorChartOptions" :data="authorChartData" />
    </v-card>
    <v-card style="margin-top: 21px; height: 250px;">
      <Bar :options="bookChartOptions" :data="bookChartData" />
    </v-card>
  </v-col>
</v-row>
      
  </div>
</template>

<script>
import axios from 'axios';
import { Bar, Pie, PolarArea } from 'vue-chartjs';
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, ArcElement } from 'chart.js';
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, ArcElement);

const API_URL = 'http://api.weatherapi.com/v1/current.json';
const API_KEY = 'a8c869b326b1458881390134241208'; // api key
const LOCATION = 'Río Grande, Tierra del Fuego'; // lugar para pasar a api

export default {
  name: 'Main',
  components: { Bar, Pie, PolarArea },
  data() {
    return {
      city: LOCATION, 
      weather: null,

      book: null,
      user: null,
      activo: null,
      total: null,
      headers: [
        { title: 'Libro', value: 'libro.titulo', align: 'center' },
        { title: 'Nombre', value: 'user.nombre', align: 'center' },
        { title: 'Apellido', value: 'user.apellido', align: 'center' },
        { title: 'Fecha', value: 'finicio', align: 'center' },
      ],
      prestamos: [],
      nuevoPrestamo: [],
      authorChartData: {
        labels: [],
        datasets: [
          {
            data: [],
            backgroundColor: [],
            borderColor: [],
            borderWidth: 1,
          },
        ],
      },
      // cant de generos
       
      // Autores más leidos
      authorChartData: {
        labels: [],
        datasets: [
          {
            label: 'Top 5 autores más leídos por los usuarios',
            data: [],
            backgroundColor: [],
            borderColor: [],
            borderWidth: 1,
          },
        ],
      },
      authorChartOptions: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Autores más leídos',
          },
        },
        maintainAspectRatio: false,
        legend: {
          position: 'left',
          labels: {
            boxWidth: 10,
 
          },
        },
      },
      bookChartData: {
        labels: [],
        datasets: [
          {
            label: 'Libros prestados por mes',
            data: [],
            backgroundColor: '#2554FF',
            borderColor: '#2554FF',
            borderWidth: 1,
          },
        ],
      },
      bookChartOptions: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Libros prestados por mes',
          },
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Mes',
            },
            ticks: {
              autoSkip: false,
            },
          },
          y: {
            title: {
              display: true,
              text: 'Cantidad de libros',
            },
            ticks: {
              suggestedMin: 0,
              suggestedMax: 20,
              stepSize: 1,
              callback: function (value) {
                return parseInt(value);
              },
            },
          },
        },
        maintainAspectRatio: false,
      },
    };
  },
  mounted() {
    this.fetchWeather();
    //
    axios
      .all([
        axios.get('/stats/bookReg'),
        axios.get('/stats/userReg'),
        axios.get('/stats/activoReg'),
        axios.get('/stats/prestamosTotal'),
        axios.get('/prestamo/list'),
      ])
      .then(([bookResponse, userResponse, activoResponse, totalResponse, prestamosResponse]) => {
        this.book = bookResponse.data;
        this.user = userResponse.data;
        this.activo = activoResponse.data;
        this.total = totalResponse.data;
        if (prestamosResponse.status === 200 && Array.isArray(prestamosResponse.data)) {
          this.prestamos = prestamosResponse.data;
        } else {
          console.error('Error obteniendo los datos de préstamos.');
        }
      })
      .catch((error) => {
        console.error(error);
      });

    // Libros prestados por mes
    axios
      .get('/stats/librosxMes')
      .then((response) => {
        const librosxMes = response.data;
        const months = ['enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio',
         'julio', 'agosto', 'septiembre', 'octubre', 'noviembre', 'diciembre'];
        const data = months.map(month => librosxMes[month] || 0);

        this.bookChartData = {
          labels: months,
          datasets: [
            {
              label: 'Libros prestados por mes',
              data: data,
              backgroundColor: '#2554FF',
              borderColor: '#2554FF',
              borderWidth: 1,
            },
          ],
        };
        //this.bookChartData.labels = months;
        //this.bookChartData.datasets[0].data = data;
      })
      .catch((error) => {
        console.error(error);
      });
 // Autores más leidos
 
  axios.get('/stats/autores').then(response=> {
    const autorLeido = response.data;
      const labels = Object.keys(autorLeido);
      const data = Object.values(autorLeido);

      const colors = [];
      for (let i = 0; i < labels.length; i++) {
        colors.push(`#${Math.floor(Math.random() * 16777215).toString(16)}`);
      }

      this.authorChartData = {
        labels,
        datasets: [
          {
            data,
            backgroundColor: colors,
            borderColor: colors,
            borderWidth: 1,
          },
        ],
      };
  }).catch((error) => {
        console.error(error);
      });
    },
    methods: {
      async fetchWeather() {
      try {
        const response = await axios.get(API_URL, {
          params: {
            key: API_KEY,
            q: LOCATION,
            aqi: 'no',
            lang: 'es'
          }
        });
        this.weather = response.data;
      } catch (error) {
        console.error('Error obteniendo datos:', error);
      }
    },
    }

};
</script>

<style scoped>
.estadisticas{
 
  max-height: 300px;
  overflow: auto;

}
.title {
  font-weight: bold;
  margin-bottom: 10px;
}
.weather-widget {
  margin-bottom: 16px;
}
</style>
