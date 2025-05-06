import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import { useNavigate, useLocation } from "react-router-dom";
import logoUCA from "../assets/idhuca-logo-blue.png";

const VerifyIdentity = () => {
  // Estado para almacenar la respuesta del usuario
  const [answer, setAnswer] = useState("");
  // Estado para almacenar el email del usuario (normalmente vendría de un contexto o props)
  const [userEmail, setUserEmail] = useState("");

  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    // obtener el email del usuario desde props, contexto, localStorage o state del router
    // state del router:
    if (location.state && location.state.email) {
      setUserEmail(location.state.email);
    } else {
      // Si no hay email en el state, obtenerlo de localStorage o de algún estado global
      const storedEmail = localStorage.getItem("userEmail");
      if (storedEmail) {
        setUserEmail(storedEmail);
      } else {
        // Si no hay email almacenado, redirigir al login
        setUserEmail("********91@gmail.com");
      }
    }
  }, [location, navigate]);

  const handleSubmit = (e) => {
    e.preventDefault();
    // Aquí iría la lógica para verificar la respuesta
    console.log("Respuesta a la pregunta de seguridad:", answer);
    // Si la respuesta es correcta, redirigir al usuario a la página de cambio de contraseña
    // navigate('/reset-password');
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
      <div className="text-center mb-4 mt-2">
        <h1 className="fw-bold mb-5">Verificar Identidad</h1>
        <p className="text-muted">
          Responda la pregunta de seguridad para verificar la cuenta
          <br />
          <strong>{userEmail}</strong>
        </p>
      </div>

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="securityQuestion" className="form-label fw-medium">
              Pregunta
            </label>
            <div className="bg-light p-2 rounded mb-3">
              ¿Cuál fue el nombre de tu primer mascota?
            </div>
          </div>

          <div className="mb-4">
            <label htmlFor="securityAnswer" className="form-label fw-medium">
              Respuesta
            </label>
            <input
              type="text"
              className="form-control bg-light"
              id="securityAnswer"
              placeholder="sanson"
              value={answer}
              onChange={(e) => setAnswer(e.target.value)}
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
            verificar cuenta
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

export default VerifyIdentity;
