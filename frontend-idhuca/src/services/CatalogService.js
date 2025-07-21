import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/idhuca-indicadores/api/srv/catalogo';
const API_GET_URL = `${API_BASE_URL}/get`;
const API_ADD_URL = `${API_BASE_URL}/add`;
const API_UPDATE_URL = `${API_BASE_URL}/update`;
const API_DELETE_URL = `${API_BASE_URL}/delete`;

// En CatalogService.js, reemplaza la función fetchCatalog con esta versión corregida:

export const fetchCatalog = async (catalogKey, parentId = "1", paginaActual = 0, registrosPorPagina = 20) => {
  // Obtener el token del almacenamiento local
  const TOKEN = localStorage.getItem('authToken');
  
  if (!TOKEN) {
    console.error('No se encontró token de autenticación');
    return { entity: [], paginacionInfo: {} };
  }
  
  // Creamos un objeto base con todos los catálogos establecidos en false
  const requestData = {
    tipoProcesoJudicial: false,
    tipoDenunciante: false,
    duracionProceso: false,
    tipoRepresion: false,
    medioExpresion: false,
    motivoDetencion: false,
    tipoArma: false,
    tipoDetencion: false,
    tipoViolencia: false,
    estadoSalud: false,
    tipoPersona: false,
    genero: false,
    lugarExacto: false,
    estadoRegistro: false,
    fuentes: false,
    paises: false,
    derechos: false,
    roles: false,
    departamentos: false,
    municipios: false,
    securityQuestions: false,
    contexto: false,
    parentId: parentId,
    filtros: {
      paginacion: {
        paginaActual: paginaActual,
        registrosPorPagina: registrosPorPagina
      }
    }
  };
  
  // Si el catalogKey es válido, lo activamos en el objeto de solicitud
  if (catalogKey in requestData) {
    requestData[catalogKey] = true;
  } else if (catalogKey !== 'catalogos') {
    console.warn(`Catálogo "${catalogKey}" no reconocido`);
  }
  
  try {
    // Realizamos la solicitud POST con axios
    const response = await axios.post(API_GET_URL, requestData, {
      headers: {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
      }
    });
    
    console.log('Respuesta completa del servidor:', response.data);
    
    // CORRECCIÓN: Devolver la estructura completa que incluye entity y paginacionInfo
    if (response.data && response.data.codigo === 0) {
      return {
        entity: response.data.entity || [],
        paginacionInfo: response.data.paginacionInfo || {
          paginaActual: 1,
          totalPaginas: 1,
          totalRegistros: 0,
          registrosPorPagina: registrosPorPagina
        }
      };
    } else {
      console.warn('Respuesta del servidor no exitosa:', response.data);
      return { entity: [], paginacionInfo: {} };
    }
    
  } catch (error) {
    console.error(`Error al obtener catálogo "${catalogKey}":`, error);
    return { entity: [], paginacionInfo: {} };
  }
};
// Función para agregar un nuevo registro al catálogo
export const addCatalogItem = async (catalogKey, newItemDescription, parentId = "1") => {
  // Obtener el token del almacenamiento local
  const TOKEN = localStorage.getItem('authToken');
  
  if (!TOKEN) {
    throw new Error('No se encontró token de autenticación');
  }
  
  // Creamos un objeto base con todos los catálogos establecidos en false
  const requestData = {
    tipoProcesoJudicial: false,
    tipoDenunciante: false,
    duracionProceso: false,
    tipoRepresion: false,
    medioExpresion: false,
    motivoDetencion: false,
    tipoArma: false,
    tipoDetencion: false,
    tipoViolencia: false,
    estadoSalud: false,
    tipoPersona: false,
    genero: false,
    lugarExacto: false,
    estadoRegistro: false,
    fuentes: false,
    paises: false,
    derechos: false,
    roles: false,
    departamentos: false,
    municipios: false,
    securityQuestions: false,
    instituciones: false,
    parentId: parentId,
    contexto:false,
    nuevoCatalogo: newItemDescription
  };
  
  // Activamos el catálogo correspondiente
  if (catalogKey in requestData) {
    requestData[catalogKey] = true;
  } else {
    throw new Error(`Catálogo "${catalogKey}" no reconocido`);
  }

  console.log(JSON.stringify(requestData));
  
  try {
    // Realizamos la solicitud POST con axios
    const response = await axios.post(API_ADD_URL, requestData, {
      headers: {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
      }
    });
    
    // Verificar si la respuesta indica éxito (código 0 significa éxito)
    if (response.data && response.data.codigo === 0) {
      return {
        success: true,
        message: response.data.mensaje || 'Registro agregado correctamente',
        data: response.data
      };
    } else {
      return {
        success: false,
        message: response.data?.mensaje || 'Error al agregar el registro',
        data: response.data
      };
    }
    
  } catch (error) {
    console.error(`Error al agregar elemento al catálogo "${catalogKey}":`, error);
    throw new Error(error.response?.data?.mensaje || 'Error al agregar el registro');
  }
};

// Función para actualizar un registro del catálogo
export const updateCatalogItem = async (codigo, descripcion) => {
  // Obtener el token del almacenamiento local
  const TOKEN = localStorage.getItem('authToken');
  
  if (!TOKEN) {
    throw new Error('No se encontró token de autenticación');
  }
  
  const requestData = {
    catalogo: {
      codigo: codigo,
      descripcion: descripcion
    }
  };
  
  try {
    // Realizamos la solicitud POST con axios
    const response = await axios.post(API_UPDATE_URL, requestData, {
      headers: {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
      }
    });
    
    // Verificar si la respuesta indica éxito (código 0 significa éxito)
    if (response.data && response.data.codigo === 0) {
      return {
        success: true,
        message: response.data.mensaje || 'Registro actualizado correctamente',
        data: response.data
      };
    } else {
      return {
        success: false,
        message: response.data?.mensaje || 'Error al actualizar el registro',
        data: response.data
      };
    }
    
  } catch (error) {
    console.error('Error al actualizar elemento del catálogo:', error);
    throw new Error(error.response?.data?.mensaje || 'Error al actualizar el registro');
  }
};

// Función para eliminar un registro del catálogo
export const deleteCatalogItem = async (codigo) => {
  // Obtener el token del almacenamiento local
  const TOKEN = localStorage.getItem('authToken');
  
  if (!TOKEN) {
    throw new Error('No se encontró token de autenticación');
  }
  
  const requestData = {
    catalogo: {
      codigo: codigo
    }
  };
  
  try {
    // Realizamos la solicitud POST con axios
    const response = await axios.post(API_DELETE_URL, requestData, {
      headers: {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
      }
    });
    
    // Verificar si la respuesta indica éxito (código 0 significa éxito)
    if (response.data && response.data.codigo === 0) {
      return {
        success: true,
        message: response.data.mensaje || 'Registro eliminado correctamente',
        data: response.data
      };
    } else {
      return {
        success: false,
        message: response.data?.mensaje || 'Error al eliminar el registro',
        data: response.data
      };
    }
    
  } catch (error) {
    console.error('Error al eliminar elemento del catálogo:', error);
    throw new Error(error.response?.data?.mensaje || 'Error al eliminar el registro');
  }
};