import React, { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [userRole, setUserRole] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  const getUserRole = async () => {
  try {
    const token = localStorage.getItem('authToken');
    if (!token) {
      throw new Error('No token found');
    }

    const response = await axios.post(
      'http://localhost:8080/idhuca-indicadores/api/srv/users/get/current',
      {},
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      }
    );

    if (response.data && response.data.entity && response.data.entity.rol) {
      return response.data.entity.rol.codigo; 
    }
    return null;
  } catch (error) {
    console.error('Error fetching user role:', error);
    return null;
  }
};

  const login = async (email, password) => {
    try {
      const response = await axios.post('http://localhost:8080/idhuca-indicadores/api/srv/auth/login', {
        email,
        password
      });

      const { data } = response;
      
      if (data.entity?.jwt) {
        localStorage.setItem('authToken', data.entity.jwt);
        setIsAuthenticated(true);
        
        // Fetch and set user role after successful login
        const role = await getUserRole();
        setUserRole(role);
        
        return { success: true, token: data.entity.jwt };
      } else {
        throw new Error('No se recibió token de autenticación');
      }
    } catch (err) {
      console.error('Error de login:', err);
      return { 
        success: false, 
        error: err.response?.data?.message || 'Error al iniciar sesión'
      };
    }
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    setIsAuthenticated(false);
    setUser(null);
    setUserRole(null);
    navigate('/login');
  };

  const checkTokenValidity = async () => {
    try {
      const token = localStorage.getItem('authToken');
      if (!token) {
        setIsAuthenticated(false);
        return false;
      }

      const response = await axios.post(
        'http://localhost:8080/idhuca-indicadores/api/srv/auth/verify',
        {},
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );

      return response.status === 200;
    } catch (err) {
      console.error('Error al verificar token:', err);
      logout();
      return false;
    }
  };

  useEffect(() => {
    const initializeAuth = async () => {
      const token = localStorage.getItem('authToken');
      if (token) {
        try {
          const isValid = await checkTokenValidity();
          if (isValid) {
            setIsAuthenticated(true);
            const role = await getUserRole();
            setUserRole(role);
          }
        } catch (error) {
          console.error('Error initializing auth:', error);
          logout();
        }
      }
      setLoading(false);
    };

    initializeAuth();
  }, []);

  const value = {
    isAuthenticated,
    user,
    userRole,
    loading,
    login,
    logout,
    checkTokenValidity,
    getUserRole
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;