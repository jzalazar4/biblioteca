import axios from 'axios';

const API_URL = 'http://api.weatherapi.com/v1/current.json';
const API_KEY = 'a8c869b326b1458881390134241208'; // Replace with your API key
const LOCATION = 'Argentina, Tierra del Fuego, Río Grande'; // Replace with desired location

export async function fetchWeather() {
  try {
    const response = await axios.get(API_URL, {
      params: {
        key: API_KEY,
        q: LOCATION,
        aqi: 'no'
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error obteniendo datos:', error);
    throw error; // Re-throw error to handle it in the component
  }
}
//export default fetchWeather();
