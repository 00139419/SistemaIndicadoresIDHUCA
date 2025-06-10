import { useState, useEffect } from "react";

const UpdateUserModal = ({ show, onClose, onSuccess, user }) => {
  const [formData, setFormData] = useState({
    id: "",
    nombre: "",
    email: "",
    password: "",
    rol: {
      codigo: "",
      descripcion: "",
    },
    securityQuestion: {
      codigo: "",
      descripcion: "",
    },
    securityAnswer: "",
    debloquearUsuario: false,
  });

  const [isUpdating, setIsUpdating] = useState(false);
  const [error, setError] = useState(null);
  const [securityQuestions, setSecurityQuestions] = useState([]);

  const roles = [
    { codigo: "ROL_1", descripcion: "Admin" },
    { codigo: "ROL_2", descripcion: "User" },
    { codigo: "ROL_3", descripcion: "Ayudante" },
  ];

  useEffect(() => {
    if (user) {
      setFormData({
        id: user.id,
        nombre: user.nombre,
        email: user.email,
        password: "",
        rol: user.rol,
        securityQuestion: user.securityQuestion || {
          codigo: "SQ_1",
          descripcion: "¿Cuál es el nombre de tu primera mascota?",
        },
        securityAnswer: "",
        debloquearUsuario: !user.activo,
      });
    }
  }, [user]);

  useEffect(() => {
    const fetchSecurityQuestions = async () => {
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
        const data = await response.json();
        setSecurityQuestions(data.entity || []);
      } catch (err) {
        console.error("Error fetching security questions:", err);
      }
    };

    if (show) {
      fetchSecurityQuestions();
    }
  }, [show]);

  const handleUpdate = async (e) => {
    e.preventDefault();
    setIsUpdating(true);
    setError(null);

    try {
      const token = localStorage.getItem("authToken");
      if (formData.debloquearUsuario) {
        const unlockResponse = await fetch(
          "http://localhost:8080/idhuca-indicadores/api/srv/users/unlock",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({ id: formData.id }),
          }
        );

        if (!unlockResponse.ok) {
          throw new Error("Error al desbloquear el usuario");
        }
      }
       
      const updateResponse = await fetch(
        "http://localhost:8080/idhuca-indicadores/api/srv/users/update",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            ...formData,
            debloquearUsuario: undefined,
          }),
        }
      );

      if (!updateResponse.ok) {
        throw new Error("Error al actualizar el usuario");
      }

      onSuccess();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsUpdating(false);
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
              <h5 className="modal-title">Actualizar Usuario</h5>
              <button
                type="button"
                className="btn-close"
                onClick={onClose}
                disabled={isUpdating}
              ></button>
            </div>
            <form onSubmit={handleUpdate}>
              <div className="modal-body">
                {error && <div className="alert alert-danger">{error}</div>}
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
                  <label className="form-label">Rol</label>
                  <select
                    className="form-select"
                    value={formData.rol.codigo}
                    onChange={(e) => {
                      const selectedRole = roles.find(
                        (r) => r.codigo === e.target.value
                      );
                      setFormData({
                        ...formData,
                        rol: selectedRole,
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
                  <div className="form-check">
                    <input
                      type="checkbox"
                      className="form-check-input"
                      checked={formData.debloquearUsuario}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          debloquearUsuario: e.target.checked,
                        })
                      }
                      id="debloquearUsuario"
                    />
                    <label
                      className="form-check-label"
                      htmlFor="debloquearUsuario"
                    >
                      Desbloquear Usuario
                    </label>
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={onClose}
                  disabled={isUpdating}
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={isUpdating}
                >
                  {isUpdating ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-2"></span>
                      Actualizando...
                    </>
                  ) : (
                    "Actualizar Usuario"
                  )}
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

export default UpdateUserModal;
