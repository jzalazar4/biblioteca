<template>
 <div id="app">
  <head>  
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
     <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-KK94CHFLLe+nY2dmCWGMq91rCGa5gtU4mk92HdvYe+M/SXH301p5ILy+dN9+nJOZ" crossorigin="anonymous">
    </head>
     <div class="wrapper">
        <aside id="sidebar" :class="{ expand: isSidebarOpen }">
            <div class="d-flex">
                
                <div class="sidebar-logo">
                    <a href="/" style="font-size: x-large;">Biblioteca</a>
                </div>
                <button class="toggle-btn" :class="{ closed: !sidebarOpen }"  type="button" @click="closeSideBar">
                    <box-icon name='menu' color='#ffffff' ></box-icon>
                </button>
            </div>
            <ul class="sidebar-nav">
                <li class="sidebar-item">
                    <a href="/main" class="sidebar-link">
                      <box-icon type='solid' name='dashboard' color='#ffffff'></box-icon>                        
                      <span> Inicio</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="/perfilAdmin" class="sidebar-link">
                      <box-icon name='user-circle'  color='#ffffff'></box-icon>                        
                      <span> Perfil</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="#" class="sidebar-link collapsed has-dropdown" data-bs-toggle="collapse"
                        data-bs-target="#auth" aria-expanded="false" aria-controls="auth">
                        <box-icon name='book'  color='#ffffff'></box-icon>
                        <span> Libros</span>
                    </a>
                    <ul id="auth" class="sidebar-dropdown list-unstyled collapse" data-bs-parent="#sidebar">
                        <li class="sidebar-item">
                            <a href="/verLibros" class="sidebar-link">Ver todo</a>
                        </li>
                        <li class="sidebar-item">
                            <a href="/cargarLibros" class="sidebar-link">Cargar libros</a>
                        </li>
                    </ul>
                </li>
                
                <li class="sidebar-item">
                    <a href="/verUsers" class="sidebar-link">
                      <box-icon name='user'  color='#ffffff'></box-icon>
                        <span> Usuarios</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="#" class="sidebar-link collapsed has-dropdown" data-bs-toggle="collapse"
                        data-bs-target="#auth" aria-expanded="false" aria-controls="auth">
                        <box-icon name='book-reader'  color='#ffffff'></box-icon>
                        <span> Préstamos</span>
                    </a>
                    <ul id="auth" class="sidebar-dropdown list-unstyled collapse" data-bs-parent="#sidebar">
                        <li class="sidebar-item">
                            <a href="/prestamoactivo" class="sidebar-link">Activos</a>
                        </li>
                        <li class="sidebar-item">
                            <a href="/pInactivo" class="sidebar-link">Inactivos</a>
                        </li>
                    </ul>
                </li>
                <li class="sidebar-item">
                    <a href="/estadisticas" class="sidebar-link">
                      <box-icon name='stats'  color='#ffffff'></box-icon>
                        <span> Estadísticas</span>
                    </a>
                </li>
                <li class="sidebar-item">
                    <a href="/tableBooks" class="sidebar-link">
                      <box-icon name='search-alt'  color='#ffffff'></box-icon>
                        <span> Catálogo</span>
                    </a>
                </li>
            </ul>
            <div class="sidebar-footer">
                <a href="#" class="sidebar-link" @click="logout">
                  <box-icon name='exit' color='#ffffff' ></box-icon>
                    <span> Cerrar sesión</span>
                </a>
            </div>
        </aside>
        <div class="main p-3">
            <div class="text-center">
              <main class="main-content">
         
          <router-view></router-view>
        </main>
            </div>
        </div>
    </div>
  

 </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
import store from '@/store/store';
export default {
  name: 'AdminLayout',
data(){
  return {
    isSidebarOpen: true
  }
},  
  methods: {
   closeSideBar(){
    this.isSidebarOpen = !this.isSidebarOpen;
   },
    activateLink(link) {
      this.activeLink = link;
    },
    isActive(link) {
      return this.activeLink === link;
    },
   async logout() {
    store.dispatch('logout');
      this.$router.push('/');
         },
  },
};
</script>

<style scoped>
::after,
::before {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
}

a {
    text-decoration: none;
}

li {
    list-style: none;
}

h1 {
    font-weight: 600;
    font-size: 1.5rem;
}

body {
    font-family: 'Poppins', sans-serif;
}

.wrapper {
    display: flex;
}

.main {
    min-height: 100vh;
    width: calc(100% - 70px);
    margin-left: 70px; 
    overflow: hidden;
    transition: all 0.35s ease-in-out;
    background-color: #fafbfe;

}

#sidebar {
    width: 70px;
    min-width: 70px;
    z-index: 1000;
    transition: all .25s ease-in-out;
    background-color: #283618;
    display: flex;
    flex-direction: column;
    position: fixed; /**antes sticky y no tenia altura */
    height: 100%;
    top: 0;
}

#sidebar.expand {
    width: 260px;
    min-width: 260px;
}
/**nuevo */
#sidebar.expand + .main {
    width: calc(100% - 260px); 
    margin-left: 260px; 
}

.toggle-btn {
  background-color: transparent;
  cursor: pointer;
  border: 0;
  margin-top: 12px;
  padding: .645rem 1.625rem;
  display: flex;
  margin-left: 30px;
}

.toggle-btn.closed {
  margin-left: auto;
}

.toggle-btn i {
  font-size: 1.5rem;
  color: #FFF;
}
.sidebar-logo {
    margin: auto 0;
    margin-left:30px;
}

.sidebar-logo a {
    color: #FFF;
    font-size: 1.15rem;
    font-weight: 600;
}

#sidebar:not(.expand) .sidebar-logo,
#sidebar:not(.expand) a.sidebar-link span {
    display: none;
}

.sidebar-nav {
    padding: 2rem 0;
    flex: 1 1 auto;
}

a.sidebar-link {
    padding: .645rem 1.625rem;
    color: #FFF;
    display: block;
    font-size: 1.0rem;
    white-space: nowrap;
    border-left: 3px solid transparent;
}

.sidebar-link i {
    font-size: 1.5rem;
    margin-right: .75rem;
}

a.sidebar-link:hover {
    background-color: rgba(255, 255, 255, .075);
    border-left: 3px solid #FEFAE0;
}

.sidebar-item {
    position: relative;
    
}

#sidebar:not(.expand) .sidebar-item .sidebar-dropdown {
    position: absolute;
    top: 0;
    left: 70px;
    background-color: #606C38;
    padding: 0;
    min-width: 15rem;
    display: none;
}
/*Lista dentro de links cuando esta la barra abierta */
#sidebar.expand .sidebar-item:hover .has-dropdown+.sidebar-dropdown {
    display: block;
    max-height: 15em;
    width: 100%;
    opacity: 1;
    background-color: #606C38;
}
#sidebar:not(.expand) .sidebar-item:hover .has-dropdown+.sidebar-dropdown {
    display: block;
    max-height: 15em;
    width: 100%;
    opacity: 1;
    background-color: #606C38;
}

#sidebar.expand .sidebar-link[data-bs-toggle="collapse"]::after {
    border: solid;
    border-width: 0 .075rem .075rem 0;
    content: "";
    display: inline-block;
    padding: 2px;
    position: absolute;
    right: 1.5rem;
    top: 1.4rem;
    transform: rotate(-135deg);
    transition: all .2s ease-out;
}

#sidebar.expand .sidebar-link[data-bs-toggle="collapse"].collapsed::after {
    transform: rotate(45deg);
    transition: all .2s ease-out;
}
</style>
