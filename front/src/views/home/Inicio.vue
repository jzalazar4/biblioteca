<template>
  <div style="  background-color: white">
    <NavBar />
    <!---<v-carousel :show-arrows="false"  height="450" hide-delimiter-background class="carousel-main">
      <div v-for="(image, index) in images" :key="index" 
      :class="['mySlides', { active: index === currentSlide }]" 
      :style="{ backgroundImage: `url(${image})` }"></div>
    </v-carousel>
   -->
   <v-carousel :show-arrows="false" height="450" hide-delimiter-background class="carousel-main">
  <div v-for="(image, index) in images" :key="index" 
       :class="['mySlides', { active: index === currentSlide }]" 
       :style="{ backgroundImage: `url(${image})` }">
    <div class="carousel-content">
      
      <router-link to="/tableBooks" v-if="index === 1">
        <v-btn v-if="index === 1" class="carousel-button"  style="position: absolute; bottom: 20px; left: 50%; transform: translateX(-50%);" >Ver catálogo</v-btn>
      </router-link>
    </div>
  </div>
</v-carousel>
     <h4 class="font-weight-light" >
        <b> MEJORES RANKEADOS EN GOODREADS</b>
      </h4>
      <hr>
      <v-carousel  height="400" cycle  hide-delimiter-background interval="4000">
        <template v-slot:prev="{ props }">
      <v-btn
        color="white"
        variant="elevated"
        rounded
        @click="props.onClick"
      >
      <box-icon name='left-arrow-alt' ></box-icon>
    </v-btn>
    </template>
    <template v-slot:next="{ props }">
      <v-btn
        color="white"
        variant="elevated"
        rounded
        @click="props.onClick"
      >
      <box-icon name='right-arrow-alt' ></box-icon>
    </v-btn>
    </template>
        <v-carousel-item v-for="(chunk, index) in chunkedBooks" :key="index">
        <div class="d-flex justify-center">
          <v-card v-for="book in chunk" :key="book.title" class="v-card-class">
            <v-img height="200px" contain :src="book.image" class="imagen-carousel"></v-img>
            <v-card-title class="text-center">{{ book.title }}</v-card-title>
            <v-card-subtitle class="text-center">{{ book.author }}</v-card-subtitle>
            <v-card-actions class="justify-center">
              <star-rating
               v-model:rating="book.rating"
               v-bind:increment="0.1"
               v-bind:max-rating="5"
                
               v-bind:star-size="25" 
              read-only></star-rating>
            
              </v-card-actions>
            </v-card>
          </div>
        </v-carousel-item>
      </v-carousel>
    


    <h4 class="font-weight-light" >
      <b> LOS ULTIMOS 10 GANADORES DEL PULITZER EN FICCION</b>
    </h4>
    <hr>
    <v-carousel  height="400" width="300" cycle  hide-delimiter-background interval="4200" >
        <template v-slot:prev="{ props }">
      <v-btn
        color="white"
        variant="elevated"
        rounded
        @click="props.onClick"
      >
      <box-icon name='left-arrow-alt' ></box-icon>
    </v-btn>
    </template>
    <template v-slot:next="{ props }">
      <v-btn
        color="white"
        variant="elevated"
        rounded
        @click="props.onClick"
      >
      <box-icon name='right-arrow-alt' ></box-icon>
    </v-btn>
    </template>
    <v-carousel-item v-for="(chunk, index) in chunkedPulitzer" :key="index">
        <div class="d-flex justify-center">
          <v-card v-for="libro in chunk" :key="libro.title" class="v-card-class">
            <v-img height="200px" contain :src="libro.image" class="imagen-carousel"></v-img>
            <v-card-title class="text-center">{{ libro.title }}</v-card-title>
            <v-card-subtitle class="text-center">{{ libro.author }}</v-card-subtitle>
            <v-card-actions class="justify-center">
              {{ libro.year }}
              </v-card-actions>
            </v-card>
          </div>
        </v-carousel-item>
      </v-carousel>

</div>
<footer style="background-color:rgba(157, 216, 150, 0.308);" >
    <v-row justify="center" no-gutters>
      <v-btn
        v-for="(link, index) in links"
        :key="link"
        class="mx-2"
        color="black"
        rounded="0"
        :to="link.route"
        variant="plain"
      >  {{ link.text }}

      </v-btn>
      <v-col class="text-center mt-4" cols="12">
        {{ new Date().getFullYear() }} — <strong>Biblioteca</strong>
      </v-col>
    </v-row>
  </footer>
</template>

<script>
import NavBar from '@/components/NavBar.vue';
import StarRating from 'vue-star-rating'

export default {
  name: "Home",
  props: ['user'],
  components: {
    NavBar,
    StarRating
  },
  data() {
    return {
      currentSlide: 0,
      interval: null,
      slide: 0,
      images: [
        require('@/assets/inicio/main.png'),
        require('@/assets/inicio/foto2.png'),
        //require('@/assets/inicio/2.png'),
        //require('@/assets/inicio/3.png'),
      ],
      links: [ 
        { text: 'Inicio', route: { name: 'Inicio' } },
        { text: 'Catálogo', route: { name: 'tableBooks' } },

      ],
      books: [
        { title: "The Help", author: "Kathryn Stockett", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1622355533i/4667024.jpg", rating: 4.47 },
        { title: "Harry Potter 4", author: "J.K. Rowling", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1663805647i/136251.jpg", rating: 4.62 },
        { title: "The Book Thief", author: "Markus Zusak", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1522157426i/19063.jpg", rating: 4.36 },
        { title: "Pride and Prejudice", author: "Jane Austen", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1320399351i/1885.jpg", rating: 4.26 },
        { title: "To Kill a Mockingbird", author: "Harper Lee", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1553383690i/2657.jpg", rating: 4.29 },
        { title: "The Hobbit", author: "J.R.R. Tolkien", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1546071216i/5907.jpg", rating: 4.27 },
        { title: "1984", author: "George Orwell", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1657781256i/61439040.jpg", rating: 4.17 },
        { title: "The Kite Runner", author: "Khaled Hosseini", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1579036753i/77203.jpg", rating: 4.28 },
        { title: "Little Women", author: "Louisa May Alcott", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1562690475i/1934.jpg", rating: 4.07 },
        { title: "Memoirs of a Geisha", author: "Arthur Golden", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1409595968i/929.jpg", rating: 4.12 },
       
      ],
      pulitzers: [
        { title: "Night Watch", author: "Jayne Anne Phillips", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1684815072i/62951865.jpg", year: 2024 },
        { title: "Trust", author: "Hernan Diaz", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1631246461i/58210933.jpg", year: 2023 },
        { title: "The Netanyahus", author: "Joshua Cohen", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1661302430i/55817233.jpg", year: 2022 },
        { title: "The Night Watchman", author: "Louise Erdrich", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1560803752i/43721059.jpg", year: 2021 },
        { title: "The Nickel Boys", author: "Colson Whitehead", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1593115396i/42270835.jpg", year: 2020 },
        { title: "The Overstory", author: "Richard Powers", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1562786502i/40180098.jpg", year: 2019 },
        { title: "Less", author: "Andrew Sean Greer", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1524491811i/39927096.jpg", year: 2018 },
        { title: "The Underground Railroad", author: "Colson Whitehead", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1493178362i/30555488.jpg", year: 2017 },
        { title: "The Sympathizer", author: "Viet Thanh Nguyen", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1718982183i/23168277.jpg", year: 2016 },
        { title: "All the Light We Cannot See", author: "Anthony Doerr ", image: "https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1451445646i/18143977.jpg", year: 2015 },

      ],
    };
  },
  computed: {
    chunkedBooks() {
      const chunkSize = 5; // libros a mostrar
      return this.books.reduce((chunks, book, index) => {
        const chunkIndex = Math.floor(index / chunkSize);
        if (!chunks[chunkIndex]) {
          chunks[chunkIndex] = [];
        }
        chunks[chunkIndex].push(book);
        return chunks;
      }, []);
    },
    chunkedPulitzer(){
      const chunkSize = 5; // libros a mostrar
      return this.pulitzers.reduce((chunks, libro, index) => {
        const chunkIndex = Math.floor(index / chunkSize);
        if (!chunks[chunkIndex]) {
          chunks[chunkIndex] = [];
        }
        chunks[chunkIndex].push(libro);
        return chunks;
      }, []);
    }
  },
  mounted() {
    this.startCarousel();
  },
  beforeDestroy() {
    clearInterval(this.interval);
  },
  methods: {
    startCarousel() {
      this.interval = setInterval(this.nextSlide, 4000); // 4 seg
    },
    nextSlide() {
      this.currentSlide = (this.currentSlide + 1) % this.images.length;
    },
    getStarClass(index, rating) {
      if (index <= rating) {
        return 'filled';
      } else if (index - 0.5 <= rating) {
        return 'half-filled';
      } else {
        return '';
      }
    },
  
  },
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Material+Icons');

body, html {
  margin: 0;
  padding: 0;
  height: auto;

}
.mySlides {
  height: fit-content;
}

.carousel {
  margin-top: 10px;
  margin-left: 30px;
}

.v-slide-group {
  margin: 20px;
  margin-left: 30px;
}

.v-card {
  margin: 10px;
}

.justify-center {
  justify-content: center;
}


.text-center {
font-size: 13px !important;
}

.v-card-actions {
  display: flex;
  opacity: 1;
  background-color: transparent;
  justify-content: space-between;
}


.v-card-class{
  max-width: 300px;
  background-color: rgba(157, 216, 150, 0.308);
  margin: 10.5px; 
  margin-bottom: 30px;
  flex-grow: 0.1;
}

.carousel2 {
  position: relative;
  width: 100%;
  height: 70vh;
  overflow: hidden;
}
.font-weight-light {
  font-size: 20px;
  font-weight: 350 !important;
  margin-left: 20px;
}

.mySlides {
  display: none;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  max-height: 400px;
  background-size: cover;
  background-position: center;
  transition: opacity 0.5s ease-in-out;
}
.imagen-carousel {
  margin-top: 10px;
}
.mySlides.active {
  display: block;
}
.carousel-button{
  justify-content: center;
  height:200px;
}
</style>
