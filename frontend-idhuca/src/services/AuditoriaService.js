import axios from 'axios';

// Función para obtener los registros de auditoría desde la API con paginación
export const fetchAuditoria = async (params = {}) => {
  const API_URL = process.env.REACT_APP_API_URL;
  const API_BACKUP_URL = process.env.REACT_APP_API_BACKUP;

  
  // Obtener el token del almacenamiento local
  const TOKEN = localStorage.getItem('authToken');
  
  if (!TOKEN) {
    console.error('No se encontró token de autenticación');
    throw new Error('No se encontró token de autenticación');
  }

  // Construir el payload con valores por defecto
  const payload = {
    rangoFechas: {
      fechaInicio: params.fechaInicio || null,
      fechaFin: params.fechaFin || null
    },
    paginacion: {
      registrosPorPagina: params.registrosPorPagina || 10,
      paginaActual: params.paginaActual || 1
    }
  };

  // Remover rangoFechas si no se proporcionan fechas
  if (!payload.rangoFechas.fechaInicio && !payload.rangoFechas.fechaFin) {
    delete payload.rangoFechas;
  }
  
  try {
    console.log('Enviando payload:', payload);
    
    // Realizamos la solicitud POST con el payload
    const response = await axios.post(
      API_URL + 'auditoria/get',
      payload, {
      headers: {
        Authorization: `Bearer ${TOKEN}`,
        'Content-Type': 'application/json'
      }
    });
    

    // Devolver tanto los datos como la información de paginación
    return {
      data: response.data.entity || [],
      paginacionInfo: response.data.paginacionInfo || {
        paginaActual: 1,
        totalPaginas: 1,
        totalRegistros: 0,
        registrosPorPagina: 10
      }
    };
    
  } catch (error) {
    console.error('Error al obtener registros de auditoría:', error);
    return { data: [],
      paginacionInfo: {
        paginaActual: 1,
        totalPaginas: 1,
        totalRegistros: 0,
        registrosPorPagina: 10
      }}
  }
};