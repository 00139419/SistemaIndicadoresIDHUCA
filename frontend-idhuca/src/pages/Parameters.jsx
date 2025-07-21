import { useState, useEffect } from "react";
import { useAuth } from "../components/AuthContext";
import axios from "axios";

const SistemaParametros = () => {
  const [parametros, setParametros] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const { isAuthenticated, logout } = useAuth();
  const [editingParam, setEditingParam] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [updatedValues, setUpdatedValues] = useState({ clave: "", valor: "" });
  const [modalError, setModalError] = useState(null);

  const fetchParametros = async () => {
    try {
      setIsLoading(true);
      setError(null);

      const token = localStorage.getItem("authToken");

      if (!token || !isAuthenticated) {
        logout();
        throw new Error(
          "No se encontró el token de autenticación o la sesión expiró."
        );
      }

      const config = {
        method: "post",
        url: "http://localhost:8080/idhuca-indicadores/api/srv/parametros/sistema/getAll",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      };

      const response = await axios(config);

      const parametrosData = response.data?.entity || [];
      if (!Array.isArray(parametrosData)) {
        throw new Error("Formato de respuesta inválido");
      }

      setParametros(parametrosData);
    } catch (err) {
      console.error("Error al obtener parámetros:", err);
      setError(err.response?.data?.message || "Error al cargar los parámetros");
      if (err.response?.status === 401) {
        logout();
      }
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchParametros();
  }, [isAuthenticated]);

  const handleUpdateParameter = async () => {
    try {
      setModalError(null);
      const token = localStorage.getItem("authToken");
      if (!token || !isAuthenticated) {
        logout();
        throw new Error("No se encontró el token de autenticación.");
      }
      const config = {
        method: "post",
        url: "http://localhost:8080/idhuca-indicadores/api/srv/parametros/sistema/update",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        data: {
          clave: editingParam.clave,
          valor: updatedValues.valor,
        },
      };
      await axios(config);
      setModalError(null);
      setShowModal(false);
      setEditingParam(null);
      await fetchParametros();
    } catch (err) {
      console.error("Error al actualizar parámetro:", err.response.data.mensaje);
      setModalError(
        err.response.data.mensaje || "Error al actualizar el parámetro"
      );
      if (err.response?.status === 401) {
        logout();
      }
    }
  };

  return (
    <div
      className="d-flex flex-column"
      style={{ height: "calc(100vh - 160px)" }}
    >
      {/* Header fijo */}
      <div className="px-4 pt-1 pb-3 border-bottom bg-white">
        <h1 className="mb-0 fs-2 fw-bold text-center">
          <i className="bi bi-gear-fill me-2 text-primary"></i>
          Parámetros del Sistema
        </h1>
      </div>

      {/* Contenido con scroll */}
      <div
        className="flex-grow-1 px-4 py-3"
        style={{ overflowY: "auto", height: "100%" }}
      >
        {/* Botones de acción */}
        <div className="mb-3">
          <div className="d-flex justify-content-end align-items-center mb-0">
            <button
              className="btn btn-outline-primary btn-sm"
              onClick={fetchParametros}
              disabled={isLoading}
            >
              <i className="bi bi-arrow-clockwise me-1"></i>
              Actualizar
            </button>
          </div>

          {/* Información de registros */}
          <div className="mb-3 small text-muted">
            <i className="bi bi-info-circle me-1"></i>
            {parametros.length > 0
              ? `Total de parámetros: ${parametros.length}`
              : "No hay parámetros para mostrar"}
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
              onClick={fetchParametros}
            >
              <i className="bi bi-arrow-clockwise me-1"></i>
              Reintentar
            </button>
          </div>
        ) : (
          <>
            {/* Tabla de parámetros */}

            <div className="table-responsive">
              <table className="table table-bordered table-hover mb-0">
                <thead className="table-dark">
                  <tr>
                    <th style={{ width: "60px", minWidth: "60px" }}>
                      <i className="bi bi-hash me-1"></i>#
                    </th>
                    <th style={{ minWidth: "200px" }}>
                      <i className="bi bi-key me-1"></i>Clave
                    </th>
                    <th style={{ minWidth: "150px" }}>
                      <i className="bi bi-tag me-1"></i>Valor
                    </th>
                    <th style={{ minWidth: "300px" }}>
                      <i className="bi bi-info-circle me-1"></i>Descripción
                    </th>
                    <th style={{ width: "140px", minWidth: "140px" }}>
                      <i className="bi bi-calendar-plus me-1"></i>Última
                      Actualización
                    </th>
                    <th style={{ width: "120px", minWidth: "120px" }}>
                      <i className="bi bi-gear me-1"></i>Acciones
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {parametros.length > 0 ? (
                    parametros.map((param, index) => (
                      <tr key={param.id || index}>
                        <td>
                          <span className="badge bg-light text-dark">
                            {index + 1}
                          </span>
                        </td>
                        <td>
                          <div className="d-flex align-items-center">
                            <i className="bi bi-key me-2 text-muted"></i>
                            <strong className="small">{param.clave}</strong>
                          </div>
                        </td>
                        <td style={{ maxWidth: "300px" }}>
                          <span
                            className="badge bg-secondary d-inline-block text-truncate w-100"
                            style={{ maxWidth: "100%" }} // o un valor fijo si prefieres
                            title={param.valor} // muestra el valor completo al hover
                          >
                            <i className="bi bi-tag me-1"></i>
                            {param.valor}
                          </span>
                        </td>

                        <td
                          style={{
                            maxWidth: "300px",
                            wordWrap: "break-word",
                            whiteSpace: "normal",
                          }}
                        >
                          <div className="small text-muted">
                            {param.descripcion}
                          </div>
                        </td>

                        <td className="small">
                          <i className="bi bi-calendar me-1 text-muted"></i>
                          {new Date(param.actualizadoEn).toLocaleDateString(
                            "es-ES",
                            {
                              year: "numeric",
                              month: "2-digit",
                              day: "2-digit",
                              hour: "2-digit",
                              minute: "2-digit",
                            }
                          )}
                        </td>
                        <td>
                          <button
                            className="btn btn-sm btn-outline-primary"
                            title="Editar parámetro"
                            onClick={() => {
                              setEditingParam(param);
                              setUpdatedValues({
                                clave: param.clave,
                                valor: param.valor,
                              });
                              setShowModal(true);
                            }}
                          >
                            <i className="bi bi-pencil-square"></i>
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="6" className="text-center py-4 text-muted">
                        <i className="bi bi-gear fs-1 mb-3 d-block text-muted"></i>
                        No hay parámetros disponibles
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </>
        )}
      </div>

      {/* Modal de edición */}
      <div
        className={`modal fade ${showModal ? "show" : ""}`}
        style={{ display: showModal ? "block" : "none" }}
        tabIndex="-1"
        role="dialog"
      >
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">
                <i className="bi bi-pencil-square me-2"></i>
                Editar Parámetro
              </h5>
              <button
                type="button"
                className="btn-close"
                onClick={() => {
                  setShowModal(false);
                  setTimeout(() => setModalError(null), 300);
                }}
              ></button>
            </div>
            <div className="modal-body">
              {modalError && (
                <div className="alert alert-danger py-2 small mb-3">
                  <i className="bi bi-exclamation-triangle me-2"></i>
                  {modalError}
                </div>
              )}
              {/* Limpiar error automáticamente después de 3 segundos */}
              {modalError && setTimeout(() => setModalError(null), 3000)}
              <div className="mb-3">
                <label className="form-label">
                  <i className="bi bi-key me-1"></i>Clave
                </label>
                <input
                  type="text"
                  className="form-control"
                  value={editingParam?.clave || ""}
                  disabled
                />
              </div>
              <div className="mb-3">
                <label className="form-label">
                  <i className="bi bi-tag me-1"></i>Valor
                </label>
                <textarea
                  className="form-control"
                  value={updatedValues.valor}
                  onChange={(e) =>
                    setUpdatedValues({
                      ...updatedValues,
                      valor: e.target.value,
                    })
                  }
                  rows={6} // define el alto (6 líneas por ejemplo)
                  style={{ resize: "vertical" }} // permite que el usuario cambie el alto si quiere
                  required // evita que quede vacío
                />
              </div>
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setShowModal(false)}
              >
                <i className="bi bi-x-circle me-1"></i>
                Cancelar
              </button>
              <button
                type="button"
                className="btn btn-primary"
                onClick={handleUpdateParameter}
              >
                <i className="bi bi-check-circle me-1"></i>
                Guardar Cambios
              </button>
            </div>
          </div>
        </div>
      </div>
      {showModal && <div className="modal-backdrop fade show"></div>}
    </div>
  );
};

export default SistemaParametros;
