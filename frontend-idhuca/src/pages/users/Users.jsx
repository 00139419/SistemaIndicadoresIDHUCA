import { useState, useEffect } from "react";
import CreateUserModal from "./CreateUserModal";
import DeleteUserModal from "./DeleteUserModal";
import UpdateUserModal from "./UpdateUserModal";
import ViewUserModal from "./ViewUserModal";
import ChangePasswordModal from "./ChangePasswordModal";

const GestionUsuarios = () => {

const API_URL = process.env.REACT_APP_API_URL;
  const API_BACKUP_URL = process.env.REACT_APP_API_BACKUP;


  const [usuarios, setUsuarios] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [userToDelete, setUserToDelete] = useState(null);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [userToUpdate, setUserToUpdate] = useState(null);
  const [showViewModal, setShowViewModal] = useState(false);
  const [userIdToView, setUserIdToView] = useState(null);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [userToChangePassword, setUserToChangePassword] = useState(null);

  useEffect(() => {
    fetchUsuarios();
  }, []);

  const getRoleDisplay = (rol) => {
    if (!rol) return "N/A";
    if (typeof rol === "object") {
      return rol.descripcion || rol.codigo || "N/A";
    }
    return rol;
  };

  const fetchUsuarios = async () => {
    try {
      setIsLoading(true);
      setError(null);

      const token = localStorage.getItem("authToken");

      if (!token) {
        throw new Error(
          "No se encontró el token de autenticación. Por favor inicie sesión nuevamente."
        );
      }

      const response = await fetch(
        API_URL + "users/get/all",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error(`Error en la petición: ${response.status}`);
      }

      const data = await response.json();

      // Ensure we're working with an array
      const usuariosArray = Array.isArray(data.entity) ? data.entity : [];
      setUsuarios(usuariosArray);
    } catch (err) {
      console.error("Error fetching users:", err);
      setError(err.message);
      setUsuarios([]); // Set empty array on error
    } finally {
      setIsLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    const date = new Date(dateString);
    return date.toLocaleDateString("es-ES", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const isUserActive = (usuario) => {
    // Considera booleanos, strings y números
    const val = usuario.activo ?? usuario.active;
    if (typeof val === "boolean") return val;
    if (typeof val === "number") return val === 1;
    if (typeof val === "string") return val === "1" || val.toLowerCase() === "true";
    return false;
  };

  return (
    <div className="d-flex flex-column" style={{ height: 'calc(100vh - 160px)' }}>
      {/* Header fijo */}
      <div className="px-4 py-3 border-bottom bg-white">
        <h1 className="mb-0 fs-2 fw-bold text-center">
          <i className="bi bi-people-fill me-2 text-primary"></i>
          Gestión de Usuarios
        </h1>
      </div>

      {/* Contenido con scroll */}
      <div className="flex-grow-1 px-4 py-3" style={{ overflowY: 'auto', height: '100%' }}>

        {/* Botón de acción */}
        <div className="mb-3">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <button
              className="btn btn-primary"
              onClick={() => setShowCreateModal(true)}
            >
              <i className="bi bi-person-plus-fill me-2"></i>
              Nuevo Usuario
            </button>

            <button
              className="btn btn-outline-primary btn-sm"
              onClick={fetchUsuarios}
              disabled={isLoading}
            >
              <i className="bi bi-arrow-clockwise me-1"></i>
              Actualizar
            </button>
          </div>

          {/* Información de registros */}
          <div className="mb-3 small text-muted">
            <i className="bi bi-info-circle me-1"></i>
            {usuarios.length > 0 ?
              `Total de usuarios: ${usuarios.length}` :
              'No hay usuarios para mostrar'}
          </div>
        </div>

        {isLoading ? (
          <div className="d-flex justify-content-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Cargando...</span>
            </div>
          </div>
        ) : error ? (
          <div className="alert alert-danger">
            <i className="bi bi-exclamation-triangle me-2"></i>
            <strong>Error:</strong> {error}
            <button
              className="btn btn-sm btn-outline-danger ms-2"
              onClick={fetchUsuarios}
            >
              <i className="bi bi-arrow-clockwise me-1"></i>
              Reintentar
            </button>
          </div>
        ) : (
          <>
            {/* Tabla de usuarios */}
            <div className="table-responsive">
              <table className="table table-bordered table-hover mb-0">
                <thead className="table-dark">
                  <tr>
                    <th style={{ width: '60px', minWidth: '60px' }}>
                      <i className="bi bi-hash me-1"></i>ID
                    </th>
                    <th style={{ minWidth: '150px' }}>
                      <i className="bi bi-person me-1"></i>Nombre
                    </th>
                    <th style={{ minWidth: '200px' }}>
                      <i className="bi bi-envelope me-1"></i>Email
                    </th>
                    <th style={{ width: '120px', minWidth: '120px' }}>
                      <i className="bi bi-tags me-1"></i>Rol
                    </th>
                    <th style={{ width: '100px', minWidth: '100px' }}>
                      <i className="bi bi-toggle-on me-1"></i>Estado
                    </th>
                    <th style={{ width: '140px', minWidth: '140px' }}>
                      <i className="bi bi-calendar-plus me-1"></i>Fecha Creación
                    </th>
                    <th style={{ width: '240px', minWidth: '240px' }}>
                      <i className="bi bi-gear me-1"></i>Acciones
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {usuarios.length > 0 ? (
                    usuarios.map((usuario, index) => (
                      <tr key={usuario.id || index}>
                        <td>
                          <span className="badge bg-light text-dark">
                            {usuario.id}
                          </span>
                        </td>
                        <td>
                          <div className="d-flex align-items-center">
                            <div className="avatar-circle me-2">
                              <i className="bi bi-person-circle fs-4 text-secondary"></i>
                            </div>
                            <div className="small">
                              <strong>
                                {usuario.nombre ||
                                  `${usuario.firstName} ${usuario.lastName}`.trim()}
                              </strong>
                            </div>
                          </div>
                        </td>
                        <td>
                          <div className="d-flex align-items-center">
                            <i className="bi bi-envelope me-2 text-muted"></i>
                            <span className="small">{usuario.email}</span>
                          </div>
                        </td>
                        <td>
                          <span className="badge bg-secondary">
                            <i className="bi bi-tags me-1"></i>
                            {getRoleDisplay(usuario.rol)}
                          </span>
                        </td>
                        <td>
                          <span
                            className={`badge ${isUserActive(usuario) ? "bg-success" : "bg-danger"}`}
                          >
                            <i className={`bi ${isUserActive(usuario) ? "bi-check-circle" : "bi-x-circle"} me-1`}></i>
                            {isUserActive(usuario) ? "Activo" : "Inactivo"}
                          </span>
                        </td>
                        <td className="small">
                          <i className="bi bi-calendar me-1 text-muted"></i>
                          {formatDate(usuario.creadoEn)}
                        </td>
                        <td>
                          <div className="btn-group" role="group">
                            {/* Ver detalles */}
                            <button
                              className="btn btn-sm btn-outline-info"
                              title="Ver detalles del usuario"
                              onClick={() => {
                                setUserIdToView(usuario.id);
                                setShowViewModal(true);
                              }}
                            >
                              <i className="bi bi-eye"></i>
                            </button>
                            
                            {/* Cambiar contraseña */}
                            <button
                              className="btn btn-sm btn-outline-warning"
                              title="Cambiar contraseña"
                              onClick={() => {
                                 setUserToChangePassword(usuario);
                                 setShowPasswordModal(true);
                              }}
                            >
                              <i className="bi bi-key"></i>
                            </button>
                            
                            {/* Editar usuario */}
                            <button
                              className="btn btn-sm btn-outline-primary"
                              title="Editar usuario"
                              onClick={() => {
                                setUserToUpdate(usuario);
                                setShowUpdateModal(true);
                              }}
                            >
                              <i className="bi bi-pencil-square"></i>
                            </button>
                            
                            {/* Eliminar usuario */}
                            <button
                              className="btn btn-sm btn-outline-danger"
                              title="Eliminar usuario"
                              onClick={() => {
                                setUserToDelete(usuario);
                                setShowDeleteModal(true);
                              }}
                            >
                              <i className="bi bi-trash"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="7" className="text-center py-4 text-muted">
                        <i className="bi bi-people fs-1 mb-3 d-block text-muted"></i>
                        No hay usuarios disponibles
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </>
        )}
      </div>

      {/* Modales */}
      <CreateUserModal
        show={showCreateModal}
        onClose={() => {
          setShowCreateModal(false);
        }}
        onSuccess={() => {
          fetchUsuarios();
        }}
      />

      <ViewUserModal
        show={showViewModal}
        onClose={() => {
          setShowViewModal(false);
          setUserIdToView(null);
        }}
        userId={userIdToView}
      />

      <ChangePasswordModal
        show={showPasswordModal}
        onClose={() => {
          setShowPasswordModal(false);
          setUserToChangePassword(null);
        }}
        onSuccess={() => {
          fetchUsuarios();
          setShowPasswordModal(false);
          setUserToChangePassword(null);
        }}
        user={userToChangePassword}
      />

      <UpdateUserModal
        show={showUpdateModal}
        onClose={() => {
          setShowUpdateModal(false);
          setUserToUpdate(null);
        }}
        onSuccess={() => {
          fetchUsuarios();
          setShowUpdateModal(false);
          setUserToUpdate(null);
        }}
        user={userToUpdate}
      />

      <DeleteUserModal
        show={showDeleteModal}
        onClose={() => {
          setShowDeleteModal(false);
          setUserToDelete(null);
        }}
        onSuccess={() => {
          fetchUsuarios();
          setShowDeleteModal(false);
          setUserToDelete(null);
        }}
        user={userToDelete}
      />
    </div>
  );
};

export default GestionUsuarios;