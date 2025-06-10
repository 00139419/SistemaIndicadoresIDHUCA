import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/idhuca-indicadores/api/srv/fichaDerecho';

// Función para obtener todas las fichas de un derecho específico
export const fetchFichasByDerecho = async (derechoCodigo) => {
  const TOKEN = localStorage.getItem('authToken');
  
  if (!TOKEN) {
    console.error('No se encontró token de autenticación');
    throw new Error('No se encontró token de autenticación');
  }
  
  try {
    const response = await axios.post(`${API_BASE_URL}/getAll/post/${derechoCodigo}`, {}, {
      headers: {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (response.data && response.data.codigo === 0) {
      return {
        success: true,
        message: response.data.mensaje || 'Datos obtenidos correctamente',
        data: response.data.entity || []
      };
    } else {
      return {
        success: false,
        message: response.data?.mensaje || 'Error al obtener las fichas',
        data: []
      };
    }
    
  } catch (error) {
    console.error(`Error al obtener fichas del derecho "${derechoCodigo}":`, error);
    throw new Error(error.response?.data?.mensaje || 'Error al obtener las fichas del derecho');
  }
};

// Función para crear una nueva ficha con archivos (multipart)
export const createFicha = async (fichaData, archivos = []) => {
  const TOKEN = localStorage.getItem('authToken');
  
  if (!TOKEN) {
    throw new Error('No se encontró token de autenticación');
  }
  
  try {
    // Crear FormData para envío multipart
    const formData = new FormData();
    
    // Preparar el objeto JSON para la nota
    const notaJson = {
      derecho: {
        codigo: fichaData.derechoCodigo,
        descripcion: fichaData.derechoDescripcion || "Descripción del derecho"
      },
      titulo: fichaData.titulo,
      descripcion: fichaData.descripcion,
      archivos: archivos.map(archivo => ({
        nombreOriginal: archivo.name,
        tipo: archivo.tipo || "documento"
      }))
    };
    
    // Agregar el JSON como parte del multipart
    formData.append('nota', JSON.stringify(notaJson));
    
    // Agregar cada archivo al FormData
    archivos.forEach((archivo, index) => {
      formData.append('archivos', archivo.file);
    });
    
    const response = await axios.post(`${API_BASE_URL}/save`, formData, {
      headers: {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'multipart/form-data'
      }
    });
    
    if (response.data && response.data.codigo === 0) {
      return {
        success: true,
        message: response.data.mensaje || 'Ficha creada correctamente',
        data: response.data.entity
      };
    } else {
      return {
        success: false,
        message: response.data?.mensaje || 'Error al crear la ficha',
        data: null
      };
    }
    
  } catch (error) {
    console.error('Error al crear ficha:', error);
    throw new Error(error.response?.data?.mensaje || 'Error al crear la ficha');
  }
};

// Función para actualizar una ficha existente
export const updateFicha = async (fichaId, fichaData) => {
  const TOKEN = localStorage.getItem('authToken');
  
  if (!TOKEN) {
    throw new Error('No se encontró token de autenticación');
  }
  
  try {
    const response = await axios.put(`${API_BASE_URL}/update/${fichaId}`, fichaData, {
      headers: {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (response.data && response.data.codigo === 0) {
      return {
        success: true,
        message: response.data.mensaje || 'Ficha actualizada correctamente',
        data: response.data.entity
      };
    } else {
      return {
        success: false,
        message: response.data?.mensaje || 'Error al actualizar la ficha',
        data: null
      };
    }
    
  } catch (error) {
    console.error('Error al actualizar ficha:', error);
    throw new Error(error.response?.data?.mensaje || 'Error al actualizar la ficha');
  }
};

// Función para eliminar una ficha usando POST con JSON
export const deleteFicha = async (fichaId) => {
  const TOKEN = localStorage.getItem('authToken');
  
  if (!TOKEN) {
    throw new Error('No se encontró token de autenticación');
  }
  
  try {
    const response = await axios.post(`${API_BASE_URL}/delete/post`, 
      { id: fichaId }, 
      {
        headers: {
          Authorization: `Bearer ${TOKEN}`,
          'Content-Type': 'application/json'
        }
      }
    );
    
    if (response.data && response.data.codigo === 0) {
      return {
        success: true,
        message: response.data.mensaje || 'Ficha eliminada correctamente',
        data: null
      };
    } else {
      return {
        success: false,
        message: response.data?.mensaje || 'Error al eliminar la ficha',
        data: null
      };
    }
    
  } catch (error) {
    console.error('Error al eliminar ficha:', error);
    throw new Error(error.response?.data?.mensaje || 'Error al eliminar la ficha');
  }
};

// Función utilitaria para formatear fechas
export const formatDate = (dateString) => {
  if (!dateString) return '';
  
  try {
    const date = new Date(dateString);
    return date.toLocaleString('es-ES', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  } catch (error) {
    console.error('Error al formatear fecha:', error);
    return dateString;
  }
};

// Función utilitaria para generar código de derecho basado en el ID
export const generateDerechoCodigo = (derechoId) => {
  return `DER_${derechoId}`;
};

// Función utilitaria para obtener descripción del derecho basado en el ID
export const getDerechoDescripcion = (derechoId) => {
  const descripciones = {
    1: "Derecho a la Libertad Personal e Integridad personal",
    2: "Derecho a la Libertad de Expresión",
    3: "Derecho al Acceso a la Justicia",
    4: "Derecho a la Vida"
  };
  return descripciones[derechoId] || "Descripción del derecho";
};