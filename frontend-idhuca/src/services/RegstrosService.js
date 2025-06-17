import axios from 'axios';

const API_URL = 'http://localhost:8080/idhuca-indicadores/api/srv';

export const getRegistrosByDerecho = async (derecho, pagina = 0, registrosPorPagina = 10) => {
  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('No hay token de autenticación');
    }

    console.log('Token:', token); // Debug token

    const requestPayload = {
      derecho: {
        codigo: derecho.codigo,
        descripcion: derecho.descripcion || ''
      },
      filtros: {
        paginacion: {
          paginaActual: pagina,
          registrosPorPagina
        }
      }
    };

    console.log('Request URL:', `${API_URL}/registros/evento/getAllByDerecho`);
    console.log('Request payload:', JSON.stringify(requestPayload, null, 2));

    const response = await fetch(`${API_URL}/registros/evento/getAllByDerecho`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestPayload)
    });

    console.log('Response status:', response.status);
    const responseData = await response.json();
    console.log('Response data:', responseData);

    if (!response.ok) {
      throw new Error(responseData.mensaje || `Error ${response.status}`);
    }

    return {
      registros: responseData.entity || [],
      paginacion: responseData.paginacionInfo || {
        paginaActual: pagina,
        totalPaginas: 0,
        totalRegistros: 0,
        registrosPorPagina
      }
    };
  } catch (error) {
    console.error('Error in getRegistrosByDerecho:', error);
    throw error;
  }
};



export const fetchCatalog = async (params) => {
  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('No hay token de autenticación');
    }

    const response = await fetch(`${API_URL}/catalogo/get`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        ...params,
        filtros: {
          paginacion: {
            paginaActual: 0,
            registrosPorPagina: 10
          }
        }
      })
    });

    if (!response.ok) {
      throw new Error('Error al obtener el catálogo');
    }

    const data = await response.json();
    
    if (data.codigo === 0) {
      return {
        items: data.entity,
        paginacion: data.paginacionInfo
      };
    } else {
      throw new Error(data.mensaje || 'Error al obtener el catálogo');
    }
  } catch (error) {
    throw new Error(error.message || 'Error al obtener el catálogo');
  }
};