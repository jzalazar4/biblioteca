import axios from 'axios';

axios.defaults.baseURL = 'http://localhost:8082/api/v1';

axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    const isPublicRequest = config.url.includes('/public');
    if (token && !isPublicRequest) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

export default axios;