import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import logoUCA from "../assets/idhuca-logo-blue.png";

export default function LoginForm() {
  const navigate = useNavigate();
  const [credentials, setCredentials] = useState({
    username: "",
    password: "",
  });
  const [error, setError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCredentials({
      ...credentials,
      [name]: value,
    });
    // Limpiar el error cuando el usuario comienza a escribir de nuevo
    if (error) setError(false);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setIsLoading(true);

    // Simulación de verificación de credenciales
    setTimeout(() => {
      // Simulamos un error - esto deberías reemplazarlo con tu lógica real de autenticación
      if (credentials.username !== "usuario@correcto.com" || credentials.password !== "contraseña123") {
        setError(true);
        setIsLoading(false);
      } else {
        // Login exitoso
        setError(false);
        // Redirigir a la página principal o dashboard
        navigate('/index');
      }
    }, 1000);
  };

  const handleForgotPassword = () => {
    navigate('/verify-identity');
  };

  return (
    <div 
      className="d-flex justify-content-center align-items-center"
      style={{
        backgroundColor: "#003C71",
        minHeight: "100vh"
      }}
    >
      <div 
        className="card shadow border-0"
        style={{
          maxWidth: "500px",
          width: "100%",
          padding: "4rem",
          borderRadius: "12px"
        }}
      >
        <div className="text-center mb-4">
          <h1 
            className="fw-bold"
            style={{
              fontSize: "2rem",
              marginBottom: "1rem"
            }}
          >
            Iniciar sesión
          </h1>
          <p 
            className="text-secondary"
            style={{
              fontSize: "1.125rem",
              marginBottom: "2rem"
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
                fontSize: "0.9rem"
              }}
            >
              Error en inicio de sesión, verifique usuario o contraseña
            </div>
          )}

          <div className="mb-3">
            <label htmlFor="username" className="form-label">Usuario</label>
            <div className="input-group">
              <input
                type="text"
                className="form-control form-control-lg bg-light"
                id="username"
                name="username"
                value={credentials.username}
                onChange={handleChange}
                placeholder="testsidhuca@uca.edu.sv"
                style={{ marginBottom: "0.5rem" }}
              />
              <span className="input-group-text bg-light">
                <i className="bi bi-person"></i>
              </span>
            </div>
          </div>

          <div className="mb-4">
            <label htmlFor="password" className="form-label">Contraseña</label>
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
                backgroundColor: "#333333"
              }}
              disabled={isLoading}
            >
              {isLoading ? (
                <span>
                  <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
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