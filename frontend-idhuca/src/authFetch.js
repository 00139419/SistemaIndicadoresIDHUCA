// src/authFetch.js
export async function authFetch(path, { headers = {}, ...options } = {}) {
  const token = localStorage.getItem('authToken');

  console.log("Entra al nuevo modulo");

  const response = await fetch(
    `${'http://localhost:8080/idhuca-indicadores/api/srv/'}${path}`,
    {
      
      headers: {
        'Content-Type': 'application/json',
        ...(token && { Authorization: `Bearer ${token}` }),
        ...headers,
      },
      ...options,
    }
  );

  if (response.status === 401) {
    // Por ejemplo: redirigir a login, refrescar token, etc.
    // window.location = '/login';
  }

  if (!response.ok) {
    const text = await response.text();
    throw new Error(`${response.status} – ${text}`);
  }

  console.log(response);

  return response.json();
}
