import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/idhuca-indicadores/api/srv/auditoria';
const API_GET_URL = `${API_BASE_URL}/get`;

// Función para obtener los registros de auditoría desde la API
export const fetchAuditoria = async () => {
  // Obtener el token del almacenamiento local
  const TOKEN = localStorage.getItem('authToken');
  
  if (!TOKEN) {
    console.error('No se encontró token de autenticación');
    throw new Error('No se encontró token de autenticación');
  }
  
  try {
    // Realizamos la solicitud POST sin datos en el body (cuerpo vacío)
    const response = await axios.post(API_GET_URL, {}, {
      headers: {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
      }
    });
    
    // Devolver la propiedad entity que contiene los datos de auditoría
    return response.data.entity || [];
    
  } catch (error) {
    console.error('Error al obtener registros de auditoría:', error);
    throw new Error(error.response?.data?.mensaje || 'Error al obtener los registros de auditoría');
  }
};