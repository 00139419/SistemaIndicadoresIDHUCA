import axios from 'axios';

const API_URL = 'http://localhost:8080/idhuca-indicadores/api/srv';

const getRegistros = async (filtros) => {
  try {
    const response = await axios.post(
      `${API_URL}/registros/evento/getAllByDerecho`,
      filtros,
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      }
    );
    return response.data;
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Error al obtener los registros');
  }
};

export { getRegistros };