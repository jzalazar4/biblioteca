<template>
  <div>
    <!-- Estadísticas -->
    <v-row>
      <v-col cols="12" md="6">
        <v-card style="height: 500px; max-width: 500px; overflow: auto;">
          <Pie :options="authorChartOptions" :data="authorChartData" />
        </v-card>
        <br>
        <v-card style="height: 500px; max-width: 500px; overflow: auto;">
          <Bar :options="bookChartOptions" :data="bookChartData" />
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card style="height: 500px; max-width: 500px; overflow: auto;">
          <Bar :options="genreChartOptions" :data="genreChartData" />
        </v-card>
       
        <br>
        <v-card style="height: 500px; max-width: 500px; overflow: auto;">
          <PolarArea :options="genOptions" :data="genChartData" />
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import axios from 'axios';
import { Bar, Pie, PolarArea } from 'vue-chartjs';
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, ArcElement, RadialLinearScale } from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, ArcElement, RadialLinearScale);

export default {
  components: { Bar, Pie, PolarArea },
  data() {
    return {
      // cantidad de libros por genero
      genreChartData: {
        labels: [],
        datasets: [
          {
            label: 'Cantidad de Libros por Género',
            data: [],
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            borderWidth: 1,
          },
        ],
      },
      genreChartOptions: {
        responsive: true,
        indexAxis: 'y', //convertir el gráfico de barras en horizontal
        plugins: {
          title: {
            display: true,
            text: 'Cantidad de Libros por Género',
          },
        },
        scales: {
          x: {
            title: {
              display: true,
              text: 'Cantidad de Libros',
            },
            beginAtZero: true,
            suggestedMax: 15,
          },
          y: {
            title: {
              display: true,
              text: 'Género',
            },
          },
        },
        maintainAspectRatio: false,
      },
      // Generos más leidos
      genChartData: {
        labels: [],
        datasets: [
          {
            label: 'Géneros más leídos',
            data: [],
            backgroundColor: [],
            borderColor: [],
            borderWidth: 1,
          }
        ]
      },
      genOptions: {
        responsive: true,
        plugins: {
          title: {
            display: true,
            text: 'Géneros más leídos',
          }
        },
        maintainAspectRatio: false,
        scales: {
          r: {
            beginAtZero: true
          }
        }
      },
      // Autores más leidos
      authorChartData: {
        labels: [],
        datasets: [
          {
            label: 'Autores más leídos',
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
          position: 'right',
          labels: {
            boxWidth: 10,
          },
        },
      },
      // Libros por mes
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
          
              beginAtZero: true,
              suggestedMax: 10,
              
           
          },
        },
        maintainAspectRatio: false,
      },
    };
  },
  async mounted() {
   // cantidad de libros por genero
   try {
      const response = await axios.get('/stats/bookgenrecant');
      const generos = response.data;
      const labels = Object.keys(generos);
      const data = Object.values(generos);
      const backgroundColor = [];
      const borderColor = [];

      for (let i = 0; i < labels.length; i++) {
        backgroundColor.push(`#${Math.floor(Math.random() * 16777215).toString(16)}`);
        borderColor.push(`#${Math.floor(Math.random() * 16777215).toString(16)}`);
      }

      this.genreChartData = {
        labels,
        datasets: [
          {
            data,
            backgroundColor,
            borderColor,
            borderWidth: 1,
          }
        ]
      };
    } catch (error) {
      console.error(error);
    }
    // Generos más leidos
    try {
      const response = await axios.get('/stats/genero');
      const generos = response.data;
      const labels = Object.keys(generos);
      const data = Object.values(generos);
      const backgroundColor = [];
      const borderColor = [];

      for (let i = 0; i < labels.length; i++) {
        backgroundColor.push(`#${Math.floor(Math.random() * 16777215).toString(16)}`);
        borderColor.push(`#${Math.floor(Math.random() * 16777215).toString(16)}`);
      }

      this.genChartData = {
        labels,
        datasets: [
          {
            data,
            backgroundColor,
            borderColor,
            borderWidth: 1,
          }
        ]
      };
    } catch (error) {
      console.log(error);
    }
    
    // Autores más leidos
    try {
      const response = await axios.get('/stats/autores');
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
    } catch (error) {
      console.error(error);
    }

    await this.updateChartData();
    const intervalId = setInterval(this.updateChartData, 100000); // actualzia cada 3 seg

    // Store the intervalId to clear it later
    this.intervalId = intervalId;
  },
  methods: {
    async updateChartData() {
      try {
        const response = await axios.get('/stats/librosxMes');
        const librosxMes = response.data;
        const months = ['enero', 'febrero', 'marzo', 'abril', 'mayo', 
        'junio', 'julio', 'agosto', 'septiembre', 'octubre', 'noviembre', 'diciembre'];
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
      } catch (error) {
        console.error(error);
      }
    },
  },
  unmounted() {
    clearInterval(this.intervalId); 
  },
};
</script>
