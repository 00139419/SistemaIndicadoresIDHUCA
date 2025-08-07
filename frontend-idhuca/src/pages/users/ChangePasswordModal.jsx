import { useState, useEffect } from "react";

const ChangePasswordModal = ({ show, onClose, onSuccess, user }) => {
  const API_URL = process.env.REACT_APP_API_URL;
  const API_BACKUP_URL = process.env.REACT_APP_API_BACKUP;
 
  const [provisionalPassword, setProvisionalPassword] = useState("");
  const [isChanging, setIsChanging] = useState(false);
  const [error, setError] = useState(null);

  // Limpia el estado cada vez que se abre la modal o cambia el usuario
  useEffect(() => {
    if (show) {
      setProvisionalPassword("");
      setError(null);
      setIsChanging(false);
    }
  }, [show, user]);

  const handleUnlock = async (e) => {
    e.preventDefault();
    setIsChanging(true);
    setError(null);
    setProvisionalPassword("");

    try {
      const token = localStorage.getItem("authToken");
      const response = await fetch(
        API_URL + "users/unlock",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            id: user.id,
          }),
        }
      );

      const data = await response.json();
      console.log("Respuesta de unlock:", data);

      if (!response.ok || data.codigo !== 0) {
        throw new Error(data.mensaje || "Error al generar la contraseña provisional");
      }

      // Extraer la contraseña provisional del mensaje
      let provisional = "Contraseña generada no disponible";
      const match = data.mensaje.match(/Contraseña provisional:\s*([^\s]+)/i);
      if (match) {
        provisional = match[1];
      }

      setProvisionalPassword(provisional);
      //onSuccess && onSuccess();
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
              <h5 className="modal-title">Generar Contraseña Provisional</h5>
              <button
                type="button"
                className="btn-close"
                onClick={onClose}
                disabled={isChanging}
              ></button>
            </div>
            <form onSubmit={handleUnlock}>
              <div className="modal-body">
                {error && <div className="alert alert-danger">{error}</div>}
                {provisionalPassword ? (
                  <div className="alert alert-success">
                    <strong>¡Contraseña provisional generada!</strong>
                    <br />
                    La nueva contraseña provisional es: <br />
                    <span style={{ fontWeight: "bold", fontSize: "1.2em" }}>{provisionalPassword}</span>
                  </div>
                ) : (
                  <div className="mb-3">
                    <p>
                      Al continuar, se generará una nueva contraseña provisional para el usuario seleccionado.
                    </p>
                  </div>
                )}
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => {
                    onClose();
                    onSuccess && onSuccess();
                  }}
                  disabled={isChanging}
                >
                  Cerrar
                </button>
                {!provisionalPassword && (
                  <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={isChanging}
                  >
                    {isChanging ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2"></span>
                        Generando...
                      </>
                    ) : (
                      "Generar Contraseña"
                    )}
                  </button>
                )}
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