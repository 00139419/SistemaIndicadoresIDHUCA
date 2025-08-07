import React, { useState, useEffect } from "react";

const Configuraciones = () => {
  const API_URL = process.env.REACT_APP_API_URL;
  const API_BACKUP_URL = process.env.REACT_APP_API_BACKUP;

  const [formData, setFormData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const [profileData, setProfileData] = useState({
    id: null,
    currentName: "",
    newName: "",
  });

  const [showAlert, setShowAlert] = useState(false);
  const [alertData, setAlertData] = useState({ type: "", message: "" });
  const [passwordStrength, setPasswordStrength] = useState({
    score: 0,
    text: "",
  });
  const [loading, setLoading] = useState(false);
  const [loadingProfile, setLoadingProfile] = useState(false);
  const [activeTab, setActiveTab] = useState("password");

  // Estados para el modal
  const [showModal, setShowModal] = useState(false);
  const [modalData, setModalData] = useState({
    type: "success",
    title: "",
    message: "",
  });

  // Cargar datos del usuario al montar el componente
  useEffect(() => {
    const loadUserData = async () => {
      try {
        const token = localStorage.getItem("authToken");
        if (!token) {
          console.error("No hay token disponible");
          return;
        }

        // Llamar a la API correcta para obtener los datos del usuario actual
        const response = await fetch(
          API_URL +"users/get/current",
          {
            method: "POST",
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );

        if (response.ok) {
          const userData = await response.json();
          console.log("Datos del usuario:", userData); // Para debuggear

          // Acceder correctamente a los datos dentro de entity
          if (userData.codigo === 0 && userData.entity) {
            setProfileData((prev) => ({
              ...prev,
              id: userData.entity.id,
              currentName: userData.entity.nombre || "Sin nombre",
            }));
          } else {
            console.error("Respuesta inesperada:", userData);
            setProfileData((prev) => ({
              ...prev,
              currentName: "Error en la respuesta",
            }));
          }
        } else {
          console.error("Error en la respuesta:", response.status);
          // Fallback: intentar extraer del token como segunda opción
          const payload = JSON.parse(atob(token.split(".")[1]));
          console.log("Payload del token:", payload); // Para debuggear

          setProfileData((prev) => ({
            ...prev,
            id: payload.userId || payload.id || null,
            currentName:
              payload.nombre || payload.name || payload.sub || "Usuario actual",
          }));
        }
      } catch (error) {
        console.error("Error al cargar datos del usuario:", error);

        // Fallback: intentar extraer del token
        try {
          const token = localStorage.getItem("authToken");
          if (token) {
            const payload = JSON.parse(atob(token.split(".")[1]));
            console.log("Payload del token (fallback):", payload); // Para debuggear

            setProfileData((prev) => ({
              ...prev,
              id: payload.userId || payload.id || null,
              currentName:
                payload.nombre ||
                payload.name ||
                payload.sub ||
                "Usuario actual",
            }));
          }
        } catch (tokenError) {
          console.error("Error al decodificar token:", tokenError);
          setProfileData((prev) => ({
            ...prev,
            currentName: "Error al cargar usuario",
          }));
        }
      }
    };

    loadUserData();
  }, []);

  // Función para evaluar la fuerza de la contraseña
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

  // Función para validar contraseña
  const validatePassword = (password) => {
    const minLength = password.length >= 8;
    const hasLower = /[a-z]/.test(password);
    const hasUpper = /[A-Z]/.test(password);
    const hasNumber = /\d/.test(password);
    const hasSpecial = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password);

    return minLength && hasLower && hasUpper && hasNumber && hasSpecial;
  };

  // Mostrar modal
  const showResponseModal = (type, title, message) => {
    setModalData({ type, title, message });
    setShowModal(true);
  };

  // Manejar cambios en los inputs
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));

    if (name === "newPassword") {
      const strength = checkPasswordStrength(value);
      setPasswordStrength(strength);
    }
  };

  // Manejar cambios en los inputs del perfil
  const handleProfileInputChange = (e) => {
    const { name, value } = e.target;
    setProfileData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // Mostrar alerta
  const displayAlert = (type, message) => {
    setAlertData({ type, message });
    setShowAlert(true);
    setTimeout(() => setShowAlert(false), 5000);
  };

  // Función para cambiar contraseña usando la API real
  const changePassword = async (currentPassword, newPassword) => {
    try {
      const token = localStorage.getItem("authToken");

      if (!token) {
        throw new Error("No hay token de autenticación");
      }

      const response = await fetch(
         API_URL + "users/change/password/simple",
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            password: currentPassword,
            newPassword: newPassword,
          }),
        }
      );

      const data = await response.json();

      if (response.ok && data.codigo === 0) {
        showResponseModal("success", "¡Éxito!", data.mensaje);
        resetForm();
      } else {
        showResponseModal(
          "error",
          "Error",
          data.mensaje || "Error al cambiar la contraseña"
        );
      }
    } catch (error) {
      console.error("Error al cambiar contraseña:", error);
      showResponseModal(
        "error",
        "Error de Conexión",
        "No se pudo conectar con el servidor. Intente nuevamente."
      );
    }
  };

  // Función para actualizar nombre usando la API real
  const updateName = async (userId, newName) => {
    try {
      const token = localStorage.getItem("authToken");

      if (!token) {
        throw new Error("No hay token de autenticación");
      }

      const response = await fetch(
        API_URL + "users/update/name/current",
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            id: userId,
            nombre: newName,
          }),
        }
      );

      const data = await response.json();

      if (response.ok && data.codigo === 0) {
        showResponseModal("success", "¡Éxito!", data.mensaje);
        setProfileData((prev) => ({
          ...prev,
          currentName: newName,
          newName: "",
        }));
      } else {
        showResponseModal(
          "error",
          "Error",
          data.mensaje || "Error al actualizar el nombre"
        );
      }
    } catch (error) {
      console.error("Error al actualizar nombre:", error);
      showResponseModal(
        "error",
        "Error de Conexión",
        "No se pudo conectar con el servidor. Intente nuevamente."
      );
    }
  };

  // Manejar envío del formulario de perfil
  const handleProfileSubmit = async (e) => {
    e.preventDefault();

    const { id, newName } = profileData;

    // Validaciones
    if (!newName || newName.trim().length === 0) {
      displayAlert("danger", "Por favor, ingrese un nombre válido.");
      return;
    }

    if (newName.trim().length < 2) {
      displayAlert("danger", "El nombre debe tener al menos 2 caracteres.");
      return;
    }

    if (newName.trim().length > 50) {
      displayAlert("danger", "El nombre no puede exceder 50 caracteres.");
      return;
    }

    if (profileData.currentName === newName.trim()) {
      displayAlert("danger", "El nuevo nombre debe ser diferente al actual.");
      return;
    }

    if (!id) {
      displayAlert(
        "danger",
        "No se pudo obtener el ID del usuario. Recargue la página e intente nuevamente."
      );
      return;
    }

    setLoadingProfile(true);
    await updateName(id, newName.trim());
    setLoadingProfile(false);
  };

  // Manejar envío del formulario de contraseña
  const handleSubmit = async (e) => {
    e.preventDefault();

    const { currentPassword, newPassword, confirmPassword } = formData;

    // Validaciones
    if (!currentPassword || !newPassword || !confirmPassword) {
      displayAlert("danger", "Por favor, complete todos los campos.");
      return;
    }

    if (!validatePassword(newPassword)) {
      displayAlert(
        "danger",
        "La nueva contraseña no cumple con los requisitos de seguridad."
      );
      return;
    }

    if (newPassword !== confirmPassword) {
      displayAlert("danger", "Las contraseñas no coinciden.");
      return;
    }

    if (currentPassword === newPassword) {
      displayAlert(
        "danger",
        "La nueva contraseña debe ser diferente a la actual."
      );
      return;
    }

    setLoading(true);
    await changePassword(currentPassword, newPassword);
    setLoading(false);
  };

  // Resetear formulario
  const resetForm = () => {
    setFormData({
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    });
    setPasswordStrength({ score: 0, text: "" });
  };

  // Resetear formulario de perfil
  const resetProfileForm = () => {
    setProfileData((prev) => ({
      ...prev,
      newName: "",
    }));
  };

  // Obtener clase CSS para la barra de progreso
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
    <div className="container mt-4">
      <div className="row justify-content-center">
        <div className="col-md-8 col-lg-6">
          <div className="card shadow">
            <div className="card-header bg-white py-3">
              <div className="d-flex justify-content-between align-items-center">
                <h5 className="mb-0">
                  <i className="fas fa-user-cog me-2"></i>
                  Configuración de Cuenta
                </h5>
              </div>

              {/* Tabs de navegación */}
              <ul className="nav nav-tabs mt-3 border-0">
                <li className="nav-item">
                  <button
                    className={`nav-link ${
                      activeTab === "profile" ? "active" : ""
                    }`}
                    onClick={() => setActiveTab("profile")}
                    style={{
                      border: "none",
                      background: "none",
                      color: activeTab === "profile" ? "#0d6efd" : "#000", // azul para activo, negro para inactivo
                    }}
                  >
                    <i className="fas fa-user me-2"></i>
                    Perfil
                  </button>
                </li>
                <li className="nav-item">
                  <button
                    className={`nav-link ${
                      activeTab === "password" ? "active" : ""
                    }`}
                    onClick={() => setActiveTab("password")}
                    style={{
                      border: "none",
                      background: "none",
                      color: activeTab === "password" ? "#0d6efd" : "#000",
                    }}
                  >
                    <i className="fas fa-lock me-2"></i>
                    Contraseña
                  </button>
                </li>
              </ul>
            </div>

            <div className="card-body p-4">
              {showAlert && (
                <div
                  className={`alert alert-${alertData.type} alert-dismissible fade show`}
                  role="alert"
                >
                  {alertData.message}
                  <button
                    type="button"
                    className="btn-close"
                    onClick={() => setShowAlert(false)}
                  ></button>
                </div>
              )}

              {/* Contenido del Tab de Perfil */}
              {activeTab === "profile" && (
                <div>
                  <h6 className="mb-3 text-muted">
                    Actualizar Información Personal
                  </h6>

                  {/* Campo de ID del usuario (oculto) */}
                  <div className="mb-3">
                    <label className="form-label">
                      <i className="fas fa-id-badge me-1"></i>
                      ID de Usuario
                    </label>
                    <input
                      type="text"
                      className="form-control"
                      value={profileData.id || "Cargando..."}
                      disabled
                      style={{ backgroundColor: "#f8f9fa" }}
                    />
                    <small className="text-muted">
                      Identificador único del usuario en el sistema
                    </small>
                  </div>

                  <div className="mb-3">
                    <label className="form-label">
                      <i className="fas fa-user me-1"></i>
                      Nombre Actual
                    </label>
                    <input
                      type="text"
                      className="form-control"
                      value={profileData.currentName}
                      disabled
                      placeholder="Cargando nombre actual..."
                      style={{ backgroundColor: "#f8f9fa" }}
                    />
                    <small className="text-muted">
                      Este es su nombre actual en el sistema
                    </small>
                  </div>

                  <div className="mb-4">
                    <label className="form-label">
                      <i className="fas fa-edit me-1"></i>
                      Nuevo Nombre <span className="text-danger">*</span>
                    </label>
                    <input
                      type="text"
                      name="newName"
                      className="form-control"
                      value={profileData.newName}
                      onChange={handleProfileInputChange}
                      placeholder="Ingrese su nuevo nombre"
                      maxLength={50}
                    />
                    <div className="d-flex justify-content-between mt-1">
                      <small className="text-muted">Mínimo 2 caracteres</small>
                      <small className="text-muted">
                        {profileData.newName.length}/50
                      </small>
                    </div>
                  </div>

                  <div className="d-flex gap-3 pt-3 border-top">
                    <button
                      type="button"
                      onClick={handleProfileSubmit}
                      className="btn btn-primary flex-fill"
                      disabled={
                        loadingProfile ||
                        !profileData.newName.trim() ||
                        !profileData.id
                      }
                    >
                      {loadingProfile ? (
                        <>
                          <i className="fas fa-spinner fa-spin me-2"></i>
                          Actualizando...
                        </>
                      ) : (
                        <>
                          <i className="fas fa-save me-2"></i>
                          Actualizar Nombre
                        </>
                      )}
                    </button>

                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={resetProfileForm}
                      disabled={loadingProfile}
                    >
                      <i className="fas fa-times me-2"></i>
                      Cancelar
                    </button>
                  </div>
                </div>
              )}

              {/* Contenido del Tab de Contraseña */}
              {activeTab === "password" && (
                <div>
                  <h6 className="mb-3 text-muted">
                    Cambiar Contraseña de Acceso
                  </h6>

                  <div className="mb-3">
                    <label className="form-label">
                      Contraseña Actual <span className="text-danger">*</span>
                    </label>
                    <input
                      type="password"
                      name="currentPassword"
                      className="form-control"
                      value={formData.currentPassword}
                      onChange={handleInputChange}
                      required
                      placeholder="Ingrese su contraseña actual"
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">
                      Nueva Contraseña <span className="text-danger">*</span>
                    </label>
                    <input
                      type="password"
                      name="newPassword"
                      className="form-control"
                      value={formData.newPassword}
                      onChange={handleInputChange}
                      required
                      placeholder="Ingrese su nueva contraseña"
                    />

                    {formData.newPassword && (
                      <div className="mt-2">
                        <div className="progress" style={{ height: "6px" }}>
                          <div
                            className={`progress-bar ${getProgressClass(
                              passwordStrength.score
                            )}`}
                            role="progressbar"
                            style={{ width: `${passwordStrength.score * 20}%` }}
                          ></div>
                        </div>
                        <small className="text-muted mt-1 d-block">
                          Fortaleza: {passwordStrength.text}
                        </small>
                      </div>
                    )}
                  </div>

                  <div className="mb-4">
                    <label className="form-label">
                      Confirmar Nueva Contraseña{" "}
                      <span className="text-danger">*</span>
                    </label>
                    <input
                      type="password"
                      name="confirmPassword"
                      className={`form-control ${
                        formData.confirmPassword &&
                        formData.newPassword !== formData.confirmPassword
                          ? "is-invalid"
                          : ""
                      }`}
                      value={formData.confirmPassword}
                      onChange={handleInputChange}
                      required
                      placeholder="Confirme su nueva contraseña"
                    />
                    {formData.confirmPassword &&
                      formData.newPassword !== formData.confirmPassword && (
                        <div className="invalid-feedback">
                          Las contraseñas no coinciden.
                        </div>
                      )}
                  </div>

                  <div className="card bg-light mb-4">
                    <div className="card-body py-3">
                      <h6 className="card-title mb-2">
                        Requisitos de la contraseña:
                      </h6>
                      <ul className="list-unstyled mb-0">
                        <li className="py-1">
                          <small className="text-muted">
                            <i className="fas fa-check-circle me-2"></i>
                            Mínimo 8 caracteres
                          </small>
                        </li>
                        <li className="py-1">
                          <small className="text-muted">
                            <i className="fas fa-check-circle me-2"></i>
                            Al menos una letra mayúscula
                          </small>
                        </li>
                        <li className="py-1">
                          <small className="text-muted">
                            <i className="fas fa-check-circle me-2"></i>
                            Al menos una letra minúscula
                          </small>
                        </li>
                        <li className="py-1">
                          <small className="text-muted">
                            <i className="fas fa-check-circle me-2"></i>
                            Al menos un número
                          </small>
                        </li>
                        <li className="py-1">
                          <small className="text-muted">
                            <i className="fas fa-check-circle me-2"></i>
                            Al menos un carácter especial (!@#$%^&*)
                          </small>
                        </li>
                      </ul>
                    </div>
                  </div>

                  <div className="d-flex gap-3 pt-3 border-top">
                    <button
                      type="button"
                      onClick={handleSubmit}
                      className="btn btn-primary flex-fill"
                      disabled={loading}
                    >
                      {loading ? (
                        <>
                          <i className="fas fa-spinner fa-spin me-2"></i>
                          Cambiando...
                        </>
                      ) : (
                        <>
                          <i className="fas fa-key me-2"></i>
                          Cambiar Contraseña
                        </>
                      )}
                    </button>

                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={resetForm}
                      disabled={loading}
                    >
                      <i className="fas fa-times me-2"></i>
                      Cancelar
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Modal de Respuesta */}
      <div
        className={`modal fade ${showModal ? "show" : ""}`}
        style={{ display: showModal ? "block" : "none" }}
        tabIndex="-1"
        role="dialog"
      >
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content">
            <div
              className={`modal-header ${
                modalData.type === "success" ? "bg-success" : "bg-danger"
              } text-white`}
            >
              <h5 className="modal-title">
                <i
                  className={`fas ${
                    modalData.type === "success"
                      ? "fa-check-circle"
                      : "fa-exclamation-triangle"
                  } me-2`}
                ></i>
                {modalData.title}
              </h5>
              <button
                type="button"
                className="btn-close btn-close-white"
                onClick={() => setShowModal(false)}
              ></button>
            </div>
            <div className="modal-body">
              <div className="text-center py-3">
                <i
                  className={`fas ${
                    modalData.type === "success"
                      ? "fa-check-circle"
                      : "fa-times-circle"
                  } fs-1 ${
                    modalData.type === "success"
                      ? "text-success"
                      : "text-danger"
                  } mb-3`}
                ></i>
                <h6 className="mb-3">{modalData.message}</h6>
              </div>
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className={`btn ${
                  modalData.type === "success" ? "btn-success" : "btn-danger"
                }`}
                onClick={() => setShowModal(false)}
              >
                <i className="fas fa-check me-1"></i>
                Aceptar
              </button>
            </div>
          </div>
        </div>
      </div>
      {showModal && <div className="modal-backdrop fade show"></div>}
    </div>
  );
};

export default Configuraciones;
