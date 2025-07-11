import React, { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const AuthContext = createContext();
const API_BASE_URL = 'http://localhost:8080/idhuca-indicadores/api/srv';
const API_GET_URL = `${API_BASE_URL}/users/get/current`;
const API_LOGIN_URL = `${API_BASE_URL}/auth/login`;
const API_VERIFY_URL = `${API_BASE_URL}/auth/verify`;
const API_REFRESH_URL = `${API_BASE_URL}/auth/refresh`;
const API_LOGOUT_URL = `${API_BASE_URL}/auth/logout`;

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
        API_GET_URL,
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
      const response = await axios.post(API_LOGIN_URL, {
        email,
        password
      });

      const { data } = response;

      if (data.entity?.jwt) {
        const { jwt, refreshToken } = data.entity;
        localStorage.setItem('authToken', jwt);
        localStorage.setItem('refreshToken', refreshToken);

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

  const notifyBackendLogout = async () => {
  try {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) return;

    await axios.post(
      API_LOGOUT_URL,
      { refreshToken },
      { headers: { 'Content-Type': 'application/json' } }
    );
  } catch (err) {
    console.warn('No se pudo notificar al backend del logout:', err.message);
  }
};

 const logout = async () => {
  await notifyBackendLogout(); // notifica al backend

  localStorage.removeItem('authToken');
  localStorage.removeItem('refreshToken');
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
        API_VERIFY_URL,
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

  const refreshAccessToken = async () => {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) throw new Error('No refresh token');

      const response = await axios.post(
        API_REFRESH_URL,
        { refreshToken },
        { headers: { 'Content-Type': 'application/json' } }
      );

      const { jwt, refreshToken: newRefresh } = response.data.entity;

      localStorage.setItem('authToken', jwt);
      localStorage.setItem('refreshToken', newRefresh); // opcionalmente rotado

      return jwt;
    } catch (err) {
      console.error('Error al refrescar token:', err);
      logout(); // Forzar logout si falla
      return null;
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

  useEffect(() => {
    const interceptor = axios.interceptors.response.use(
      res => res,
      async err => {
        const originalRequest = err.config;

        if (err.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          const newToken = await refreshAccessToken();
          if (newToken) {
            originalRequest.headers['Authorization'] = `Bearer ${newToken}`;
            return axios(originalRequest);
          }
        }

        return Promise.reject(err);
      }
    );

    return () => {
      axios.interceptors.response.eject(interceptor);
    };
  }, []);


  const value = {
    isAuthenticated,
    user,
    userRole,
    loading,
    login,
    logout,
    checkTokenValidity,
    getUserRole,
    refreshAccessToken
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;