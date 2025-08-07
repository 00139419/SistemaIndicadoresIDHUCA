// src/authFetch.js
export async function authFetch(path, { headers = {}, ...options } = {}) {
  const token = localStorage.getItem('authToken');

  const API_URL = process.env.REACT_APP_API_URL;

  const response = await fetch(
    `${API_URL}${path}`,
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

  return response.json();
}
