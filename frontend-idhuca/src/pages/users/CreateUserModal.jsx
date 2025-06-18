import { useState, useEffect } from "react";

const CreateUserModal = ({ show, onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    nombre: "",
    email: "",
    rol: {
      codigo: "ROL_1",
    },
  });

  const roles = [
    { codigo: "ROL_1", descripcion: "Admin" },
    { codigo: "ROL_2", descripcion: "User" },
    { codigo: "ROL_3", descripcion: "Ayudante" },
  ];

  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [showCloseButton, setShowCloseButton] = useState(true);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccessMessage("");
    setIsLoading(true);
    setShowCloseButton(false);

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

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.mensaje || "Error al crear el usuario");
      }

      setSuccessMessage(data.mensaje);
      setShowCloseButton(true);

      // Call onSuccess to refresh the users list
      onSuccess();
  
    } catch (err) {
      setError(err.message);
      setShowCloseButton(true);
    } finally {
      setIsLoading(false);
    }
  };

  const handleClose = () => {
    setSuccessMessage("");
    setError(null);
    setFormData({
      nombre: "",
      email: "",
      rol: { codigo: "ROL_1" },
    });
    setIsLoading(false);
    setShowCloseButton(true);
    onClose();
  };

  // Reset states when modal is shown/hidden
  useEffect(() => {
    if (show) {
      // Reset all states when modal opens
      setSuccessMessage("");
      setError(null);
      setIsLoading(false);
      setShowCloseButton(true);
      setFormData({
        nombre: "",
        email: "",
        rol: { codigo: "ROL_1" },
      });
    }
  }, [show]);

  if (!show) return null;

  return (
    <>
      <div className="modal fade show" style={{ display: "block" }} tabIndex="-1">
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">
                {successMessage ? "Usuario Creado" : "Crear Nuevo Usuario"}
              </h5>
              {showCloseButton && (
                <button type="button" className="btn-close" onClick={handleClose}></button>
              )}
            </div>
            <div className="modal-body">
              {error && <div className="alert alert-danger">{error}</div>}
              {successMessage ? (
                <div className="alert alert-success">
                  <p className="mb-0">{successMessage}</p>
                  <p className="mb-0 mt-2">
                    <small>
                      <strong>Nota:</strong> Por favor, guarde esta contraseña provisional
                      antes de cerrar esta ventana.
                    </small>
                  </p>
                </div>
              ) : (
                <form onSubmit={handleSubmit}>
                  <div className="mb-3">
                    <label className="form-label">Rol</label>
                    <select
                      className="form-select"
                      value={formData.rol.codigo}
                      onChange={(e) => {
                        setFormData({
                          ...formData,
                          rol: {
                            codigo: e.target.value,
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
                  <div className="modal-footer">
                    <button
                      type="button"
                      className="btn btn-secondary"
                      onClick={handleClose}
                    >
                      Cerrar
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
              )}
            </div>
            {successMessage && (
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-primary"
                  onClick={handleClose}
                >
                  Entendido
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
      <div className="modal-backdrop fade show"></div>
    </>
  );
};

export default CreateUserModal;