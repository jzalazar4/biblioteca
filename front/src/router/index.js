import { createRouter, createWebHistory } from 'vue-router';
import store from '../store/store'

import Inicio from '../views/home/Inicio.vue'

// AUTH
import Login from '../views/login/Login.vue'
import SignUp from '../views/signup/SignUp.vue'
import EmailConfirm from '../views/sesion/EmailConfirm.vue'
import ForgotPassword from '../views/sesion/ForgotPassword.vue'
import ResetPassword from '../views/sesion/ResetPassword.vue'

// ADMIN
import AdminLayout from '../components/AdminLayout.vue'
import Main from '../views/admin/Main.vue'
import VerUsers from '../views/admin/VerUsers.vue'
import PerfilAdmin from '../views/admin/PerfilAdmin.vue'
import CargarLibros from '../views/libros/CargarLibros.vue'

// USER
import UserProfile from '../views/user/UserProfile.vue'
import TableBooks from '../views/user/TableBooks.vue'
import UserLibros from '../views/user/UserLibros.vue';

// LIBROS
import VerLibros from '../views/admin/VerLibros.vue'

// PRESTAMO
import PrestamoActivo from '../views/prestamo/PrestamoActivo.vue'
import PInactivo from '../views/prestamo/PInactivo.vue';
import Estadisticas from '../views/admin/Estadisticas.vue';


const routes = [

  {
    path:'/',
    name: 'Inicio',
    component: Inicio
  },
  {
    path: '/signin', 
    name: 'Login',
    component: Login
  },
  {
    path: '/signup', 
    name: 'SignUp',
    component: SignUp
  },
  {
    path: '/emailConfirm', 
    name: 'emailConfirm',
    component: EmailConfirm
  },
  {
    path: '/forgotPassword',
    name: 'forgotPassword',
    component: ForgotPassword 
  },
  {
    path: '/reset-password/:token',
    name: 'reset-password',
    component: ResetPassword,
    props: route => ({token: route.params.token }),
  },
  {
    path:'/userProfile',
    name: 'userProfile',
    component: UserProfile,
    meta: { requiresAuth: true }
 
  },

  {
    path: '/tableBooks',
    name: 'tableBooks',
    component: TableBooks
  },
  {
    path: '/userLibros',
    name: 'userLibros',
    component: UserLibros
  },
   {
    path:'/adminLayout',
    name:'adminLayout',
    meta: {requiresAuth: true},
    component: AdminLayout,
    children: [
      {
        path: '/main',
        component: Main,
      },
      {
        path: '/verLibros',
        component: VerLibros,
      },
      {
        path: '/estadisticas',
        name: 'estadisticas',
        component: Estadisticas
      },
      {
        path: '/prestamoActivo',
        name: 'prestamoActivo',
        component: PrestamoActivo,
     
      },
      {
        path: '/pInactivo',
        name: 'pInactivo',
        component: PInactivo,
     
      },
      {
        path: '/verUsers',
        name: 'verUsers',
        component: VerUsers,
      },
     
      {
        path: '/perfilAdmin',
        component: PerfilAdmin
      },
      {
        path: '/cargarLibros',
        component: CargarLibros
      }
    ],
  },
    
   
];

const router = createRouter({
  history: createWebHistory(),
  routes
});
router.beforeEach((to, from, next) => {
  const isAuthenticated = store.getters.isAuthenticated;
  const user =  localStorage.getItem('userLog');
//JSON.parse(localStorage.getItem('userLog'));
  if (to.matched.some(record => record.meta.requiresAuth) && !isAuthenticated) {
    next({ name: 'Login' });
  } else if (to.matched.some(record => record.meta.requiresAdmin) && (!user || user.role !== 'admin')) {
    next({ name: 'Inicio' }); // redirige a main si no es admin
  } else {
    next();
  }
});


export default router;