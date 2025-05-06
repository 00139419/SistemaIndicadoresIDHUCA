import React, { useState } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import { useNavigate } from "react-router-dom";
import logoUCA from "../assets/idhuca-logo-blue.png";

const ResetPassword = () => {
  const [passwords, setPasswords] = useState({
    newPassword: "",
    confirmPassword: "",
  });
  const [error, setError] = useState("");
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

  const handleSubmit = (e) => {
    e.preventDefault();

    // Validar que las contraseñas coincidan
    if (passwords.newPassword !== passwords.confirmPassword) {
      setError("Las contraseñas no coinciden");
      return;
    }

    // Aquí iría la lógica para enviar la nueva contraseña al servidor
    console.log("Nueva contraseña establecida:", passwords.newPassword);

    // Redireccionar al login después de cambiar la contraseña
    // navigate('/login');
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
        <h1 className="fw-bold mb-2 mt-5">Restablecer Contraseña</h1>
        <p className="text-muted mb-3 mt-3">¡Ya puedes restablecer tu contraseña!</p>
      </div>

        <form onSubmit={handleSubmit}>
          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          <div className="mb-3">
            <label htmlFor="newPassword" className="form-label fw-medium">
              Digite su nueva contraseña
            </label>
            <input
              type="password"
              className="form-control bg-light"
              id="newPassword"
              name="newPassword"
              placeholder="Contraseña123"
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
              placeholder="************"
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
              ¿recuerdas tu contraseña? Inicia sesión aquí
            </button>
          </div>

          <button type="submit" className="btn btn-dark w-100 py-2">
            restablecer contraseña
          </button>
        </form>

        <div className="text-center mt-5">
          <img
            src={logoUCA}
            alt="Instituto de Derechos Humanos de la UCA"
            style={{ height: "50px", width: "auto" }} // Slightly larger logo
          />
        </div>
      </div>
    </div>
  );
};

export default ResetPassword;
