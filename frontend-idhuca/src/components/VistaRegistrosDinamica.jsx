import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "./AuthContext"; // Asegúrate de importar useAuth

const VistaRegistrosDinamica = ({
  categoriaEjeX,
  filtros,
  derechoId,
  title = "Registros",
  columns = [],
  data = [],
  isLoading = false,
  error = null,
  onEdit = null,
  onView = null,
  onDelete = null,
  onCreate = null,
  onGenerateChart = null,
  onFilter = null,
  showActions = true,
  currentPage,
  totalPages,
  onPageChange,
  itemsPerPage,
}) => {
  const { userRole } = useAuth(); // Obtener el rol del usuarios
  const navigate = useNavigate();
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteItem, setDeleteItem] = useState(null);

  const getCurrentPageData = () => {
    return data;
  };

  const handlePageChange = (pageNumber) => {
    onPageChange(pageNumber);
  };

  const handleGenerateChart = () => {
    navigate("/graphs", { state: { derechoId, filtros, categoriaEjeX } });
  };

  const handleCreateRegister = () => {
    navigate("/registros/add");
  };

  const handleFilter = () => {
    navigate("/filter", { state: { derechoId, filtros } }); // Pasamos derechoId y filtros en el state
  };

  // Exportar Excel
  const exportExcel = async () => {
    try {
      const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/idhuca-indicadores/api/srv/';
      const token = localStorage.getItem('authToken');
      if (!token) {
        throw new Error('No hay token de autenticación');
      }

      // Construir requestBody con paginacion vacía
      const requestBody = {
        derecho: {
          codigo: derechoId,
          descripcion: ''
        },
        filtros: {
          ...filtros,
          paginacion: {}
        }
      };

      const response = await fetch(`${API_URL}registros/exportData`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestBody)
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || 'Error al exportar datos');
      }

      const blob = await response.blob();

      // Inferir nombre de archivo por fecha
      const now = new Date();
      const filename = `registros_${derechoId || 'all'}_${now.toISOString().slice(0,19).replace(/[:T]/g,'-')}.xlsx`;

      // Crear enlace de descarga
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Error exporting excel:', err);
      // Opcional: mostrar alerta al usuario
      alert(err.message || 'Error al exportar');
    }
  };

  const handleAction = (action, item, index) => {
    switch (action) {
      case "view":
        onView && onView(item, index);
        break;
      case "edit":
        navigate(`/registros/update/${item.id}`);
        break;
      case "delete":
        setDeleteItem(item);
        setShowDeleteModal(true);
        break;
      default:
        break;
    }
  };

  const confirmDelete = () => {
    if (onDelete && deleteItem) {
      onDelete(deleteItem);
    }
    setShowDeleteModal(false);
    setDeleteItem(null);
  };

  const cancelDelete = () => {
    setShowDeleteModal(false);
    setDeleteItem(null);
  };

  const renderCellValue = (item, column) => {
    if (typeof column.render === "function") {
      return column.render(item[column.key], item);
    }
    return item[column.key] || "";
  };

  // Función para verificar permisos
  const hasPermission = (action) => {
    switch (action) {
      case "view":
        return ["ROL_1", "ROL_2", "ROL_3"].includes(userRole);
      case "edit":
      case "delete":
        return ["ROL_1", "ROL_2"].includes(userRole);
      case "filter":
      case "generateChart":
        return ["ROL_1", "ROL_2", "ROL_3"].includes(userRole);
      default:
        return false;
    }
  };

  return (
    <div
      className="container-fluid px-0"
      style={{ minHeight: "100%", width: "100vw", maxWidth: "100%" }}
    >
      <div
        className="container-fluid px-4 py-1 "
        style={{ width: "100%", maxWidth: "100%" }}
      >
        <div className="text-center mb-1">
          <h1
            className="display-4 fw-bold fs-2"
            style={{
              color: "#0f0f0f",
              fontFamily: "Montserrat, Arial, sans-serif",
              letterSpacing: "1px",
            }}
          >
            {title}
          </h1>
        </div>

        <div className="mb-3">
          <div className="d-flex gap-2">
            {onCreate && hasPermission("edit") && (
              <button
                className="btn btn-primary"
                onClick={handleCreateRegister}
                style={{ fontSize: "14px" }}
              >
                Crear
              </button>
            )}
            {onGenerateChart && hasPermission("generateChart") && (
              <button
                className="btn btn-success"
                onClick={handleGenerateChart} // Cambiar aquí el onClick
                style={{ fontSize: "14px" }}
              >
                Generar gráfico
              </button>
            )}
            {onFilter && hasPermission("filter") && (
              <button
                className={`btn btn-info text-white d-flex align-items-center gap-2 position-relative ${
                  filtros && Object.keys(filtros).length > 0
                    ? "active-filter"
                    : ""
                }`}
                onClick={handleFilter}
                style={{ fontSize: "14px" }}
              >
                <i
                  className={`bi bi-funnel${
                    filtros && Object.keys(filtros).length > 0
                      ? "-fill text-light"
                      : ""
                  }`}
                  style={{ fontSize: "18px" }}
                ></i>
                <span>
                  {filtros && Object.keys(filtros).length > 0
                    ? "Filtro (Activos)"
                    : "Filtrar"}
                </span>
                {filtros && Object.keys(filtros).length > 0 && (
                  <span
                    className="position-absolute top-0 end-0 translate-middle p-1 bg-light border border-info rounded-circle"
                    style={{ width: "12px", height: "12px" }}
                  ></span>
                )}
              </button>
            )}
            {/* Botón Exportar */}
            {hasPermission('filter') && (
              <button
                className="btn btn-success d-flex align-items-center gap-2"
                onClick={exportExcel}
                style={{ fontSize: '14px' }}
                title="Exportar Excel"
              >
                <i className="bi bi-file-earmark-excel-fill"></i>
                <span>Exportar</span>
              </button>
            )}
          </div>
        </div>

        <div
          className="bg-white rounded shadow-sm"
          style={{
            overflow: "hidden",
            width: "100%",
            margin: "0 auto",
            maxWidth: "2400px",
          }}
        >
          {isLoading ? (
            <div className="d-flex justify-content-center py-5">
              <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Cargando...</span>
              </div>
            </div>
          ) : error ? (
            <div className="alert alert-danger m-3">
              <strong>Error:</strong> {error}
            </div>
          ) : (
            <>
              <div className="table-responsive" style={{ width: "100%" }}>
                <table
                  className="table table-hover mb-0"
                  style={{
                    width: "100%",
                    tableLayout: "fixed",
                    minWidth: "1200px",
                  }}
                >
                  <thead>
                    <tr>
                      {columns.map((column, index) => (
                        <th
                          key={index}
                          className="px-3 py-2"
                          style={{
                            backgroundColor: "#000000",
                            color: "white",
                            fontSize: "14px",
                            lineHeight: "14px",
                            fontWeight: "600",
                            border: "none",
                            width: showActions
                              ? `${150 / columns.length}%`
                              : `${100 / columns.length}%`,
                            textAlign: "center",
                          }}
                        >
                          {column.title || column.key}
                        </th>
                      ))}
                      {showActions && (
                        <th
                          className="px-3 py-2 text-end"
                          style={{
                            backgroundColor: "#000000",
                            color: "white",
                            fontSize: "14px",
                            fontWeight: "650",
                            border: "none",
                            width: "15%",
                            minWidth: "120px",
                          }}
                        >
                          Acciones
                        </th>
                      )}
                    </tr>
                  </thead>

                  <tbody>
                    {getCurrentPageData().length > 0 ? (
                      getCurrentPageData().map((item, rowIndex) => (
                        <tr key={rowIndex} className="border-bottom">
                          {columns.map((column, colIndex) => (
                            <td
                              key={colIndex}
                              className="px-3 py-2"
                              style={{ fontSize: "13px", textAlign: "center" }}
                            >
                              {renderCellValue(item, column)}
                            </td>
                          ))}
                          {showActions && (
                            <td className="px-3 py-2">
                              <div className="d-flex justify-content-end gap-1">
                                {onView && hasPermission("view") && (
                                  <button
                                    className="btn btn-sm btn-outline-info"
                                    onClick={() =>
                                      handleAction("view", item, rowIndex)
                                    }
                                    title="Ver"
                                  >
                                    <i className="bi bi-eye"></i>
                                  </button>
                                )}
                                {onEdit && hasPermission("edit") && (
                                  <button
                                    className="btn btn-sm btn-outline-primary"
                                    onClick={() =>
                                      handleAction("edit", item, rowIndex)
                                    }
                                    title="Editar"
                                  >
                                    <i className="bi bi-pencil-square"></i>
                                  </button>
                                )}
                                {onDelete && hasPermission("delete") && (
                                  <button
                                    className="btn btn-sm btn-outline-danger"
                                    onClick={() =>
                                      handleAction("delete", item, rowIndex)
                                    }
                                    title="Eliminar"
                                  >
                                    <i className="bi bi-trash"></i>
                                  </button>
                                )}
                              </div>
                            </td>
                          )}
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td
                          colSpan={columns.length + (showActions ? 1 : 0)}
                          className="text-center py-4 text-muted"
                        >
                          No hay datos disponibles
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </>
          )}
        </div>
      </div>

      {/* Modal de confirmación de eliminación */}
      {showDeleteModal && (
        <div
          className="modal show d-block"
          tabIndex="-1"
          style={{ backgroundColor: "rgba(0,0,0,0.2)" }}
        >
          <div className="modal-dialog modal-sm modal-dialog-centered">
            <div className="modal-content">
              <div className="modal-header bg-danger text-white py-2">
                <h6 className="modal-title">
                  <i className="bi bi-exclamation-triangle me-2"></i>
                  Confirmar eliminación
                </h6>
                <button
                  type="button"
                  className="btn-close btn-close-white"
                  onClick={cancelDelete}
                ></button>
              </div>
              <div className="modal-body text-center">
                <p>¿Está seguro que desea eliminar este registro?</p>
                <p className="text-warning">
                  <i className="bi bi-exclamation-triangle-fill me-2"></i>
                  Esta acción no se puede deshacer.
                </p>
              </div>
              <div className="modal-footer d-flex justify-content-between py-2">
                <button
                  className="btn btn-secondary btn-sm"
                  onClick={cancelDelete}
                >
                  Cancelar
                </button>
                <button
                  className="btn btn-danger btn-sm"
                  onClick={confirmDelete}
                >
                  Eliminar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default VistaRegistrosDinamica;
