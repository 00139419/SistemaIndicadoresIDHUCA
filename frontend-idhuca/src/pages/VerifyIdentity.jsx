import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import { useNavigate } from "react-router-dom";
import logoUCA from "../assets/idhuca-logo-blue.png";
import axios from "axios";

const VerifyIdentity = () => {
  const API_URL = process.env.REACT_APP_API_URL;
  const API_BACKUP_URL = process.env.REACT_APP_API_BACKUP;
  const [securityAnswer, setSecurityAnswer] = useState("");
  const [securityQuestion, setSecurityQuestion] = useState("");
  const [userEmail, setUserEmail] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const email = localStorage.getItem("resetEmail");
    const question = localStorage.getItem("securityQuestion");

    if (!email || !question) {
      navigate("/reset-password");
      return;
    }

    setUserEmail(email);
    setSecurityQuestion(question);
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const response = await axios.post(
        API_URL +"auth/get/securityQuestion",
        {
          email: userEmail,
          securityAnswer,
        }
      );

      if (response.data) {
        // Store the actual securityAnswer value, not the state variable name
        localStorage.setItem("verifiedEmail", userEmail);
        localStorage.setItem("userSecurityAnswer", securityAnswer); // Changed key name for clarity
        navigate("/set-new-password");
      }
    } catch (err) {
      console.error("Error:", err);
      setError("La respuesta de seguridad es incorrecta.");
    } finally {
      setLoading(false);
    }
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
          <h1 className="fw-bold mb-2 mt-5">Verificar Identidad</h1>
          <p className="text-muted mb-3 mt-3">
            Responda la pregunta de seguridad para la cuenta:
            <br />
            <strong>{userEmail}</strong>
          </p>
        </div>

        <form onSubmit={handleSubmit}>
          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
            </div>
          )}

          <div className="mb-3">
            <label className="form-label fw-medium">
              Pregunta de seguridad:
            </label>
            <div className="bg-light p-3 rounded mb-3">{securityQuestion}</div>
          </div>

          <div className="mb-4">
            <label htmlFor="securityAnswer" className="form-label fw-medium">
              Respuesta
            </label>
            <input
              type="text"
              className="form-control bg-light"
              id="securityAnswer"
              placeholder="Ingrese su respuesta"
              value={securityAnswer}
              onChange={(e) => setSecurityAnswer(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-dark w-100 py-2"
            disabled={loading}
          >
            {loading ? "Verificando..." : "Verificar"}
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
