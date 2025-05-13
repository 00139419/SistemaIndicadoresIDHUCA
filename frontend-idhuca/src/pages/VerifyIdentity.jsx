import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import { useNavigate, useLocation } from "react-router-dom";
import logoUCA from "../assets/idhuca-logo-blue.png";

const VerifyIdentity = () => {
  const [answer, setAnswer] = useState("");
  const [userEmail, setUserEmail] = useState("");
  const [error, setError] = useState(""); // State for error message
  const [retryCounter, setRetryCounter] = useState(0); // State for retry countdown
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    if (location.state && location.state.email) {
      setUserEmail(location.state.email);
    } else {
      const storedEmail = localStorage.getItem("userEmail");
      if (storedEmail) {
        setUserEmail(storedEmail);
      } else {
        setUserEmail("********91@gmail.com");
      }
    }
  }, [location]);

  const handleSubmit = (e) => {
    e.preventDefault();

    // Simulate verification logic
    const isAnswerCorrect = false; // Simulate a failed verification
    if (!isAnswerCorrect) {
      setError("No se pudo validar identidad. Inténtalo de nuevo.");
      setRetryCounter(10); // Set retry countdown to 10 seconds

      // Start countdown
      const interval = setInterval(() => {
        setRetryCounter((prev) => {
          if (prev <= 1) {
            clearInterval(interval);
            setError(""); // Clear error after countdown
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    } else {
      // Navigate to the next page if verification succeeds
      navigate("/reset-password");
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

          {error && (
            <div
              className="alert alert-danger text-center"
              role="alert"
            >
              {error} {retryCounter > 0 && `Puedes volver a intentar en ${retryCounter} segundos.`}
            </div>
          )}

          <div className="text-center mb-4">
            <button
              type="button"
              className="btn btn-link text-decoration-none p-0"
              onClick={handleLoginRedirect}
            >
              ¿Recuerdas tu contraseña? Inicia sesión aquí
            </button>
          </div>

          <button
            type="submit"
            className="btn btn-dark w-100 py-2"
            disabled={retryCounter > 0} // Disable button during countdown
          >
            Verificar cuenta
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

export default VerifyIdentity;