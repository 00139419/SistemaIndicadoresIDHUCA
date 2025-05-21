import axios from 'axios';

const API_URL = 'http://localhost:8080/idhuca-indicadores/api/srv/catalogo/get';

// Por ahora hardcodeamos el token, pero idealmente lo obtienes de un login o storage
const TOKEN = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIwMDEzOTQxOUB1Y2EuZWR1LnN2IiwiaWF0IjoxNzQ3NzA5MTIyLCJleHAiOjE3NDc3OTU1MjJ9.4jgXo98NuV9B1uTc8FvpUpS51a3cfj47VURRJ30-aXXNqxP8H-Jb1Wp8LqFsgY2NA6tCCdjy6HQhT4oQxGEL-g';

// Versión corregida de fetchCatalog que asegura retornar el array correctamente
export const fetchCatalog = async (catalogKey, parentId = "1") => {
  // Creamos el objeto de datos que será enviado en el cuerpo de la solicitud
  const requestData = {
    derechos: false,
    roles: false,
    departamentos: false,
    municipios: false,
    securityQuestions: false,
    instituciones: false,
    parentId: parentId
  };
  
  // Establecemos como true solamente el catálogo solicitado
  requestData[catalogKey] = true;
  
  try {
    // Realizamos la solicitud POST con axios
    const response = await axios.post(API_URL, requestData, {
      headers: {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
      }
    });
    
    // Directamente devolver la propiedad entity que es un array
    return response.data.entity;
    
  } catch (error) {
    console.error(`Error al obtener catálogo "${catalogKey}":`, error);
    return [];
  }
};