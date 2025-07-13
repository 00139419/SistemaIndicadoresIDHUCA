import { useEffect, useState } from 'react';
import { fetchAuditoria } from '../../services/AuditoriaService';

const AuditoriaPage = () => {
  const [auditoriaData, setAuditoriaData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [showModal, setShowModal] = useState(false);

  // Estados para paginación del servidor
  const [paginacionInfo, setPaginacionInfo] = useState({
    paginaActual: 1,
    totalPaginas: 1,
    totalRegistros: 0,
    registrosPorPagina: 10
  });

  // Estados para filtros de fecha únicamente
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');

  // Cargar datos de auditoría con parámetros
  const loadAuditoria = async (params = {}) => {
    setLoading(true);
    setError(null);

    try {
      const requestParams = {
        registrosPorPagina: paginacionInfo.registrosPorPagina,
        paginaActual: paginacionInfo.paginaActual,
        fechaInicio: fechaInicio || null,
        fechaFin: fechaFin || null,
        ...params
      };

      console.log('Cargando auditoría con parámetros:', requestParams);

      const result = await fetchAuditoria(requestParams);
      console.log('Resultado recibido:', result);

      setAuditoriaData(result.data || []);
      setPaginacionInfo(result.paginacionInfo);

    } catch (err) {
      setError('Error al cargar datos de auditoría: ' + (err.message || 'Error desconocido'));
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Cargar datos al montar el componente
  useEffect(() => {
    loadAuditoria();
  }, []);

  // Función para formatear fecha
  const formatFecha = (fechaString) => {
    try {
      const fecha = new Date(fechaString);
      return fecha.toLocaleString('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    } catch (error) {
      return fechaString;
    }
  };

  // Función para abrir el modal con los detalles del registro
  const handleViewDetails = (record) => {
    setSelectedRecord(record);
    setShowModal(true);
  };

  // Función para cerrar el modal
  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedRecord(null);
  };

  // Función para truncar texto
  const truncateText = (text, maxLength = 50) => {
    if (!text) return '';
    return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
  };

  // Manejar cambio de página
  const handlePageChange = (pageNumber) => {
    if (pageNumber < 1 || pageNumber > paginacionInfo.totalPaginas) return;

    const newPaginacionInfo = {
      ...paginacionInfo,
      paginaActual: pageNumber
    };

    setPaginacionInfo(newPaginacionInfo);
    loadAuditoria({
      paginaActual: pageNumber,
      registrosPorPagina: paginacionInfo.registrosPorPagina
    });
  };

  // Manejar cambio de items por página
  const handleItemsPerPageChange = (e) => {
    const newItemsPerPage = parseInt(e.target.value);

    const newPaginacionInfo = {
      ...paginacionInfo,
      registrosPorPagina: newItemsPerPage,
      paginaActual: 1
    };

    setPaginacionInfo(newPaginacionInfo);
    loadAuditoria({
      paginaActual: 1,
      registrosPorPagina: newItemsPerPage
    });
  };

  // Aplicar filtros de fecha
  const handleApplyDateFilter = () => {
    const newPaginacionInfo = {
      ...paginacionInfo,
      paginaActual: 1
    };

    setPaginacionInfo(newPaginacionInfo);
    loadAuditoria({
      paginaActual: 1,
      registrosPorPagina: paginacionInfo.registrosPorPagina,
      fechaInicio,
      fechaFin
    });
  };

  // Limpiar filtros
  const clearFilters = () => {
    setFechaInicio('');
    setFechaFin('');

    const newPaginacionInfo = {
      ...paginacionInfo,
      paginaActual: 1
    };

    setPaginacionInfo(newPaginacionInfo);
    loadAuditoria({
      paginaActual: 1,
      registrosPorPagina: paginacionInfo.registrosPorPagina,
      fechaInicio: null,
      fechaFin: null
    });
  };

  return (
    <div className="d-flex flex-column" style={{ height: 'calc(100vh - 160px)' }}>
      {/* Header fijo */}
      <div className="px-4 pb-3 border-bottom bg-white" style={{ flexShrink: 0 }}>
        <h1 className="mb-0 fs-2 text-center fw-bold">
          <i className="bi bi-shield-check me-2 text-primary"></i>
          Auditoría del Sistema
        </h1>
      </div>

      {/* Filtros fijos */}
      <div className="px-4 py-3 bg-light border-bottom" style={{ flexShrink: 0 }}>
        <div className="row mb-3">
          <div className="col-md-3">
            <label className="form-label small">
              <i className="bi bi-calendar-event me-1"></i>
              Fecha Inicio:
            </label>
            <input
              type="date"
              className="form-control form-control-sm"
              value={fechaInicio}
              onChange={(e) => setFechaInicio(e.target.value)}
            />
          </div>
          <div className="col-md-3">
            <label className="form-label small">
              <i className="bi bi-calendar-event me-1"></i>
              Fecha Fin:
            </label>
            <input
              type="date"
              className="form-control form-control-sm"
              value={fechaFin}
              onChange={(e) => setFechaFin(e.target.value)}
            />
          </div>
          <div className="col-md-6 d-flex align-items-end justify-content-between">
            <div>
              <button
                className="btn btn-primary btn-sm me-2"
                onClick={handleApplyDateFilter}
                disabled={loading}
              >
                <i className="bi bi-funnel me-1"></i>
                Aplicar Filtro
              </button>
              <button
                className="btn btn-outline-secondary btn-sm me-2"
                onClick={clearFilters}
                disabled={loading}
              >
                <i className="bi bi-x-lg me-1"></i>
                Limpiar
              </button>
            </div>
            <button
              className="btn btn-outline-primary btn-sm"
              onClick={() => loadAuditoria()}
              disabled={loading}
            >
              <i className="bi bi-arrow-clockwise me-1"></i>
              Actualizar
            </button>
          </div>
        </div>

        {/* Información de registros */}
        <div className="mb-0 small text-muted">
          <i className="bi bi-info-circle me-1"></i>
          {paginacionInfo.totalRegistros > 0 ?
            `Total de registros de auditoría: ${paginacionInfo.totalRegistros}` :
            'No hay registros de auditoría para mostrar'}
        </div>
      </div>

      {/* Contenedor de tabla con scroll */}
      <div className="flex-grow-1 px-4" style={{ overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>

        {loading && (
          <div className="d-flex justify-content-center align-items-center flex-grow-1">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Cargando...</span>
            </div>
          </div>
        )}

        {error && (
          <div className="alert alert-danger mt-3">
            <i className="bi bi-exclamation-triangle me-2"></i>
            <strong>Error:</strong> {error}
            <button
              className="btn btn-sm btn-outline-danger ms-2"
              onClick={() => loadAuditoria()}
            >
              <i className="bi bi-arrow-clockwise me-1"></i>
              Reintentar
            </button>
          </div>
        )}

        {!loading && !error && (
          <>
            {/* Tabla con scroll interno - ESTRUCTURA MEJORADA */}
            <div
              className="table-container mt-3"
              style={{
                flexGrow: 1,
                overflow: 'auto',
                border: '1px solid #dee2e6',
                borderRadius: '0.375rem',
                position: 'relative'
              }}
            >
              <table className="table table-hover mb-0" style={{ borderCollapse: 'separate', borderSpacing: 0 }}>
                <thead className="table-dark">
                  <tr>
                    <th style={{ width: '60px', minWidth: '60px', top: 0, zIndex: 100 }}>
                      <i className="bi bi-hash me-1"></i>ID
                    </th>
                    <th style={{ minWidth: '150px', top: 0, zIndex: 100 }}>
                      <i className="bi bi-person me-1"></i>Usuario
                    </th>
                    <th style={{ width: '120px', minWidth: '120px', top: 0, zIndex: 100 }}>
                      <i className="bi bi-table me-1"></i>Tabla
                    </th>
                    <th style={{ width: '100px', minWidth: '100px', top: 0, zIndex: 100 }}>
                      <i className="bi bi-gear me-1"></i>Operación
                    </th>
                    <th style={{ width: '80px', minWidth: '80px', top: 0, zIndex: 100 }}>
                      <i className="bi bi-record-circle me-1"></i>Registro ID
                    </th>
                    <th style={{ minWidth: '200px', top: 0, zIndex: 100 }}>
                      <i className="bi bi-file-text me-1"></i>Descripción
                    </th>
                    <th style={{ width: '140px', minWidth: '140px', top: 0, zIndex: 100 }}>
                      <i className="bi bi-calendar-plus me-1"></i>Fecha
                    </th>
                    <th style={{ width: '100px', minWidth: '100px', top: 0, zIndex: 100 }}>
                      <i className="bi bi-eye me-1"></i>Acciones
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {auditoriaData && auditoriaData.length > 0 ? (
                    auditoriaData.map((record) => (
                      <tr key={record.id} style={{ backgroundColor: '#fff' }}>
                        <td style={{ backgroundColor: 'inherit' }}>
                          <span className="badge bg-light text-dark">
                            {record.id}
                          </span>
                        </td>
                        <td style={{ backgroundColor: 'inherit' }}>
                          <div className="d-flex align-items-center">
                            <div className="avatar-circle me-2">
                              <i className="bi bi-person-circle fs-6 text-secondary"></i>
                            </div>
                            <div className="small">
                              <strong>{record.usuario?.nombre || 'N/A'}</strong>
                              <div className="text-muted" style={{ fontSize: '0.75rem' }}>
                                {record.usuario?.email || 'N/A'}
                              </div>
                            </div>
                          </div>
                        </td>
                        <td style={{ backgroundColor: 'inherit' }}>
                          <span className="badge bg-secondary">
                            <i className="bi bi-table me-1"></i>
                            {record.tablaAfectada}
                          </span>
                        </td>
                        <td style={{ backgroundColor: 'inherit' }}>
                          <span className={`badge ${record.operacion === 'crear' ? 'bg-success' :
                              record.operacion === 'actualizar' ? 'bg-warning' :
                                record.operacion === 'eliminar' ? 'bg-danger' :
                                  'bg-info'
                            }`}>
                            <i className={`bi ${record.operacion === 'crear' ? 'bi-plus-circle' :
                                record.operacion === 'actualizar' ? 'bi-pencil-square' :
                                  record.operacion === 'eliminar' ? 'bi-trash' :
                                    'bi-gear'
                              } me-1`}></i>
                            {record.operacion}
                          </span>
                        </td>
                        <td style={{ backgroundColor: 'inherit' }}>
                          <span className="badge bg-light text-dark">
                            {record.registroId}
                          </span>
                        </td>
                        <td style={{ backgroundColor: 'inherit' }}>
                          <span title={record.descripcion} className="small">
                            {truncateText(record.descripcion, 60)}
                          </span>
                        </td>
                        <td className="small" style={{ backgroundColor: 'inherit' }}>
                          <i className="bi bi-calendar me-1 text-muted"></i>
                          {formatFecha(record.fecha)}
                        </td>
                        <td style={{ backgroundColor: 'inherit' }}>
                          <button
                            className="btn btn-sm btn-outline-info"
                            onClick={() => handleViewDetails(record)}
                            title="Ver detalles completos"
                          >
                            <i className="bi bi-eye"></i>
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="8" className="text-center py-4 text-muted" style={{ backgroundColor: '#fff' }}>
                        <i className="bi bi-shield-check fs-1 mb-3 d-block text-muted"></i>
                        No hay registros de auditoría disponibles
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </>
        )}
      </div>

      {/* Paginación fija */}
      {!loading && !error && paginacionInfo.totalRegistros > 0 && (
        <div className="px-4 py-3 bg-light border-top" style={{ flexShrink: 0 }}>
          <div className="d-flex justify-content-between align-items-center">
            <div className="d-flex align-items-center">
              <span className="me-2 small">Mostrar</span>
              <select
                className="form-select form-select-sm"
                style={{ width: "80px" }}
                value={paginacionInfo.registrosPorPagina}
                onChange={handleItemsPerPageChange}
                disabled={loading}
              >
                <option value="5">5</option>
                <option value="10">10</option>
                <option value="15">15</option>
                <option value="20">20</option>
                <option value="25">25</option>
                <option value="50">50</option>
              </select>
              <span className="ms-2 small">registros por página</span>
            </div>

            <div className="d-flex align-items-center">
              <span className="me-3 small">
                Página {paginacionInfo.paginaActual} de {paginacionInfo.totalPaginas}
                <span className="ms-2 text-muted">
                  ({paginacionInfo.totalRegistros} registros totales)
                </span>
              </span>
              <div className="btn-group">
                <button
                  className="btn btn-sm btn-outline-secondary"
                  onClick={() => handlePageChange(1)}
                  disabled={paginacionInfo.paginaActual === 1 || loading}
                  title="Primera página"
                >
                  <i className="bi bi-chevron-double-left"></i>
                </button>
                <button
                  className="btn btn-sm btn-outline-secondary"
                  onClick={() => handlePageChange(paginacionInfo.paginaActual - 1)}
                  disabled={paginacionInfo.paginaActual === 1 || loading}
                  title="Página anterior"
                >
                  <i className="bi bi-chevron-left"></i>
                </button>
                <button
                  className="btn btn-sm btn-outline-secondary"
                  onClick={() => handlePageChange(paginacionInfo.paginaActual + 1)}
                  disabled={paginacionInfo.paginaActual === paginacionInfo.totalPaginas || paginacionInfo.totalPaginas === 0 || loading}
                  title="Página siguiente"
                >
                  <i className="bi bi-chevron-right"></i>
                </button>
                <button
                  className="btn btn-sm btn-outline-secondary"
                  onClick={() => handlePageChange(paginacionInfo.totalPaginas)}
                  disabled={paginacionInfo.paginaActual === paginacionInfo.totalPaginas || paginacionInfo.totalPaginas === 0 || loading}
                  title="Última página"
                >
                  <i className="bi bi-chevron-double-right"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Modal para ver detalles completos */}
      {showModal && selectedRecord && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 1050 }}>
          <div className="modal-dialog modal-lg">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">
                  <i className="bi bi-info-circle me-2"></i>
                  Detalles de Auditoría - ID: {selectedRecord.id}
                </h5>
                <button
                  type="button"
                  className="btn-close"
                  onClick={handleCloseModal}
                ></button>
              </div>
              <div className="modal-body">
                <div className="row">
                  <div className="col-md-6">
                    <div className="card mb-3">
                      <div className="card-header">
                        <strong><i className="bi bi-person me-1"></i> Información del Usuario</strong>
                      </div>
                      <div className="card-body">
                        <p><strong>ID:</strong> {selectedRecord.usuario?.id || 'N/A'}</p>
                        <p><strong>Nombre:</strong> {selectedRecord.usuario?.nombre || 'N/A'}</p>
                        <p><strong>Email:</strong> {selectedRecord.usuario?.email || 'N/A'}</p>
                        <p><strong>Rol:</strong>
                          <span className="badge bg-primary ms-1">
                            {selectedRecord.usuario?.rol?.descripcion || 'N/A'}
                          </span>
                        </p>
                        <p><strong>Estado:</strong>
                          <span className={`badge ms-1 ${selectedRecord.usuario?.activo ? 'bg-success' : 'bg-danger'}`}>
                            {selectedRecord.usuario?.activo ? 'Activo' : 'Inactivo'}
                          </span>
                        </p>
                        <p><strong>Creado:</strong> {formatFecha(selectedRecord.usuario?.creadoEn)}</p>
                      </div>
                    </div>
                  </div>
                  <div className="col-md-6">
                    <div className="card mb-3">
                      <div className="card-header">
                        <strong><i className="bi bi-gear me-1"></i> Información de la Operación</strong>
                      </div>
                      <div className="card-body">
                        <p><strong>Tabla Afectada:</strong>
                          <span className="badge bg-secondary ms-1">{selectedRecord.tablaAfectada}</span>
                        </p>
                        <p><strong>Operación:</strong>
                          <span className={`badge ms-1 ${selectedRecord.operacion === 'crear' ? 'bg-success' :
                              selectedRecord.operacion === 'actualizar' ? 'bg-warning' :
                                selectedRecord.operacion === 'eliminar' ? 'bg-danger' :
                                  'bg-info'
                            }`}>
                            {selectedRecord.operacion}
                          </span>
                        </p>
                        <p><strong>ID del Registro:</strong> {selectedRecord.registroId}</p>
                        <p><strong>Fecha de la Operación:</strong> {formatFecha(selectedRecord.fecha)}</p>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="card">
                  <div className="card-header">
                    <strong><i className="bi bi-file-text me-1"></i> Descripción Completa</strong>
                  </div>
                  <div className="card-body">
                    <div className="bg-light p-3 rounded" style={{ maxHeight: '300px', overflowY: 'auto' }}>
                      <pre style={{ whiteSpace: 'pre-wrap', fontSize: '0.9rem', margin: 0 }}>
                        {selectedRecord.descripcion}
                      </pre>
                    </div>
                  </div>
                </div>
              </div>
              <div className="modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={handleCloseModal}
                >
                  <i className="bi bi-x-lg me-1"></i> Cerrar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AuditoriaPage;