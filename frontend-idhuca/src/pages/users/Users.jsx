import { useState, useEffect } from "react";
import CreateUserModal from "./CreateUserModal";
import DeleteUserModal from "./DeleteUserModal";
import UpdateUserModal from "./UpdateUserModal";
import ViewUserModal from "./ViewUserModal";
import ChangePasswordModal from "./ChangePasswordModal";

const GestionUsuarios = () => {
  const [usuarios, setUsuarios] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [userToDelete, setUserToDelete] = useState(null);
  const [showUpdateModal, setShowUpdateModal] = useState(false);
  const [userToUpdate, setUserToUpdate] = useState(null);
  const [showViewModal, setShowViewModal] = useState(false);
  const [userIdToView, setUserIdToView] = useState(null);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [userToChangePassword, setUserToChangePassword] = useState(null);

  const usersPerPage = 15;

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

  // Modify the fetchUsuarios function
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
        "http://localhost:8080/idhuca-indicadores/api/srv/users/get/all",
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
      setTotalPages(Math.ceil(usuariosArray.length / usersPerPage));
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

  const getCurrentPageUsers = () => {
    const startIndex = (currentPage - 1) * usersPerPage;
    const endIndex = startIndex + usersPerPage;
    return usuarios.slice(startIndex, endIndex);
  };

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const renderPagination = () => {
    if (totalPages <= 1) return null;

    return (
      <div className="d-flex justify-content-between align-items-center mt-3">
        <div className="text-muted">
          Mostrar {Math.min(usersPerPage, usuarios.length)} de {usuarios.length}
        </div>
        <div>
          <span className="me-2">
            Página {currentPage} de {totalPages}
          </span>
          <div className="btn-group">
            <button
              className="btn btn-outline-secondary btn-sm"
              onClick={() => handlePageChange(currentPage - 1)}
              disabled={currentPage === 1}
            >
              ‹
            </button>
            <button
              className="btn btn-outline-secondary btn-sm"
              onClick={() => handlePageChange(currentPage + 1)}
              disabled={currentPage === totalPages}
            >
              ›
            </button>
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="container-fluid px-4 py-3">
      {/* Header */}
      <div className="row mb-4">
        <div className="col-12">
          <h2
            className="text-center mb-4"
            style={{ fontSize: "2.5rem", fontWeight: "bold" }}
          >
            Usuarios
          </h2>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="row mb-3">
        <div className="col-12">
          <div className="d-flex gap-2 flex-wrap">
            <button
              className="btn btn-primary"
              onClick={() => setShowCreateModal(true)}
            >
              <i className="bi bi-plus-circle me-2"></i>
              Crear Usuario
            </button>

            <CreateUserModal
              show={showCreateModal}
              onClose={() => setShowCreateModal(false)}
              onSuccess={() => {
                fetchUsuarios();
                setShowCreateModal(false);
              }}
            />
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="row">
        <div className="col-12">
          {isLoading ? (
            <div className="d-flex justify-content-center py-5">
              <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Cargando...</span>
              </div>
            </div>
          ) : error ? (
            <div className="alert alert-danger">
              <strong>Error:</strong> {error}
            </div>
          ) : (
            <>
              {/* Table */}
              <div className="table-responsive">
                <table className="table table-hover mb-0">
                  <thead style={{ backgroundColor: "#2c3e50", color: "white" }}>
                    <tr>
                      <th>ID</th>
                      <th>Nombre</th>
                      <th>Email</th>
                      <th>Rol</th>
                      <th>Estado</th>
                      <th>Fecha Creación</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {getCurrentPageUsers().length > 0 ? (
                      getCurrentPageUsers().map((usuario, index) => (
                        <tr key={usuario.id || index}>
                          <td>{usuario.id}</td>
                          <td>
                            {usuario.nombre ||
                              `${usuario.firstName} ${usuario.lastName}`.trim()}
                          </td>
                          <td>{usuario.email}</td>
                          <td>
                            <span className="badge bg-secondary">
                              {getRoleDisplay(usuario.rol)}
                            </span>
                          </td>
                          <td>
                            <span
                              className={`badge ${
                                usuario.activo || usuario.active
                                  ? "bg-success"
                                  : "bg-danger"
                              }`}
                            >
                              {usuario.activo || usuario.active
                                ? "Activo"
                                : "Inactivo"}
                            </span>
                          </td>
                          <td>{formatDate(usuario.creadoEn)}</td>
                          <td>
                            <div className="d-flex gap-1">
                              <button
                                className="btn btn-sm btn-outline-primary"
                                title="Ver"
                                onClick={() => {
                                  setUserIdToView(usuario.id);
                                  setShowViewModal(true);
                                }}
                              >
                                👁️
                              </button>
                              <ViewUserModal
                                show={showViewModal}
                                onClose={() => {
                                  setShowViewModal(false);
                                  setUserIdToView(null);
                                }}
                                userId={userIdToView}
                              />
                              <button
                                className="btn btn-sm btn-outline-warning"
                                title="Cambiar Contraseña"
                                onClick={() => {
                                  setUserToChangePassword(usuario);
                                  setShowPasswordModal(true);
                                }}
                              >
                                🔑
                              </button>
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
                              <button
                                className="btn btn-sm btn-outline-success"
                                title="Editar"
                                onClick={() => {
                                  setUserToUpdate(usuario);
                                  setShowUpdateModal(true);
                                }}
                              >
                                ✏️
                              </button>
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
                              <button
                                className="btn btn-sm btn-outline-danger"
                                title="Eliminar"
                                onClick={() => {
                                  setUserToDelete(usuario);
                                  setShowDeleteModal(true);
                                }}
                              >
                                🗑️
                              </button>
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
                          </td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan="7" className="text-center py-4 text-muted">
                          No hay usuarios disponibles
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>

              {/* Pagination */}
              {renderPagination()}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default GestionUsuarios;
