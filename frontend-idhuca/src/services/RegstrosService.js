import axios from 'axios';

export const getRegistrosByDerecho = async (derecho, filtros) => {
const API_URL = process.env.REACT_APP_API_URL;

  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('No hay token de autenticación');
    }

    const requestPayload = {
      derecho: {
        codigo: derecho.codigo,
        descripcion: derecho.descripcion || ''
      },
      filtros
    };

    const response = await fetch(`${API_URL}/registros/evento/getAllByDerecho`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestPayload)
    });

    const responseData = await response.json();

    return {
      registros: responseData.entity || [],
      paginacion: responseData.paginacionInfo || {
        paginaActual: filtros.paginacion?.paginaActual || 0,
        totalPaginas: 0,
        totalRegistros: 0,
        registrosPorPagina: filtros.paginacion?.registrosPorPagina || 10
      }
    };
  } catch (error) {
    console.error('Error in getRegistrosByDerecho:', error);
    throw error;
  }
};

export const fetchCatalog = async (params) => {
  
  const API_URL = process.env.REACT_APP_API_URL;
  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('No hay token de autenticación');
    }

    const url = `${API_URL}/catalogo/get`;
    const requestBody = {
      ...params
    };


    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestBody)
    });

    const responseText = await response.text();

    if (!response.ok) {
      throw new Error('Error al obtener el catálogo');
    }

    const data = JSON.parse(responseText);

    if (data.codigo === 0) {
      return {
        items: data.entity,
        paginacion: data.paginacionInfo
      };
    } else {
      throw new Error(data.mensaje || 'Error al obtener el catálogo');
    }
  } catch (error) {
    console.error('❌ Error en fetchCatalog:', error);
    throw new Error(error.message || 'Error al obtener el catálogo');
  }
};

export const updateEvento = async (evento) => {
  const API_URL = process.env.REACT_APP_API_URL;
  try {
    const token = localStorage.getItem('authToken');
    if (!token) throw new Error('No hay token de autenticación');

    const response = await fetch(
      API_URL + 'evento/update',
      {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(evento),
      }
    );

    const data = await response.json();
    if (!response.ok || data.codigo !== 0) {
      throw new Error(data.mensaje || 'Error al actualizar el evento');
    }
    return data;
  } catch (error) {
    throw error;
  }
};

export const updatePersonaAfectada = async (persona) => {
  const API_URL = process.env.REACT_APP_API_URL;
  const token = localStorage.getItem('authToken');
  if (!token) throw new Error('No hay token de autenticación');
  const response = await fetch(
    API_URL + 'registros/personasAfectadas/update',
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(persona),
    }
  );
  const data = await response.json();
  if (!response.ok || data.codigo !== 0) {
    throw new Error(data.mensaje || 'Error al actualizar persona afectada');
  }
  return data;
};

export const deletePersonaAfectada = async (eventoId, personaId) => {
  const API_URL = process.env.REACT_APP_API_URL;
 
  const token = localStorage.getItem('authToken');
  if (!token) throw new Error('No hay token de autenticación');
  const response = await fetch(
    API_URL + 'registros/personasAfectadas/delete',
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        id: eventoId,
        personasAfectadas: [{ id: personaId }]
      }),
    }
  );
  const data = await response.json();
  if (!response.ok || data.codigo !== 0) {
    throw new Error(data.mensaje || 'Error al eliminar persona afectada');
  }
  return data;
};

export const deleteEvent = async (eventId) => {
  const API_URL = process.env.REACT_APP_API_URL;
  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('No hay token de autenticación');
    }

    const response = await fetch(`${API_URL}/registros/evento/delete`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ id: eventId })
    });

    if (!response.ok) {
      throw new Error('Error al obtener respuesta del servicio');
    }

    const data = await response.json();

    if (data.codigo === 0) {
      return true;
    } else {
      throw new Error(data.mensaje || 'Error al eliminar registros del evento');
    }

  } catch (error) {
    throw new Error(error.message || 'Error al eliminar registros del evento');
  }
};

export const detailEvent = async (eventId) => {
  const API_URL = process.env.REACT_APP_API_URL;
  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('No hay token de autenticación');
    }

    const response = await fetch(`${API_URL}/registros/evento/getOne`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ id: eventId })
    });

    if (!response.ok) {
      throw new Error('Error al obtener respuesta del servicio');
    }

    const data = await response.json();

    if (data.codigo === 0) {
      return data;
    } else {
      throw new Error(data.mensaje || 'Error al eliminar registros del evento');
    }

  } catch (error) {
    throw new Error(error.message || 'Error al eliminar registros del evento');
  }
};

export const getDerechosCatalog = async () => {
  const API_URL = process.env.REACT_APP_API_URL;
  const params = {
    derechos: true,
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
    subDerechos: false,
    roles: false,
    departamentos: false,
    municipios: false,
    securityQuestions: false,
    parentId: "",
    filtros: {
      paginacion: {
        paginaActual: 0,
        registrosPorPagina: 100
      }
    }
  };

  const response = await fetchCatalog(params);
  return response.items;
};

export const getCatalogo = async (paramsOverrides) => {
  const API_URL = process.env.REACT_APP_API_URL;
  const baseParams = {
    derechos: false,
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
    subDerechos: false,
    roles: false,
    departamentos: false,
    municipios: false,
    securityQuestions: false,
    contexto: false,
    cargarDeafult: false,
    parentId: "",
    filtros: {
      paginacion: {
        paginaActual: 0,
        registrosPorPagina: 1000
      }
    }
  };

  const params = { ...baseParams, ...paramsOverrides };
  const response = await fetchCatalog(params);
  return response.items;
};


export const renderCheck = (value) => (
  value ?
    <span className="d-flex justify-content-center align-items-center" style={{ color: 'green' }}>✔️</span>
    :
    <span className="d-flex justify-content-center align-items-center" style={{ color: 'red' }}>❌</span>
);