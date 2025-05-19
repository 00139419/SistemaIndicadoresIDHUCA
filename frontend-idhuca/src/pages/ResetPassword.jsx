import React, { useState } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import { useNavigate } from "react-router-dom";
import logoUCA from "../assets/idhuca-logo-blue.png";
import axios from "axios";

const ResetPassword = () => {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const response = await axios.get(
        'http://localhost:8080/idhuca-indicadores/api/srv/auth/get/securityQuestion',
        {
          params: { email },
          headers: {
            'Content-Type': 'application/json'
          }
        }
      );

      if (response.data) {
        localStorage.setItem("resetEmail", email);
        localStorage.setItem("securityQuestion", response.data.securityQuestion);
        navigate("/verify-identity");
      }
    } catch (err) {
      console.error("Error:", err);
      if (err.response) {
        switch (err.response.status) {
          case 404:
            setError("No se encontró una cuenta con ese correo electrónico.");
            break;
          default:
            setError("Error al conectar con el servidor. Por favor intente más tarde.");
        }
      } else {
        setError("Error al conectar con el servidor. Por favor intente más tarde.");
      }
    } finally {
      setLoading(false);
    }
  };


  return (
    <div className="vh-100 d-flex align-items-center justify-content-center" style={{ backgroundColor: "#003C71" }}>
      <div className="bg-white rounded-4 p-4 p-md-5 shadow" style={{ maxWidth: "500px", width: "100%" }}>
        <div className="text-center mb-4">
          <h1 className="fw-bold mb-2 mt-5">Recuperar Contraseña</h1>
          <p className="text-muted mb-3 mt-3">Ingresa tu correo electrónico para comenzar</p>
        </div>

        <form onSubmit={handleSubmit}>
          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          <div className="mb-3">
            <label htmlFor="email" className="form-label fw-medium">
              Correo electrónico
            </label>
            <input
              type="email"
              className="form-control bg-light"
              id="email"
              placeholder="Ingrese su correo electrónico"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-dark w-100 py-2"
            disabled={loading}
          >
            {loading ? "Verificando..." : "Continuar"}
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