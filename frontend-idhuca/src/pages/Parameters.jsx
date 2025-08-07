import { useState, useEffect } from "react";
import { useAuth } from "../components/AuthContext";
import axios from "axios";

const SistemaParametros = () => {
  const API_URL = process.env.REACT_APP_API_URL;
  const API_BACKUP_URL = process.env.REACT_APP_API_BACKUP;
  const [parametros, setParametros] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const { isAuthenticated, logout } = useAuth();
  const [editingParam, setEditingParam] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [updatedValues, setUpdatedValues] = useState({ clave: "", valor: "" });
  const [modalError, setModalError] = useState(null);
  const [backups, setBackups] = useState([]);
  const [isLoadingBackups, setIsLoadingBackups] = useState(true);
  const [backupError, setBackupError] = useState(null);
  const [showBackupModal, setShowBackupModal] = useState(false);
  const [backupType, setBackupType] = useState("manual");
  const [backupForm, setBackupForm] = useState({
    name: "",
    dayOfWeek: "MON",
    dayOfMonth: 1,
    hour: 0,
    minute: 0,
  });
  const [backupModalError, setBackupModalError] = useState(null);
  const [isExecutingBackup, setIsExecutingBackup] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [backupToDelete, setBackupToDelete] = useState(null);
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

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
        url: API_URL +"parametros/sistema/getAll",
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
        url: API_URL +"parametros/sistema/update",
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
      console.error(
        "Error al actualizar parámetro:",
        err.response.data.mensaje
      );
      setModalError(
        err.response.data.mensaje || "Error al actualizar el parámetro"
      );
      if (err.response?.status === 401) {
        logout();
      }
    }
  };

  const fetchBackups = async () => {
    try {
      setIsLoadingBackups(true);
      setBackupError(null);

      const token = localStorage.getItem("authToken");

      if (!token || !isAuthenticated) {
        logout();
        throw new Error("No se encontró el token de autenticación.");
      }

      const config = {
        method: "get",
        url: API_BACKUP_URL +"schedules",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      };

      const response = await axios(config);

      // Cambio aquí: acceder a response.data.data en lugar de response.data
      setBackups(response.data?.data || []);
    } catch (err) {
      console.error("Error al obtener backups:", err);
      setBackupError(
        err.response?.data?.message || "Error al cargar los backups"
      );
      if (err.response?.status === 401) {
        logout();
      }
    } finally {
      setIsLoadingBackups(false);
    }
  };

  const executeManualBackup = async (backupName) => {
    try {
      setIsExecutingBackup(true);
      const token = localStorage.getItem("authToken");

      if (!token || !isAuthenticated) {
        logout();
        throw new Error("No se encontró el token de autenticación.");
      }

      const config = {
        method: "post",
        url: `${API_BACKUP_URL}schedules/${backupName}/execute`,
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      };

      await axios(config);
      setSuccessMessage("Backup ejecutado correctamente");
      setShowSuccessModal(true);
    } catch (err) {
      console.error("Error al ejecutar backup:", err);
      alert(err.response?.data?.message || "Error al ejecutar el backup");
      if (err.response?.status === 401) {
        logout();
      }
    } finally {
      setIsExecutingBackup(false);
    }
  };

  const createBackup = async () => {
    try {
      setBackupModalError(null);
      const token = localStorage.getItem("authToken");

      if (!token || !isAuthenticated) {
        logout();
        throw new Error("No se encontró el token de autenticación.");
      }

      let url = API_BACKUP_URL;
      let params = new URLSearchParams();

      if (backupType === "manual") {
        // Para backup manual, simplemente ejecutamos
        await executeManualBackup(backupForm.name);
        setShowBackupModal(false);
        return;
      } else if (backupType === "weekly") {
        url += "/quick-schedules/weekly";
        params.append("name", backupForm.name);
        params.append("dayOfWeek", backupForm.dayOfWeek);
        params.append("hour", backupForm.hour);
        params.append("minute", backupForm.minute);
      } else if (backupType === "monthly") {
        url += "/quick-schedules/monthly";
        params.append("name", backupForm.name);
        params.append("dayOfMonth", backupForm.dayOfMonth);
        params.append("hour", backupForm.hour);
        params.append("minute", backupForm.minute);
      }

      const config = {
        method: "post",
        url: `${url}?${params.toString()}`,
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      };

      await axios(config);
      setShowBackupModal(false);
      setBackupForm({
        name: "",
        dayOfWeek: "MON",
        dayOfMonth: 1,
        hour: 0,
        minute: 0,
      });
      await fetchBackups();
    } catch (err) {
      console.error("Error al crear backup:", err);
      setBackupModalError(
        err.response?.data?.message || "Error al crear el backup"
      );
      if (err.response?.status === 401) {
        logout();
      }
    }
  };

  const deleteBackup = async (backupName) => {
    setBackupToDelete(backupName);
    setShowDeleteModal(true);
  };

  const confirmDeleteBackup = async () => {
    try {
      const token = localStorage.getItem("authToken");

      if (!token || !isAuthenticated) {
        logout();
        throw new Error("No se encontró el token de autenticación.");
      }

      const config = {
        method: "delete",
        url: `${API_BACKUP_URL}schedules/${backupToDelete}`,
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      };

      await axios(config);
      setShowDeleteModal(false);
      setBackupToDelete(null);
      await fetchBackups();
    } catch (err) {
      console.error("Error al eliminar backup:", err);
      alert(err.response?.data?.message || "Error al eliminar el backup");
      if (err.response?.status === 401) {
        logout();
      }
    }
  };

  useEffect(() => {
    fetchParametros();
    fetchBackups();
  }, [isAuthenticated]);

  const getDaysOfWeekOptions = () => [
    { value: "MON", label: "Lunes" },
    { value: "TUE", label: "Martes" },
    { value: "WED", label: "Miércoles" },
    { value: "THU", label: "Jueves" },
    { value: "FRI", label: "Viernes" },
    { value: "SAT", label: "Sábado" },
    { value: "SUN", label: "Domingo" },
  ];

  const formatBackupSchedule = (backup) => {
    if (backup.cronExpression) {
      // Intentar interpretar la expresión cron
      const parts = backup.cronExpression.split(" ");
      if (parts.length >= 5) {
        const minute = parts[1];
        const hour = parts[2];
        const dayOfMonth = parts[3];
        const dayOfWeek = parts[5];

        if (dayOfMonth === "*" && dayOfWeek !== "*") {
          const dayNames = ["DOM", "LUN", "MAR", "MIE", "JUE", "VIE", "SAB"];
          return `Semanal - ${
            dayNames[parseInt(dayOfWeek)] || dayOfWeek
          } a las ${hour}:${minute.padStart(2, "0")}`;
        } else if (dayOfMonth !== "*" && dayOfWeek === "*") {
          return `Mensual - Día ${dayOfMonth} a las ${hour}:${minute.padStart(
            2,
            "0"
          )}`;
        }
      }
    }
    return backup.cronExpression || "Programación personalizada";
  };

  useEffect(() => {
    if (modalError) {
      const timer = setTimeout(() => setModalError(null), 3000);
      return () => clearTimeout(timer);
    }
  }, [modalError]);

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
        {/* Sección de Parámetros */}
        <div className="mb-6">
          {/* Botones de acción */}
          <div className="mb-3">
            <div className="d-flex justify-content-between align-items-center mb-0">
              <h3 className="mb-0">
                <i className="bi bi-sliders me-2 text-primary"></i>
                Parámetros de Configuración
              </h3>
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
                            style={{ maxWidth: "100%" }}
                            title={param.valor}
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
          )}
        </div>

        {/* Sección de Backups */}
        <div className="mb-4">
          <div className="mb-3">
            <div className="d-flex justify-content-between align-items-center mb-0">
              <h3 className="mb-0">
                <i className="bi bi-shield-check me-2 text-success"></i>
                Gestión de Backups
              </h3>
              <div className="d-flex gap-2">
                <button
                  className="btn btn-success btn-sm"
                  onClick={() => setShowBackupModal(true)}
                >
                  <i className="bi bi-plus-circle me-1"></i>
                  Nuevo Backup
                </button>
                <button
                  className="btn btn-outline-success btn-sm"
                  onClick={fetchBackups}
                  disabled={isLoadingBackups}
                >
                  <i className="bi bi-arrow-clockwise me-1"></i>
                  Actualizar
                </button>
              </div>
            </div>

            <div className="mb-3 small text-muted">
              <i className="bi bi-info-circle me-1"></i>
              {backups.length > 0
                ? `Total de backups programados: ${backups.length}`
                : "No hay backups programados"}
            </div>
          </div>

          {isLoadingBackups ? (
            <div className="d-flex justify-content-center py-5">
              <div className="spinner-border text-success" role="status">
                <span className="visually-hidden">Cargando backups...</span>
              </div>
            </div>
          ) : backupError ? (
            <div className="alert alert-warning">
              <i className="bi bi-exclamation-triangle me-2"></i>
              <strong>Error:</strong> {backupError}
              <button
                className="btn btn-sm btn-outline-warning ms-2"
                onClick={fetchBackups}
              >
                <i className="bi bi-arrow-clockwise me-1"></i>
                Reintentar
              </button>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table table-bordered table-hover mb-0">
                <thead className="table-success">
                  <tr>
                    <th style={{ width: "60px", minWidth: "60px" }}>
                      <i className="bi bi-hash me-1"></i>#
                    </th>
                    <th style={{ minWidth: "200px" }}>
                      <i className="bi bi-bookmark me-1"></i>Nombre
                    </th>
                    <th style={{ minWidth: "250px" }}>
                      <i className="bi bi-clock me-1"></i>Programación
                    </th>
                    <th style={{ width: "120px", minWidth: "120px" }}>
                      <i className="bi bi-activity me-1"></i>Estado
                    </th>
                    <th style={{ width: "180px", minWidth: "180px" }}>
                      <i className="bi bi-gear me-1"></i>Acciones
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {backups.length > 0 ? (
                    backups.map((backup, index) => (
                      <tr key={backup.name || index}>
                        <td>
                          <span className="badge bg-light text-dark">
                            {index + 1}
                          </span>
                        </td>
                        <td>
                          <div className="d-flex align-items-center">
                            <i className="bi bi-bookmark me-2 text-muted"></i>
                            <strong className="small">{backup.name}</strong>
                          </div>
                        </td>
                        <td>
                          <span className="badge bg-info text-white">
                            <i className="bi bi-clock me-1"></i>
                            {formatBackupSchedule(backup)}
                          </span>
                        </td>
                        <td>
                          <span
                            className={`badge ${
                              backup.active ? "bg-success" : "bg-secondary"
                            }`}
                          >
                            <i
                              className={`bi ${
                                backup.active
                                  ? "bi-check-circle"
                                  : "bi-pause-circle"
                              } me-1`}
                            ></i>
                            {backup.active ? "Activo" : "Inactivo"}
                          </span>
                        </td>
                        <td>
                          <div className="d-flex gap-1">
                            <button
                              className="btn btn-sm btn-outline-primary"
                              title="Ejecutar backup ahora"
                              onClick={() => executeManualBackup(backup.name)}
                              disabled={isExecutingBackup}
                            >
                              <i className="bi bi-play-fill"></i>
                            </button>
                            <button
                              className="btn btn-sm btn-outline-danger"
                              title="Eliminar backup"
                              onClick={() => deleteBackup(backup.name)}
                            >
                              <i className="bi bi-trash"></i>
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="5" className="text-center py-4 text-muted">
                        <i className="bi bi-shield-check fs-1 mb-3 d-block text-muted"></i>
                        No hay backups programados
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* Modal de edición de parámetros */}
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
                  rows={6}
                  style={{ resize: "vertical" }}
                  required
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

      {/* Modal de creación de backup */}
      <div
        className={`modal fade ${showBackupModal ? "show" : ""}`}
        style={{ display: showBackupModal ? "block" : "none" }}
        tabIndex="-1"
        role="dialog"
      >
        <div className="modal-dialog modal-lg">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">
                <i className="bi bi-plus-circle me-2"></i>
                Nuevo Backup
              </h5>
              <button
                type="button"
                className="btn-close"
                onClick={() => {
                  setShowBackupModal(false);
                  setTimeout(() => setBackupModalError(null), 300);
                }}
              ></button>
            </div>
            <div className="modal-body">
              {backupModalError && (
                <div className="alert alert-danger py-2 small mb-3">
                  <i className="bi bi-exclamation-triangle me-2"></i>
                  {backupModalError}
                </div>
              )}

              <div className="mb-3">
                <label className="form-label">
                  <i className="bi bi-bookmark me-1"></i>Nombre del Backup
                </label>
                <input
                  type="text"
                  className="form-control"
                  value={backupForm.name}
                  onChange={(e) =>
                    setBackupForm({ ...backupForm, name: e.target.value })
                  }
                  placeholder="Ej: backup_semanal_lunes"
                  required
                />
              </div>

              <div className="mb-3">
                <label className="form-label">
                  <i className="bi bi-clock me-1"></i>Tipo de Backup
                </label>
                <select
                  className="form-select"
                  value={backupType}
                  onChange={(e) => setBackupType(e.target.value)}
                >
                  <option value="manual">Manual (Ejecutar ahora)</option>
                  <option value="weekly">Semanal</option>
                  <option value="monthly">Mensual</option>
                </select>
              </div>

              {backupType === "weekly" && (
                <>
                  <div className="mb-3">
                    <label className="form-label">
                      <i className="bi bi-calendar-week me-1"></i>Día de la
                      Semana
                    </label>
                    <select
                      className="form-select"
                      value={backupForm.dayOfWeek}
                      onChange={(e) =>
                        setBackupForm({
                          ...backupForm,
                          dayOfWeek: e.target.value,
                        })
                      }
                    >
                      {getDaysOfWeekOptions().map((day) => (
                        <option key={day.value} value={day.value}>
                          {day.label}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="row">
                    <div className="col-6">
                      <label className="form-label">
                        <i className="bi bi-clock me-1"></i>Hora
                      </label>
                      <select
                        className="form-select"
                        value={backupForm.hour}
                        onChange={(e) =>
                          setBackupForm({
                            ...backupForm,
                            hour: parseInt(e.target.value),
                          })
                        }
                      >
                        {Array.from({ length: 24 }, (_, i) => (
                          <option key={i} value={i}>
                            {i.toString().padStart(2, "0")}:00
                          </option>
                        ))}
                      </select>
                    </div>
                    <div className="col-6">
                      <label className="form-label">
                        <i className="bi bi-stopwatch me-1"></i>Minuto
                      </label>
                      <select
                        className="form-select"
                        value={backupForm.minute}
                        onChange={(e) =>
                          setBackupForm({
                            ...backupForm,
                            minute: parseInt(e.target.value),
                          })
                        }
                      >
                        {Array.from({ length: 60 }, (_, i) => (
                          <option key={i} value={i}>
                            {i.toString().padStart(2, "0")}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>
                </>
              )}

              {backupType === "monthly" && (
                <>
                  <div className="mb-3">
                    <label className="form-label">
                      <i className="bi bi-calendar-month me-1"></i>Día del Mes
                    </label>
                    <select
                      className="form-select"
                      value={backupForm.dayOfMonth}
                      onChange={(e) =>
                        setBackupForm({
                          ...backupForm,
                          dayOfMonth: parseInt(e.target.value),
                        })
                      }
                    >
                      {Array.from({ length: 28 }, (_, i) => i + 1).map(
                        (day) => (
                          <option key={day} value={day}>
                            Día {day}
                          </option>
                        )
                      )}
                    </select>
                  </div>
                  <div className="row">
                    <div className="col-6">
                      <label className="form-label">
                        <i className="bi bi-clock me-1"></i>Hora
                      </label>
                      <select
                        className="form-select"
                        value={backupForm.hour}
                        onChange={(e) =>
                          setBackupForm({
                            ...backupForm,
                            hour: parseInt(e.target.value),
                          })
                        }
                      >
                        {Array.from({ length: 24 }, (_, i) => (
                          <option key={i} value={i}>
                            {i.toString().padStart(2, "0")}:00
                          </option>
                        ))}
                      </select>
                    </div>

                    <div className="col-6">
                      <label className="form-label">
                        <i className="bi bi-stopwatch me-1"></i>Minuto
                      </label>
                      <select
                        className="form-select"
                        value={backupForm.minute}
                        onChange={(e) =>
                          setBackupForm({
                            ...backupForm,
                            minute: parseInt(e.target.value),
                          })
                        }
                      >
                        {Array.from({ length: 60 }, (_, i) => (
                          <option key={i} value={i}>
                            {i.toString().padStart(2, "0")}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>
                </>
              )}
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setShowBackupModal(false)}
              >
                <i className="bi bi-x-circle me-1"></i>
                Cancelar
              </button>
              <button
                type="button"
                className="btn btn-success"
                onClick={createBackup}
                disabled={!backupForm.name.trim()}
              >
                <i className="bi bi-check-circle me-1"></i>
                {backupType === "manual" ? "Ejecutar Backup" : "Crear Backup"}
              </button>
            </div>
          </div>
        </div>
      </div>
      {showBackupModal && <div className="modal-backdrop fade show"></div>}

      {/* Modal de confirmación de eliminación */}
      <div
        className={`modal fade ${showDeleteModal ? "show" : ""}`}
        style={{ display: showDeleteModal ? "block" : "none" }}
        tabIndex="-1"
        role="dialog"
      >
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content">
            <div className="modal-header bg-danger text-white">
              <h5 className="modal-title">
                <i className="bi bi-exclamation-triangle me-2"></i>
                Confirmar Eliminación
              </h5>
              <button
                type="button"
                className="btn-close btn-close-white"
                onClick={() => {
                  setShowDeleteModal(false);
                  setBackupToDelete(null);
                }}
              ></button>
            </div>
            <div className="modal-body">
              <div className="text-center py-3">
                <i className="bi bi-trash fs-1 text-danger mb-3"></i>
                <h6 className="mb-3">
                  ¿Estás seguro de que deseas eliminar el backup?
                </h6>
                <div className="alert alert-warning">
                  <strong>"{backupToDelete}"</strong>
                </div>
                <p className="text-muted small">
                  Esta acción no se puede deshacer.
                </p>
              </div>
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => {
                  setShowDeleteModal(false);
                  setBackupToDelete(null);
                }}
              >
                <i className="bi bi-x-circle me-1"></i>
                Cancelar
              </button>
              <button
                type="button"
                className="btn btn-danger"
                onClick={confirmDeleteBackup}
              >
                <i className="bi bi-trash me-1"></i>
                Eliminar Backup
              </button>
            </div>
          </div>
        </div>
      </div>
      {showDeleteModal && <div className="modal-backdrop fade show"></div>}

      {/* Modal de éxito */}
      <div
        className={`modal fade ${showSuccessModal ? "show" : ""}`}
        style={{ display: showSuccessModal ? "block" : "none" }}
        tabIndex="-1"
        role="dialog"
      >
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content">
            <div className="modal-header bg-success text-white">
              <h5 className="modal-title">
                <i className="bi bi-check-circle me-2"></i>
                Operación Exitosa
              </h5>
              <button
                type="button"
                className="btn-close btn-close-white"
                onClick={() => setShowSuccessModal(false)}
              ></button>
            </div>
            <div className="modal-body">
              <div className="text-center py-3">
                <i className="bi bi-check-circle fs-1 text-success mb-3"></i>
                <h6 className="mb-3">{successMessage}</h6>
                <p className="text-muted small">
                  El backup se ha ejecutado correctamente.
                </p>
              </div>
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-success"
                onClick={() => setShowSuccessModal(false)}
              >
                <i className="bi bi-check me-1"></i>
                Aceptar
              </button>
            </div>
          </div>
        </div>
      </div>
      {showSuccessModal && <div className="modal-backdrop fade show"></div>}
    </div>
  );
};

export default SistemaParametros;
