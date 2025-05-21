import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import { useNavigate } from "react-router-dom";
import logoUCA from "../assets/idhuca-logo-blue.png";
import axios from "axios";

const SetNewPassword = () => {
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const verifiedEmail = localStorage.getItem("verifiedEmail");
    if (!verifiedEmail) {
      navigate("/reset-password");
    }
  }, [navigate]);

  const handleSubmit = async (e) => {
  e.preventDefault();
  setLoading(true);
  setError("");

  if (newPassword !== confirmPassword) {
    setError("Las contraseñas no coinciden");
    setLoading(false);
    return;
  }

  try {
    const email = localStorage.getItem("verifiedEmail");
    const securityAnswer = localStorage.getItem("userSecurityAnswer"); // Updated key name

    if (!email || !securityAnswer) {
      setError("Información de verificación incompleta");
      navigate("/reset-password");
      return;
    }

    const response = await axios.post(
      "http://localhost:8080/idhuca-indicadores/api/srv/auth/recovery/password",
      {
        email,
        newPassword,
        securityAnswer
      }
    );

    if (response.data) {
    
      localStorage.removeItem("resetEmail");
      localStorage.removeItem("securityQuestion");
      localStorage.removeItem("verifiedEmail");
      localStorage.removeItem("userSecurityAnswer"); 
      
      navigate("/reset-password-success", { 
        state: { message: "Contraseña actualizada con éxito" }
      });
    }
  } catch (err) {
    console.error("Error:", err);
    setError("Error al actualizar la contraseña. Intente nuevamente.");
  } finally {
    setLoading(false);
  }
};

  return (
    <div className="vh-100 d-flex align-items-center justify-content-center" style={{ backgroundColor: "#003C71" }}>
      <div className="bg-white rounded-4 p-4 p-md-5 shadow" style={{ maxWidth: "500px", width: "100%" }}>
        <div className="text-center mb-4">
          <h1 className="fw-bold mb-2 mt-5">Nueva Contraseña</h1>
          <p className="text-muted mb-3 mt-3">Ingrese su nueva contraseña</p>
        </div>

        <form onSubmit={handleSubmit}>
          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          <div className="mb-3">
            <label htmlFor="newPassword" className="form-label fw-medium">
              Nueva contraseña
            </label>
            <input
              type="password"
              className="form-control bg-light"
              id="newPassword"
              placeholder="Ingrese su nueva contraseña"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              required
            />
          </div>

          <div className="mb-4">
            <label htmlFor="confirmPassword" className="form-label fw-medium">
              Confirmar contraseña
            </label>
            <input
              type="password"
              className="form-control bg-light"
              id="confirmPassword"
              placeholder="Confirme su nueva contraseña"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-dark w-100 py-2"
            disabled={loading}
          >
            {loading ? "Actualizando..." : "Actualizar contraseña"}
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

export default SetNewPassword;