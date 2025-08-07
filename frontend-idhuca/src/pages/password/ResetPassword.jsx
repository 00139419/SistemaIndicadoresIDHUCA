import React, { useState, useEffect } from "react";
import "bootstrap/dist/css/bootstrap.min.css";
import { useNavigate } from "react-router-dom";
import logoUCA from "../../assets/idhuca-logo-blue.png";
import axios from "axios";

const ResetPassword = () => {
  const API_URL = process.env.REACT_APP_API_URL;
  const API_BACKUP_URL = process.env.REACT_APP_API_BACKUP;
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [isProvisional, setIsProvisional] = useState(false);
  const [showProvisionalForm, setShowProvisionalForm] = useState(false);
  const [securityQuestions, setSecurityQuestions] = useState([]);
  const [provisionalData, setProvisionalData] = useState({
    currentPassword: "",
    newPassword: "",
    selectedQuestion: "",
    securityAnswer: ""
  });

  const navigate = useNavigate();

  const fetchSecurityQuestions = async () => {
    try {
      const token = localStorage.getItem("tempAuthToken");
      const response = await axios.post(
        API_URL + 'catalogo/get',
        {
          securityQuestions: true
        },
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );
      setSecurityQuestions(response.data.entity || []);
    } catch (err) {
      console.error("Error fetching security questions:", err);
      setError("Error al cargar las preguntas de seguridad");
    }
  };

  const handleProvisionalSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const token = localStorage.getItem("tempAuthToken");
      const response = await axios.post(
        API_URL + 'users/change/password',
        {
          email: localStorage.getItem("userEmail"),
          password: provisionalData.currentPassword,
          newPassword: provisionalData.newPassword,
          securityQuestion: {
            codigo: provisionalData.selectedQuestion
          },
          securityAnswer: provisionalData.securityAnswer
        },
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );

      if (response.data) {
        localStorage.removeItem("tempAuthToken");
        localStorage.removeItem("userEmail");
        navigate("/login");
      }
    } catch (err) {
      console.error("Error:", err);
      setError(err.response?.data?.mensaje || "Error al actualizar la contraseña. Por favor intente más tarde.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem("tempAuthToken");
    const userEmail = localStorage.getItem("userEmail");
    
    if (token && userEmail) {
      setEmail(userEmail);
      setIsProvisional(true);
      setShowProvisionalForm(true);
      fetchSecurityQuestions();
    }
  }, []);

  // Modify handleSubmit for non-provisional accounts only
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      const response = await axios.post(
        API_URL + 'auth/get/securityQuestion',
        { email }
      );

      localStorage.setItem("resetEmail", email);
      localStorage.setItem("securityQuestion", response.data.entity.descripcion);
      navigate("/verify-identity");
    } catch (err) {
      console.error("Error:", err);
      if (err.response?.status === 404) {
        setError("No se encontró una cuenta con ese correo electrónico.");
      } else {
        setError(err.response?.data?.mensaje || "Error al enviar el correo de recuperación. Por favor intente más tarde.");
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
          <p className="text-muted mb-3 mt-3">
            {showProvisionalForm 
              ? "Configure su nueva contraseña y pregunta de seguridad" 
              : "Ingresa tu correo electrónico para comenzar"}
          </p>
        </div>

        {!showProvisionalForm ? (
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
        ) : (
          <form onSubmit={handleProvisionalSubmit}>
            {error && (
              <div className="alert alert-danger" role="alert">
                {error}
              </div>
            )}

            <div className="mb-3">
              <label htmlFor="currentPassword" className="form-label fw-medium">
                Contraseña Provisional
              </label>
              <input
                type="password"
                className="form-control bg-light"
                id="currentPassword"
                value={provisionalData.currentPassword}
                onChange={(e) => setProvisionalData({
                  ...provisionalData,
                  currentPassword: e.target.value
                })}
                required
              />
            </div>

            <div className="mb-3">
              <label htmlFor="newPassword" className="form-label fw-medium">
                Nueva Contraseña
              </label>
              <input
                type="password"
                className="form-control bg-light"
                id="newPassword"
                value={provisionalData.newPassword}
                onChange={(e) => setProvisionalData({
                  ...provisionalData,
                  newPassword: e.target.value
                })}
                required
              />
            </div>

            <div className="mb-3">
              <label htmlFor="securityQuestion" className="form-label fw-medium">
                Pregunta de Seguridad
              </label>
              <select
                className="form-select bg-light"
                id="securityQuestion"
                value={provisionalData.selectedQuestion}
                onChange={(e) => setProvisionalData({
                  ...provisionalData,
                  selectedQuestion: e.target.value
                })}
                required
              >
                <option value="">Seleccione una pregunta</option>
                {securityQuestions.map((question) => (
                  <option key={question.codigo} value={question.codigo}>
                    {question.descripcion}
                  </option>
                ))}
              </select>
            </div>

            <div className="mb-3">
              <label htmlFor="securityAnswer" className="form-label fw-medium">
                Respuesta
              </label>
              <input
                type="text"
                className="form-control bg-light"
                id="securityAnswer"
                value={provisionalData.securityAnswer}
                onChange={(e) => setProvisionalData({
                  ...provisionalData,
                  securityAnswer: e.target.value
                })}
                required
              />
            </div>

            <button
              type="submit"
              className="btn btn-dark w-100 py-2"
              disabled={loading}
            >
              {loading ? "Actualizando..." : "Actualizar Contraseña"}
            </button>
          </form>
        )}

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