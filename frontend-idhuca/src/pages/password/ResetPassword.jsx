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
  // Password strength for provisional password
  const [passwordStrength, setPasswordStrength] = useState({ score: 0, text: "" });

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

  // Helper for password strength
  const checkPasswordStrength = (password) => {
    let score = 0;
    let text = "";
    if (password.length >= 8) score++;
    if (/[a-z]/.test(password)) score++;
    if (/[A-Z]/.test(password)) score++;
    if (/\d/.test(password)) score++;
    if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)) score++;
    switch (score) {
      case 0:
      case 1:
        text = "Muy débil";
        break;
      case 2:
        text = "Débil";
        break;
      case 3:
        text = "Regular";
        break;
      case 4:
        text = "Buena";
        break;
      case 5:
        text = "Fuerte";
        break;
      default:
        text = "";
    }
    return { score, text };
  };

  // Update password strength on change
  useEffect(() => {
    setPasswordStrength(checkPasswordStrength(provisionalData.newPassword));
  }, [provisionalData.newPassword]);

  // Helper for progress bar color
  const getProgressClass = (score) => {
    switch (score) {
      case 1:
      case 2:
        return "bg-danger";
      case 3:
        return "bg-warning";
      case 4:
        return "bg-info";
      case 5:
        return "bg-success";
      default:
        return "bg-secondary";
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
              {provisionalData.newPassword && (
                <div className="mt-2">
                  <div className="progress" style={{ height: "8px" }}>
                    <div
                      className={`progress-bar ${getProgressClass(passwordStrength.score)}`}
                      role="progressbar"
                      style={{ width: `${(passwordStrength.score / 5) * 100}%` }}
                      aria-valuenow={passwordStrength.score}
                      aria-valuemin="0"
                      aria-valuemax="5"
                    ></div>
                  </div>
                  <small className="text-muted mt-1 d-block">
                    {passwordStrength.score <= 2
                      ? "Contraseña vulnerable"
                      : passwordStrength.score === 3
                      ? "Contraseña segura"
                      : passwordStrength.score === 4
                      ? "Contraseña muy segura"
                      : passwordStrength.score === 5
                      ? "Contraseña super segura"
                      : ""}
                  </small>
                </div>
              )}
            </div>
            {/* Requisitos visuales */}
            <div className="card bg-light mb-4">
              <div className="card-body py-3">
                <h6 className="card-title mb-2">Requisitos de la contraseña:</h6>
                <ul className="list-unstyled mb-0">
                  <li className="py-1 d-flex align-items-center">
                    <span className="me-2">
                      {provisionalData.newPassword.length >= 8 ? (
                        <i className="bi bi-check-circle-fill text-success"></i>
                      ) : (
                        <i className="bi bi-circle text-secondary"></i>
                      )}
                    </span>
                    <small className="text-muted">Mínimo 8 caracteres</small>
                  </li>
                  <li className="py-1 d-flex align-items-center">
                    <span className="me-2">
                      {/[A-Z]/.test(provisionalData.newPassword) ? (
                        <i className="bi bi-check-circle-fill text-success"></i>
                      ) : (
                        <i className="bi bi-circle text-secondary"></i>
                      )}
                    </span>
                    <small className="text-muted">Al menos una letra mayúscula</small>
                  </li>
                  <li className="py-1 d-flex align-items-center">
                    <span className="me-2">
                      {/[a-z]/.test(provisionalData.newPassword) ? (
                        <i className="bi bi-check-circle-fill text-success"></i>
                      ) : (
                        <i className="bi bi-circle text-secondary"></i>
                      )}
                    </span>
                    <small className="text-muted">Al menos una letra minúscula</small>
                  </li>
                  <li className="py-1 d-flex align-items-center">
                    <span className="me-2">
                      {/\d/.test(provisionalData.newPassword) ? (
                        <i className="bi bi-check-circle-fill text-success"></i>
                      ) : (
                        <i className="bi bi-circle text-secondary"></i>
                      )}
                    </span>
                    <small className="text-muted">Al menos un número</small>
                  </li>
                  <li className="py-1 d-flex align-items-center">
                    <span className="me-2">
                      {/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(provisionalData.newPassword) ? (
                        <i className="bi bi-check-circle-fill text-success"></i>
                      ) : (
                        <i className="bi bi-circle text-secondary"></i>
                      )}
                    </span>
                    <small className="text-muted">Al menos un carácter especial (!@#$%^&*)</small>
                  </li>
                </ul>
              </div>
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