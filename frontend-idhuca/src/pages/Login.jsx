import { useState } from "react";
import logoUCA from "../assets/idhuca-logo-blue.png";
import { useAuth } from "../components/AuthContext";
import { useNavigate } from "react-router-dom";
import axios from "axios";

export default function LoginForm() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [credentials, setCredentials] = useState({
    email: "",
    password: "",
  });
  const [error, setError] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials({
      ...credentials,
      [name]: value,
    });
    if (error) setError(false);
  };

  const checkProvisionalStatus = async (token) => {
    try {
      const response = await axios.post(
        "http://localhost:8080/idhuca-indicadores/api/srv/users/get/current",
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      // Check the esPasswordProvisional field from the entity
      return response.data.entity.esPasswordProvisional || false;
    } catch (error) {
      console.error("Error checking provisional status:", error);
      throw new Error("Error verificando estado de la cuenta");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError(false);

    try {
      const result = await login(credentials.email, credentials.password);

      if (!result.success || !result.token) {
        throw new Error(result.error || "Error al iniciar sesión");
      }

      // Store token temporarily until we verify provisional status
      const token = result.token;

      // Check provisional status using the token
      const isProvisional = await checkProvisionalStatus(token);

      if (isProvisional) {
        // Store necessary data for password reset
        localStorage.setItem("tempAuthToken", token);
        localStorage.setItem("userEmail", credentials.email);
        setErrorMessage(
          "Cuenta provisional detectada - Redirigiendo para cambiar contraseña"
        );
        setError(true);
        setTimeout(() => {
          navigate("/reset-password");
        }, 2000);
        return;
      }

      // If not provisional, proceed with normal login
      localStorage.setItem("authToken", token);
      navigate("/index");
    } catch (err) {
      setError(true);
      setErrorMessage(
        err.message || "Error en inicio de sesión, verifique usuario o contraseña"
      );
      console.error("Error de login:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleForgotPassword = () => {
    navigate("/reset-password");
  };

  return (
    <div
      className="d-flex justify-content-center align-items-center"
      style={{
        backgroundColor: "#003C71",
        minHeight: "100vh",
      }}
    >
      <div
        className="card shadow border-0"
        style={{
          maxWidth: "500px",
          width: "100%",
          padding: "4rem",
          borderRadius: "12px",
        }}
      >
        <div className="text-center mb-4">
          <h1
            className="fw-bold"
            style={{
              fontSize: "2rem",
              marginBottom: "1rem",
            }}
          >
            Iniciar sesión
          </h1>
          <p
            className="text-secondary"
            style={{
              fontSize: "1.125rem",
              marginBottom: "2rem",
            }}
          >
            Inicie sesión para continuar
          </p>
        </div>

        <form onSubmit={handleSubmit}>
          {error && (
            <div
              className="alert alert-danger d-flex align-items-center"
              role="alert"
              style={{
                backgroundColor: "#f44336",
                color: "white",
                border: "none",
                borderRadius: "4px",
                padding: "0.75rem",
                marginBottom: "1.5rem",
                fontSize: "0.9rem",
              }}
            >
              {
                errorMessage ||
                "Error en inicio de sesión, verifique usuario o contraseña"
              }
            </div>
          )}

          <div className="mb-3">
            <label htmlFor="email" className="form-label">
              Usuario
            </label>
            <div className="input-group">
              <input
                type="email"
                className="form-control form-control-lg bg-light"
                id="email"
                name="email"
                value={credentials.email}
                onChange={handleChange}
                placeholder="correo@uca.edu.sv"
                style={{ marginBottom: "0.5rem" }}
              />
              <span className="input-group-text bg-light">
                <i className="bi bi-person"></i>
              </span>
            </div>
          </div>

          <div className="mb-4">
            <label htmlFor="password" className="form-label">
              Contraseña
            </label>
            <div className="input-group">
              <input
                type="password"
                className="form-control form-control-lg bg-light"
                id="password"
                name="password"
                value={credentials.password}
                onChange={handleChange}
                style={{ marginBottom: "0.5rem" }}
              />
              <span className="input-group-text bg-light">
                <i className="bi bi-eye"></i>
              </span>
            </div>
          </div>

          <div className="text-center mb-4">
            <a
              href="#"
              className="text-secondary text-decoration-none"
              onClick={(e) => {
                e.preventDefault();
                handleForgotPassword();
              }}
              style={{ fontSize: "1rem" }}
            >
              ¿Olvidaste tu contraseña?
            </a>
          </div>

          <div className="d-grid gap-2">
            <button
              type="submit"
              className="btn btn-dark btn-lg"
              style={{
                fontSize: "1.125rem",
                padding: "0.75rem",
                backgroundColor: "#333333",
              }}
              disabled={isLoading}
            >
              {isLoading ? (
                <span>
                  <span
                    className="spinner-border spinner-border-sm me-2"
                    role="status"
                    aria-hidden="true"
                  ></span>
                  Cargando...
                </span>
              ) : (
                "iniciar sesión"
              )}
            </button>
          </div>
        </form>

        <div className="text-center mt-5">
          <img
            src={logoUCA}
            alt="Instituto de Derechos Humanos de la UCA"
            style={{ height: "50px", width: "auto" }}
          />
        </div>
      </div>
    </div>
  );
}