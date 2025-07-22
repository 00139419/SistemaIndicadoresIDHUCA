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

  const MAX_TIMEOUT = 2147483647; // ~24.8 días, límite de setTimeout

  const programLogoutOnExpiry = (jwt) => {
    try {
      const { exp } = jwtDecode(jwt); // exp en segundos
      const now = Date.now(); // en milisegundos
      let msUntilExpiry = exp * 1000 - now;

      console.log(
        "[Auth] JWT exp:",
        exp,
        "(UTC seg)",
        "now:",
        now,
        "(ms)",
        "msUntilExpiry:",
        msUntilExpiry
      );

      if (msUntilExpiry <= 0) {
        console.warn("[Auth] Token expirado, logout inmediato");
        logout();
      } else {
        // Limitar el timeout para evitar errores en navegadores
        if (msUntilExpiry > MAX_TIMEOUT) {
          console.warn(
            `[Auth] Tiempo hasta expiración (${msUntilExpiry}) supera el máximo permitido (${MAX_TIMEOUT}). Truncando.`
          );
          msUntilExpiry = MAX_TIMEOUT;
        }

        // Limpiar temporizador anterior si existe
        if (window.__logoutTimer) clearTimeout(window.__logoutTimer);

        window.__logoutTimer = setTimeout(() => {
          console.info(
            "[Auth] Sesión cerrada automáticamente por expiración de JWT."
          );
          logout();
        }, msUntilExpiry);
      }
    } catch (err) {
      console.error("[Auth] Error decodificando JWT:", err);
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
        "http://localhost:8080/idhuca-indicadores/api/srv/users/get/current",
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      return response.status === 200 || response.status < 300;
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

      const minutos = Number(data?.entity?.valor);

      const MAX_IDLE_MINUTES = Math.floor(2147483647 / (1000 * 60)); // ≈ 35791 min ≈ 596 h

      if (isNaN(minutos) || minutos <= 0) {
        console.warn(
          "Tiempo inválido de inactividad. Usando por defecto 15 minutos."
        );
        return 15 * 60 * 1000;
      }

      if (minutos > MAX_IDLE_MINUTES) {
        console.warn(
          `Tiempo de inactividad demasiado alto (${minutos} min). Truncando a ${MAX_IDLE_MINUTES} min.`
        );
        return MAX_IDLE_MINUTES * 60 * 1000;
      }

      return minutos * 60 * 1000; // Tiempo válido
    } catch (e) {
      console.error("No se pudo obtener tiempo de inactividad:", e);
      return 15 * 60 * 1000; // Valor por defecto
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
