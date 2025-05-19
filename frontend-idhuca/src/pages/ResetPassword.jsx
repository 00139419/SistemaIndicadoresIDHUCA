import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import { useNavigate } from "react-router-dom";
import logoUCA from "../assets/idhuca-logo-blue.png";
import { useAuth } from "../components/AuthContext"; 
import axios from "axios";

const ResetPassword = () => {
  const [passwords, setPasswords] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setPasswords((prev) => ({
      ...prev,
      [name]: value,
    }));
    // Limpiar error cuando el usuario empieza a escribir
    if (error) setError("");
  };

  const { checkTokenValidity } = useAuth(); 

 const handleSubmit = async (e) => {
  e.preventDefault();
  setLoading(true);
  setError("");
  setSuccess("");

  // Validate if the passwords are the same
  if (passwords.newPassword !== passwords.confirmPassword) {
    setError("Las contraseñas no coinciden");
    setLoading(false);
    return;
  }

  try {
    const authToken = localStorage.getItem('authToken');
    if (!authToken) {
      setError("No se encontró un token de autenticación. Por favor inicia sesión nuevamente.");
      navigate('/login');
      return;
    }

    // Check token validity first
    const isValid = await checkTokenValidity();
    if (!isValid) {
      setError("Su sesión ha expirado. Por favor inicie sesión nuevamente.");
      navigate('/login');
      return;
    }

    const config = {
      headers: {
        'Authorization': `Bearer ${authToken}`,
        'Content-Type': 'application/json'
      }
    };

    const data = {
      password: passwords.currentPassword,
      newPassword: passwords.newPassword
    };

    const response = await axios.put(
      '/idhuca-indicadores/api/srv/users/change/password',
      data,
      config
    );

    if (response.data) {
      setSuccess("Tu contraseña ha sido cambiada con éxito");
      // Wait a moment before redirecting
      setTimeout(() => {
        navigate('/reset-password-success');
      }, 2000);
    }

  } catch (err) {
    console.error("Error completo:", err);
    if (err.response) {
      if (err.response.status === 401) {
        setError("Credenciales inválidas o sesión expirada.");
        setTimeout(() => navigate('/login'), 2000);
      } else {
        setError(err.response.data.message || "Error al cambiar la contraseña.");
      }
    } else if (err.request) {
      setError("Error de conexión con el servidor.");
    } else {
      setError("Error al procesar la solicitud.");
    }
  } finally {
    setLoading(false);
  }
};

  const handleLoginRedirect = () => {
    navigate("/login");
  };

  return (
    <div
      className="vh-100 d-flex align-items-center justify-content-center"
      style={{ backgroundColor: "#003C71" }}
    >
      <div
        className="bg-white rounded-4 p-4 p-md-5 shadow"
        style={{ maxWidth: "500px", width: "100%" }}
      >
        <div className="text-center mb-4">
          <h1 className="fw-bold mb-2 mt-5">Cambiar Contraseña</h1>
          <p className="text-muted mb-3 mt-3">Actualiza tu contraseña de forma segura</p>
        </div>

        <form onSubmit={handleSubmit}>
          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}
          
          {success && (
            <div className="alert alert-success" role="alert">
              {success}
            </div>
          )}

          <div className="mb-3">
            <label htmlFor="currentPassword" className="form-label fw-medium">
              Contraseña actual
            </label>
            <input
              type="password"
              className="form-control bg-light"
              id="currentPassword"
              name="currentPassword"
              placeholder="Ingrese su contraseña actual"
              value={passwords.currentPassword}
              onChange={handleChange}
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="newPassword" className="form-label fw-medium">
              Nueva contraseña
            </label>
            <input
              type="password"
              className="form-control bg-light"
              id="newPassword"
              name="newPassword"
              placeholder="Ingrese su nueva contraseña"
              value={passwords.newPassword}
              onChange={handleChange}
              required
            />
          </div>

          <div className="mb-4">
            <label htmlFor="confirmPassword" className="form-label fw-medium">
              Confirmar nueva contraseña
            </label>
            <input
              type="password"
              className="form-control bg-light"
              id="confirmPassword"
              name="confirmPassword"
              placeholder="Confirme su nueva contraseña"
              value={passwords.confirmPassword}
              onChange={handleChange}
              required
            />
          </div>

          <div className="text-center mb-4">
            <button
              type="button"
              className="btn btn-link text-decoration-none p-0"
              onClick={handleLoginRedirect}
            >
              ¿Deseas iniciar sesión? Haz clic aquí
            </button>
          </div>

          <button 
            type="submit" 
            className="btn btn-dark w-100 py-2" 
            disabled={loading}
          >
            {loading ? "Procesando..." : "Cambiar contraseña"}
          </button>
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
};

export default ResetPassword;