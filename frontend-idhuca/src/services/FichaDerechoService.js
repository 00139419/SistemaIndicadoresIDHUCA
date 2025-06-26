import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/idhuca-indicadores/api/srv/fichaDerecho';

// Función para obtener todas las fichas de un derecho específico con paginación y filtros
export const fetchFichasByDerecho = async (derechoCodigo, options = {}) => {
  const TOKEN = localStorage.getItem('authToken');

  if (!TOKEN) {
    console.error('No se encontró token de autenticación');
    throw new Error('No se encontró token de autenticación');
  }

  try {
    // Construir el objeto de petición
    const requestBody = {
      codigoDerecho: derechoCodigo
    };

    // Agregar filtros si se proporcionan
    if (options.filtros) {
      requestBody.filtros = {};

      // Filtros de rango de fechas
      if (options.filtros.fechaInicio || options.filtros.fechaFin) {
        requestBody.filtros.rangoFechas = {};
        if (options.filtros.fechaInicio) {
          requestBody.filtros.rangoFechas.fechaInicio = options.filtros.fechaInicio;
        }
        if (options.filtros.fechaFin) {
          requestBody.filtros.rangoFechas.fechaFin = options.filtros.fechaFin;
        }
      }

      // Filtros de paginación
      if (options.filtros.paginacion) {
        requestBody.filtros.paginacion = {
          registrosPorPagina: options.filtros.paginacion.registrosPorPagina || 10,
          paginaActual: options.filtros.paginacion.paginaActual || 0
        };
      }
    }

    console.log('Request body:', JSON.stringify(requestBody, null, 2));

    const response = await axios.post(`${API_BASE_URL}/getAll/post`, requestBody, {
      headers: {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
      }
    });

    console.log('JSON response:', response.data);

    if (response.data && response.data.codigo === 0) {
      return {
        success: true,
        message: response.data.mensaje || 'Datos obtenidos correctamente',
        data: response.data.entity || [],
        paginacionInfo: response.data.paginacionInfo || {
          paginaActual: 0,
          totalPaginas: response.data.entity?.length > 0 ? 1 : 0,
          totalRegistros: response.data.entity?.length || 0,
          registrosPorPagina: 10
        }
      };
    } else {
      return {
        success: false,
        message: response.data?.mensaje || 'Error al obtener las fichas',
        data: [],
        paginacionInfo: null
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
    // Estructura del JSON - NECESITO QUE ME PROPORCIONES EL FORMATO EXACTO
    const requestBody = {
      id: fichaId,
      titulo: fichaData.titulo,
      descripcion: fichaData.descripcion,
      // Agregar otros campos según el formato que me proporciones
    };

    const response = await axios.post(`${API_BASE_URL}/update/post/note`, requestBody, {
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

// Función utilitaria para formatear fecha solo (sin hora) para filtros
export const formatDateOnly = (dateString) => {
  if (!dateString) return '';

  try {
    const date = new Date(dateString);
    return date.toISOString().split('T')[0]; // YYYY-MM-DD
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

// Función para descargar archivo
export const downloadFile = async (archivoUrl, nombreOriginal) => {
  const TOKEN = localStorage.getItem('authToken');

  if (!TOKEN) {
    throw new Error('No se encontró token de autenticación');
  }

  try {
    const response = await axios.post(`${API_BASE_URL}/get/file/${archivoUrl}`, null, {
      headers: {
        Authorization: `Bearer ${TOKEN}`
      },
      responseType: 'blob' // Importante para manejar archivos binarios
    });

    // Crear un blob URL y descargar el archivo
    const blob = new Blob([response.data]);
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = nombreOriginal || archivoUrl; // Usar el nombre original o el URL como fallback
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    return {
      success: true,
      message: 'Archivo descargado correctamente'
    };

  } catch (error) {
    console.error('Error al descargar archivo:', error);
    throw new Error(error.response?.data?.mensaje || 'Error al descargar el archivo');
  }
};