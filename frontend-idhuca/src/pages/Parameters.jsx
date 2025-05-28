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
          clave: editingParam.clave, // Use the original clave
          valor: updatedValues.valor,
        },
      };

      await axios(config);
      setError(null);
      setShowModal(false);
      setEditingParam(null);
      await fetchParametros();
    } catch (err) {
      console.error("Error al actualizar parámetro:", err);
      setError(
        err.response?.data?.message || "Error al actualizar el parámetro"
      );
      if (err.response?.status === 401) {
        logout();
      }
    }
  };

  const handleRetry = () => {
    setError(null);
    setShowModal(true);
  };

  const renderTable = () => {
    if (!Array.isArray(parametros)) {
      return (
        <div className="alert alert-danger">Error en el formato de datos</div>
      );
    }

    if (parametros.length === 0) {
      return (
        <div className="alert alert-info d-flex align-items-center">
          <i className="bi bi-info-circle me-2"></i>
          No hay parámetros disponibles
        </div>
      );
    }

    return (
      <div className="table-responsive px-3">
        {" "}
        {/* Added padding */}
        <table
          className="table table-striped table-hover table-bordered"
          style={{ minWidth: "1000px" }}
        >
          {" "}
          {/* Added minWidth */}
          <thead className="table-dark">
            <tr>
              <th scope="col" className="text-center" style={{ width: "5%" }}>
                #
              </th>
              <th scope="col" style={{ width: "20%" }}>
                Clave
              </th>
              <th scope="col" style={{ width: "15%" }}>
                Valor
              </th>
              <th scope="col" style={{ width: "35%" }}>
                Descripción
              </th>
              <th scope="col" style={{ width: "15%" }}>
                Última Actualización
              </th>
              <th scope="col" style={{ width: "10%" }}>
                Acciones
              </th>
            </tr>
          </thead>
          <tbody>
            {parametros.map((param, index) => (
              <tr key={param.id}>
                <td className="text-center">{index + 1}</td>
                <td>{param.clave}</td>
                <td>{param.valor}</td>
                <td>{param.descripcion}</td>
                <td>{new Date(param.actualizadoEn).toLocaleString()}</td>
                <td>
                  <button
                    className="btn btn-primary btn-sm"
                    style={{ minWidth: "100px" }}
                    onClick={() => {
                      setEditingParam(param);
                      setUpdatedValues({
                        clave: param.clave,
                        valor: param.valor,
                      });
                      setShowModal(true);
                    }}
                  >
                    <i className="bi bi-pencil-fill"></i> Editar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {/* Edit Modal */}
        <div
          className={`modal fade ${showModal ? "show" : ""}`}
          style={{ display: showModal ? "block" : "none" }}
          tabIndex="-1"
          role="dialog"
        >
          <div className="modal-dialog">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Editar Parámetro</h5>
                <button
                  type="button"
                  className="btn-close"
                  onClick={() => setShowModal(false)}
                ></button>
              </div>
              <div className="modal-body">
                <div className="mb-3">
                  <label className="form-label">Clave</label>
                  <input
                    type="text"
                    className="form-control"
                    value={editingParam?.clave || ""}
                    disabled
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Valor</label>
                  <input
                    type="text"
                    className="form-control"
                    value={updatedValues.valor}
                    onChange={(e) =>
                      setUpdatedValues({
                        ...updatedValues,
                        valor: e.target.value,
                      })
                    }
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowModal(false)}
                >
                  Cancelar
                </button>
                <button
                  type="button"
                  className="btn btn-primary"
                  onClick={handleUpdateParameter}
                >
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

  return (
    <div className="container-fluid py-4 px-4">
      <div className="row">
        <div className="col-12">
          <div className="card shadow-sm">
            <div className="card-header bg-primary text-white d-flex justify-content-between align-items-center">
              <h5 className="mb-0">
                <i className="bi bi-gear-fill me-2"></i>
                Parámetros del Sistema
              </h5>
            </div>
            <div className="card-body">
              {isLoading ? (
                <div className="d-flex justify-content-center py-5">
                  <div className="spinner-border text-primary" role="status">
                    <span className="visually-hidden">Cargando...</span>
                  </div>
                </div>
              ) : error ? (
                <div className="alert alert-danger d-flex align-items-center justify-content-between">
                  <div>
                    <i className="bi bi-exclamation-triangle-fill me-2"></i>
                    <strong>Error:</strong> {error}
                  </div>
                  <button
                    className="btn btn-outline-danger btn-sm"
                    onClick={handleRetry}
                  >
                    <i className="bi bi-arrow-clockwise me-2"></i>
                    Reintentar
                  </button>
                </div>
              ) : (
                renderTable()
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SistemaParametros;
