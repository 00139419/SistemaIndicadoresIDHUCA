import { useState } from "react";

const ChangePasswordModal = ({ show, onClose, onSuccess, user }) => {
  const [password, setPassword] = useState("");
  const [isChanging, setIsChanging] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsChanging(true);
    setError(null);

    try {
      const token = localStorage.getItem("authToken");
      const response = await fetch(
        // Change to the correct endpoint for password updates
        "http://localhost:8080/idhuca-indicadores/api/srv/users/update",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            id: user.id,
            nuevaContrasena: password, // Change field name to match API expectation
          }),
        }
      );

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.mensaje || "Error al cambiar la contraseña");
      }

      onSuccess();
      setPassword("");
      onClose(); // Add this to close modal on success
    } catch (err) {
      setError(err.message);
    } finally {
      setIsChanging(false);
    }
  };

  if (!show) return null;

  return (
    <>
      <div className="modal fade show" style={{ display: "block" }} tabIndex="-1">
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">Cambiar Contraseña</h5>
              <button
                type="button"
                className="btn-close"
                onClick={onClose}
                disabled={isChanging}
              ></button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="modal-body">
                {error && <div className="alert alert-danger">{error}</div>}
                <div className="mb-3">
                  <label className="form-label">Nueva Contraseña</label>
                  <input
                    type="password"
                    className="form-control"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={onClose}
                  disabled={isChanging}
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={isChanging}
                >
                  {isChanging ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-2"></span>
                      Cambiando...
                    </>
                  ) : (
                    "Cambiar Contraseña"
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

export default ChangePasswordModal;