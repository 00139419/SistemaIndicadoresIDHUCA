import React, { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

// Crear el contexto de autenticación
const AuthContext = createContext();

// Hook personalizado para usar el contexto en cualquier componente
export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  // Verificar si hay un token guardado al cargar la aplicación
  useEffect(() => {
    const checkAuth = () => {
      const token = localStorage.getItem('authToken');
      if (token) {
        // Token existe, cargar información del usuario si está disponible
        const userStr = localStorage.getItem('user');
        if (userStr) {
          try {
            setUser(JSON.parse(userStr));
          } catch (e) {
            console.error('Error al parsear la información del usuario:', e);
          }
        }
        setIsAuthenticated(true);
      }
      setLoading(false);
    };

    checkAuth();
  }, []);

  // Función de login
  const login = async (email, password) => {
  try {
    const response = await fetch('http://localhost:8080/idhuca-indicadores/api/srv/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password }),
    });
    
    const data = await response.json();
    console.log('API Response:', data);
    
    if (!response.ok) {
      throw new Error(data.message || 'Error al iniciar sesión');
    }
    
    // Extract token from the correct location in the response
    const token = data.entity?.jwt;
    
    if (!token) {
      throw new Error('No se recibió token de autenticación del servidor');
    }

    // Store token
    localStorage.setItem('authToken', token);
    setIsAuthenticated(true);
    
    return { 
      success: true,
      token: token
    };
    
  } catch (err) {
    console.error('Error de login:', err);
    return { 
      success: false, 
      error: err.message 
    };
  }
};

  // Función de logout
  const logout = () => {
    // Eliminar token y datos del usuario
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    
    // Actualizar estado
    setIsAuthenticated(false);
    setUser(null);
    
    // Redirigir al login
    navigate('/login');
    
    // Opcionalmente, hacer una llamada a la API para invalidar el token en el backend
    // fetch('http://localhost:8080/idhuca-indicadores/api/srv/auth/logout', {
    //   method: 'POST',
    //   headers: {
    //     'Authorization': `Bearer ${token}`,
    //     'Content-Type': 'application/json',
    //   },
    // }).catch(err => {
    //   console.error('Error al cerrar sesión en el servidor:', err);
    // });
  };

  // Función para verificar si el token sigue siendo válido
  const checkTokenValidity = async () => {
    const token = localStorage.getItem('authToken');
    if (!token) {
      setIsAuthenticated(false);
      return false;
    }

    // Endpoint para verificar el token
    //
    // try {
    //   const response = await fetch('http://localhost:8080/idhuca-indicadores/api/srv/auth/verify', {
    //     headers: {
    //       'Authorization': `Bearer ${token}`,
    //     },
    //   });
    //   
    //   if (!response.ok) {
    //     throw new Error('Token inválido');
    //   }
    //   
    //   return true;
    // } catch (err) {
    //   console.error('Error al verificar token:', err);
    //   logout();
    //   return false;
    // }

    // Por ahora, simplemente asumimos que el token es válido si existe
    return true;
  };

  // Proporcionar los valores y funciones a los componentes hijos
  const value = {
    isAuthenticated,
    user,
    loading,
    login,
    logout,
    checkTokenValidity
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;