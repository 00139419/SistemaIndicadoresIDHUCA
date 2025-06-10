import { useState, useEffect } from 'react';

const ViewUserModal = ({ show, onClose, userId }) => {
  const [userData, setUserData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchUserDetails = async () => {
      if (!show || !userId) return;
      
      setIsLoading(true);
      setError(null);
      
      try {
        const token = localStorage.getItem('authToken');
        const response = await fetch(
          `http://localhost:8080/idhuca-indicadores/api/srv/users/get/one/${userId}`,
          {
            method: 'POST',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
            }
          }
        );

        if (!response.ok) {
          throw new Error('Error al obtener los detalles del usuario');
        }

        const data = await response.json();
        setUserData(data.entity);
      } catch (err) {
        setError(err.message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserDetails();
  }, [show, userId]);

  if (!show) return null;

  return (
    <>
      <div className="modal fade show" style={{ display: 'block' }} tabIndex="-1">
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">Detalles del Usuario</h5>
              <button type="button" className="btn-close" onClick={onClose}></button>
            </div>
            <div className="modal-body">
              {isLoading ? (
                <div className="text-center py-4">
                  <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Cargando...</span>
                  </div>
                </div>
              ) : error ? (
                <div className="alert alert-danger">{error}</div>
              ) : userData ? (
                <div className="user-details">
                  <div className="mb-4">
                    <h6 className="text-muted mb-2">Información General</h6>
                    <div className="card">
                      <div className="card-body">
                        <dl className="row mb-0">
                          <dt className="col-sm-4">ID</dt>
                          <dd className="col-sm-8">{userData.id}</dd>
                          
                          <dt className="col-sm-4">Nombre</dt>
                          <dd className="col-sm-8">{userData.nombre}</dd>
                          
                          <dt className="col-sm-4">Email</dt>
                          <dd className="col-sm-8">{userData.email}</dd>
                          
                          <dt className="col-sm-4">Rol</dt>
                          <dd className="col-sm-8">
                            <span className="badge bg-secondary">
                              {userData.rol?.descripcion || 'N/A'}
                            </span>
                          </dd>
                          
                          <dt className="col-sm-4">Estado</dt>
                          <dd className="col-sm-8">
                            <span className={`badge ${userData.activo ? 'bg-success' : 'bg-danger'}`}>
                              {userData.activo ? 'Activo' : 'Inactivo'}
                            </span>
                          </dd>
                          
                          <dt className="col-sm-4">Creado</dt>
                          <dd className="col-sm-8">
                            {new Date(userData.creadoEn).toLocaleString()}
                          </dd>
                        </dl>
                      </div>
                    </div>
                  </div>
                </div>
              ) : (
                <div className="alert alert-warning">
                  No se encontraron datos del usuario
                </div>
              )}
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-secondary" onClick={onClose}>
                Cerrar
              </button>
            </div>
          </div>
        </div>
      </div>
      <div className="modal-backdrop fade show"></div>
    </>
  );
};

export default ViewUserModal;