import { useState } from 'react';

const DeleteUserModal = ({ show, onClose, onSuccess, user }) => {
  const [isDeleting, setIsDeleting] = useState(false);
  const [error, setError] = useState(null);

  const handleDelete = async () => {
    try {
      setIsDeleting(true);
      setError(null);
      
      const token = localStorage.getItem('authToken');
      
      if (!token) {
        throw new Error('No se encontró el token de autenticación');
      }

      const response = await fetch(
        `http://localhost:8080/idhuca-indicadores/api/srv/users/delete/${user.id}`,
        {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      );

      if (!response.ok) {
        throw new Error('Error al eliminar el usuario');
      }

      onSuccess();
      onClose();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsDeleting(false);
    }
  };

  if (!show) return null;

  return (
    <>
      <div className="modal fade show" style={{ display: 'block' }} tabIndex="-1">
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">Confirmar Eliminación</h5>
              <button 
                type="button" 
                className="btn-close" 
                onClick={onClose}
                disabled={isDeleting}
              ></button>
            </div>
            <div className="modal-body">
              {error && (
                <div className="alert alert-danger">
                  {error}
                </div>
              )}
              <p>
                ¿Está seguro que desea eliminar al usuario <strong>{user?.nombre}</strong>?
              </p>
              <p className="text-danger">
                <i className="bi bi-exclamation-triangle-fill me-2"></i>
                Esta acción no se puede deshacer.
              </p>
            </div>
            <div className="modal-footer">
              <button 
                type="button" 
                className="btn btn-secondary" 
                onClick={onClose}
                disabled={isDeleting}
              >
                Cancelar
              </button>
              <button 
                type="button" 
                className="btn btn-danger" 
                onClick={handleDelete}
                disabled={isDeleting}
              >
                {isDeleting ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2"></span>
                    Eliminando...
                  </>
                ) : (
                  'Eliminar Usuario'
                )}
              </button>
            </div>
          </div>
        </div>
      </div>
      <div className="modal-backdrop fade show"></div>
    </>
  );
};

export default DeleteUserModal;