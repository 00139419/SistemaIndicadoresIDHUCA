import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
} from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { authFetch } from "../authFetch";
import { useIdleTimer } from "../hooks/useIdleTimer";

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [userRole, setUserRole] = useState(null);
  const [loading, setLoading] = useState(true);
  const [token, setToken] = useState(localStorage.getItem("authToken"));
  const navigate = useNavigate();

  // --- ①: Tiempo máximo de inactividad (ms). Puedes traerlo desde la BD si quieres.
  const [idleTimeout, setIdleTimeout] = useState(null);

  const getUserRole = async () => {
    try {
      const token = localStorage.getItem("authToken");
      if (!token) {
        throw new Error("No token found");
      }

      const response = await axios.post(
        "http://localhost:8080/idhuca-indicadores/api/srv/users/get/current",
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (response.data && response.data.entity && response.data.entity.rol) {
        return response.data.entity.rol.codigo;
      }
      return null;
    } catch (error) {
      console.error("Error fetching user role:", error);
      return null;
    }
  };

  const login = async (email, password) => {
    try {
      const response = await axios.post(
        "http://localhost:8080/idhuca-indicadores/api/srv/auth/login",
        {
          email,
          password,
        }
      );

      const { data } = response;

      if (data.entity?.jwt) {
        localStorage.setItem("authToken", data.entity.jwt);

        setIsAuthenticated(true);
        programLogoutOnExpiry(data.entity.jwt);
        setToken(data.entity.jwt);

        // obtenemos rol y timeout en paralelo
        const [role, timeout] = await Promise.all([
          getUserRole(),
          getIdleTimeoutFromApi(),
        ]);

        setUserRole(role);
        setIdleTimeout(timeout);

        return { success: true, token: data.entity.jwt };
      } else {
        throw new Error("No se recibió token de autenticación");
      }
    } catch (err) {
      console.error("Error de login:", err);
      return {
        success: false,
        error: err.response?.data?.message || "Error al iniciar sesión",
      };
    }
  };

  useEffect(() => {
    if (token) programLogoutOnExpiry(token);
  }, [token]);

  const programLogoutOnExpiry = (jwt) => {
    try {
      const { exp } = jwtDecode(jwt);
      const msUntilExpiry = exp * 1000 - Date.now();
      if (msUntilExpiry <= 0) {
        logout();
      } else {
        // Clean previous timer
        if (window.__logoutTimer) clearTimeout(window.__logoutTimer);
        window.__logoutTimer = setTimeout(() => {
          logout();
        }, msUntilExpiry);
      }
    } catch (_) {
      logout(); // token corrupto
    }
  };

  const logout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("user");
    setIsAuthenticated(false);
    setUser(null);
    setUserRole(null);
    setIdleTimeout(null); 
    navigate("/login");
  };

  const checkTokenValidity = async () => {
    try {
      const token = localStorage.getItem("authToken");
      if (!token) {
        setIsAuthenticated(false);
        return false;
      }

      const response = await axios.post(
        "http://localhost:8080/idhuca-indicadores/api/srv/auth/verify",
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      return response.status === 200;
    } catch (err) {
      console.error("Error al verificar token:", err);
      logout();
      return false;
    }
  };

  useEffect(() => {
    const initializeAuth = async () => {
      const token = localStorage.getItem("authToken");
      if (token) {
        try {
          const isValid = await checkTokenValidity();
          if (isValid) {
            setIsAuthenticated(true);
            const role = await getUserRole();
            setUserRole(role);
          }
        } catch (error) {
          console.error("Error initializing auth:", error);
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
    getUserRole,
  };

  const getIdleTimeoutFromApi = useCallback(async () => {
    try {
      const tkn = localStorage.getItem("authToken");
      if (!tkn) throw new Error("No token");

      const { data } = await axios.post(
        "http://localhost:8080/idhuca-indicadores/api/srv/parametros/sistema/getOne",
        { clave: "max_tiempo_inactividad" },
        { headers: { Authorization: `Bearer ${tkn}` } }
      );

      const min = Number(data?.entity?.valor);

      return !isNaN(min) && min > 0 ? min * 60 * 1000 : 15 * 60 * 1000;
    } catch (e) {
      console.error("No se pudo obtener tiempo de inactividad:", e);
      return 15 * 60 * 1000;
    }
  }, []);

  useEffect(() => {
    if (isAuthenticated && idleTimeout == null) {
      getIdleTimeoutFromApi().then((timeout) => {
        console.log("Timeout recibido:", timeout);
        setIdleTimeout(timeout);
      });
    }
  }, [isAuthenticated, idleTimeout, getIdleTimeoutFromApi]);

  // --- función estable:
  const handleIdle = useCallback(() => {
    console.info("Sesión cerrada por inactividad");
    logout();
  }, [logout]);

  useIdleTimer(handleIdle, isAuthenticated ? idleTimeout : null);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export default AuthContext;
