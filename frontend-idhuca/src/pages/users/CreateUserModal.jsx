import { useState, useEffect } from "react";

const CreateUserModal = ({ show, onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    nombre: "",
    email: "",
    password: "",
    rol: {
      codigo: "ROL_1",
      descripcion: "user",
    },
    securityQuestion: {
      codigo: "SQ_1",
      descripcion: "¿Cuál es el nombre de tu primera mascota?",
    },
    securityAnswer: "",
  });

  const roles = [
    { codigo: "ROL_1", descripcion: "Admin" },
    { codigo: "ROL_2", descripcion: "User" },
    { codigo: "ROL_3", descripcion: "Ayudante" },
  ];

  const [securityQuestions, setSecurityQuestions] = useState([]);
  const [isLoadingQuestions, setIsLoadingQuestions] = useState(false);

  useEffect(() => {
    const fetchSecurityQuestions = async () => {
      setIsLoadingQuestions(true);
      try {
        const token = localStorage.getItem("authToken");
        const response = await fetch(
          "http://localhost:8080/idhuca-indicadores/api/srv/catalogo/get",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
              securityQuestions: true,
              
            }),
          }
        );

        if (!response.ok) {
          throw new Error("Error al cargar las preguntas de seguridad");
        }

        const data = await response.json();
        setSecurityQuestions(data.entity || []);
      } catch (err) {
        console.error("Error:", err);
        setError("Error al cargar las preguntas de seguridad");
      } finally {
        setIsLoadingQuestions(false);
      }
    };

    if (show) {
      fetchSecurityQuestions();
    }
  }, [show]);

  const [error, setError] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      const token = localStorage.getItem("authToken");

      const response = await fetch(
        "http://localhost:8080/idhuca-indicadores/api/srv/users/add",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(formData),
        }
      );

      if (!response.ok) {
        throw new Error("Error al crear el usuario");
      }

      onSuccess();
      onClose();
      setFormData({
        nombre: "",
        email: "",
        password: "",
        rol: { codigo: "ROL_1", descripcion: "user" },
        securityQuestion: {
          codigo: "SQ_1",
          descripcion: "¿Cuál es el nombre de tu primera mascota?",
        },
        securityAnswer: "",
      });
    } catch (err) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  if (!show) return null;

  return (
    <>
      <div
        className="modal fade show"
        style={{ display: "block" }}
        tabIndex="-1"
      >
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">Crear Nuevo Usuario</h5>
              <button
                type="button"
                className="btn-close"
                onClick={onClose}
              ></button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                {error && <div className="alert alert-danger">{error}</div>}
                <div className="mb-3">
                  <label className="form-label">Rol</label>
                  <select
                    className="form-select"
                    value={formData.rol.codigo}
                    onChange={(e) => {
                      const selectedRole = roles.find(
                        (role) => role.codigo === e.target.value
                      );
                      setFormData({
                        ...formData,
                        rol: {
                          codigo: selectedRole.codigo,
                          descripcion: selectedRole.descripcion,
                        },
                      });
                    }}
                    required
                  >
                    {roles.map((role) => (
                      <option key={role.codigo} value={role.codigo}>
                        {role.descripcion}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="mb-3">
                  <label className="form-label">Nombre</label>
                  <input
                    type="text"
                    className="form-control"
                    value={formData.nombre}
                    onChange={(e) =>
                      setFormData({ ...formData, nombre: e.target.value })
                    }
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Email</label>
                  <input
                    type="email"
                    className="form-control"
                    value={formData.email}
                    onChange={(e) =>
                      setFormData({ ...formData, email: e.target.value })
                    }
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Contraseña</label>
                  <input
                    type="password"
                    className="form-control"
                    value={formData.password}
                    onChange={(e) =>
                      setFormData({ ...formData, password: e.target.value })
                    }
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Pregunta de Seguridad</label>
                  <select
                    className="form-select"
                    value={formData.securityQuestion.codigo}
                    onChange={(e) => {
                      const selectedQuestion = securityQuestions.find(
                        (q) => q.codigo === e.target.value
                      );
                      setFormData({
                        ...formData,
                        securityQuestion: {
                          codigo: selectedQuestion.codigo,
                          descripcion: selectedQuestion.descripcion,
                        },
                      });
                    }}
                    required
                    disabled={isLoadingQuestions}
                  >
                    {isLoadingQuestions ? (
                      <option>Cargando preguntas...</option>
                    ) : (
                      securityQuestions.map((question) => (
                        <option key={question.codigo} value={question.codigo}>
                          {question.descripcion}
                        </option>
                      ))
                    )}
                  </select>
                </div>
                <div className="mb-3">
                  <label className="form-label">Respuesta de Seguridad</label>
                  <input
                    type="password"
                    className="form-control"
                    value={formData.securityAnswer}
                    onChange={(e) =>
                      setFormData({
                        ...formData,
                        securityAnswer: e.target.value,
                      })
                    }
                    required
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={onClose}
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={isLoading}
                >
                  {isLoading ? "Creando..." : "Crear Usuario"}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
      <div className="modal-backdrop fade show"></div>
    </>
  );
};

export default CreateUserModal;
