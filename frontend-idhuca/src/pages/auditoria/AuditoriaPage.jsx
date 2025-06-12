import { useEffect, useState } from 'react';
import { fetchAuditoria } from '../../services/AuditoriaService';
import './styles/auditoria.css';

const AuditoriaPage = () => {
  const [auditoriaData, setAuditoriaData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [showModal, setShowModal] = useState(false);

  // Estados para filtros
  const [filterUsuario, setFilterUsuario] = useState('');
  const [filterTabla, setFilterTabla] = useState('');
  const [filterOperacion, setFilterOperacion] = useState('');

  // Cargar datos de auditoría
  const loadAuditoria = async () => {
    setLoading(true);
    setError(null);

    try {
      const result = await fetchAuditoria();
      console.log('Datos de auditoría recibidos:', result);
      setAuditoriaData(result || []);
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

  // Aplicar filtros
  const filteredData = auditoriaData.filter(record => {
    const matchUsuario = !filterUsuario || 
      record.usuario?.nombre?.toLowerCase().includes(filterUsuario.toLowerCase()) ||
      record.usuario?.email?.toLowerCase().includes(filterUsuario.toLowerCase());
    
    const matchTabla = !filterTabla || 
      record.tablaAfectada?.toLowerCase().includes(filterTabla.toLowerCase());
    
    const matchOperacion = !filterOperacion || 
      record.operacion?.toLowerCase().includes(filterOperacion.toLowerCase());

    return matchUsuario && matchTabla && matchOperacion;
  });

  // Calcular datos paginados
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = filteredData.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(filteredData.length / itemsPerPage);

  // Manejar cambio de página
  const handlePageChange = (pageNumber) => {
    if (pageNumber < 1 || pageNumber > totalPages) return;
    setCurrentPage(pageNumber);
  };

  // Manejar cambio de items por página
  const handleItemsPerPageChange = (e) => {
    setItemsPerPage(parseInt(e.target.value));
    setCurrentPage(1); // Resetear a la primera página
  };

  // Limpiar filtros
  const clearFilters = () => {
    setFilterUsuario('');
    setFilterTabla('');
    setFilterOperacion('');
    setCurrentPage(1);
  };

  return (
    <div className="auditoria-page-container">
      {/* Header de la página */}
      <div className="auditoria-header">
        <div className="container">
          <h1 className="mb-4">Auditoría del Sistema</h1>

          {loading && (
            <div className="text-center my-4">
              <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Cargando...</span>
              </div>
            </div>
          )}

          {error && (
            <div className="alert alert-danger" role="alert">
              {error}
              <button 
                className="btn btn-sm btn-outline-danger ms-2"
                onClick={loadAuditoria}
              >
                Reintentar
              </button>
            </div>
          )}

          {!loading && !error && (
            <>
  
              {/* Información de registros */}
              <div className="mb-3 small text-muted">
                {filteredData && filteredData.length > 0 ?
                  `Mostrando ${Math.min(currentItems.length, itemsPerPage)} de ${filteredData.length} registros` :
                  'No hay datos para mostrar'}
                {filteredData.length !== auditoriaData.length && (
                  <span className="ms-2 text-info">
                    (filtrados de {auditoriaData.length} registros totales)
                  </span>
                )}
              </div>
            </>
          )}
        </div>
      </div>

      {/* Contenedor de tabla con scroll */}
      {!loading && !error && (
        <div className="auditoria-content-wrapper">
          <div className="container">
            <div className="auditoria-table-container">
              <table className="table table-bordered table-hover mb-0 auditoria-table-sticky">
                <thead className="table-dark sticky-top">
                  <tr>
                    <th style={{width: '60px', minWidth: '60px'}}>ID</th>
                    <th style={{width: '150px', minWidth: '150px'}}>Usuario</th>
                    <th style={{width: '120px', minWidth: '120px'}}>Tabla</th>
                    <th style={{width: '100px', minWidth: '100px'}}>Operación</th>
                    <th style={{width: '80px', minWidth: '80px'}}>Registro ID</th>
                    <th style={{minWidth: '200px'}}>Descripción</th>
                    <th style={{width: '140px', minWidth: '140px'}}>Fecha</th>
                    <th style={{width: '100px', minWidth: '100px'}}>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {currentItems && currentItems.length > 0 ? (
                    currentItems.map((record) => (
                      <tr key={record.id}>
                        <td>{record.id}</td>
                        <td>
                          <div className="small">
                            <strong>{record.usuario?.nombre || 'N/A'}</strong><br />
                            <span className="text-muted">{record.usuario?.email || 'N/A'}</span>
                          </div>
                        </td>
                        <td>
                          <span className="badge bg-secondary">{record.tablaAfectada}</span>
                        </td>
                        <td>
                          <span className={`badge ${
                            record.operacion === 'crear' ? 'bg-success' :
                            record.operacion === 'actualizar' ? 'bg-warning' :
                            record.operacion === 'eliminar' ? 'bg-danger' :
                            'bg-info'
                          }`}>
                            {record.operacion}
                          </span>
                        </td>
                        <td>{record.registroId}</td>
                        <td>
                          <span title={record.descripcion}>
                            {truncateText(record.descripcion, 60)}
                          </span>
                        </td>
                        <td className="small">
                          {formatFecha(record.fecha)}
                        </td>
                        <td>
                          <button
                            className="btn btn-sm btn-outline-primary"
                            onClick={() => handleViewDetails(record)}
                            title="Ver detalles completos"
                          >
                            <i className="fas fa-eye"></i> Ver
                          </button>
                        </td>
                      </tr>
                    ))
                  ) : (
                    <tr>
                      <td colSpan="8" className="text-center">No hay datos disponibles</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>

            {/* Paginación fija en la parte inferior */}
            {filteredData && filteredData.length > 0 && (
              <div className="auditoria-pagination-container">
                <div>
                  Mostrar
                  <select
                    className="form-select form-select-sm d-inline-block mx-2"
                    style={{ width: "80px" }}
                    value={itemsPerPage}
                    onChange={handleItemsPerPageChange}
                  >
                    <option value="5">5</option>
                    <option value="10">10</option>
                    <option value="15">15</option>
                    <option value="20">20</option>
                    <option value="25">25</option>
                    <option value="50">50</option>
                  </select>
                  registros por página
                </div>

                <div>
                  Página {currentPage} de {totalPages}
                  <div className="btn-group ms-2">
                    <button
                      className="btn btn-sm btn-outline-secondary"
                      onClick={() => handlePageChange(1)}
                      disabled={currentPage === 1}
                    >
                      &lt;&lt;
                    </button>
                    <button
                      className="btn btn-sm btn-outline-secondary"
                      onClick={() => handlePageChange(currentPage - 1)}
                      disabled={currentPage === 1}
                    >
                      &lt;
                    </button>
                    <button
                      className="btn btn-sm btn-outline-secondary"
                      onClick={() => handlePageChange(currentPage + 1)}
                      disabled={currentPage === totalPages || totalPages === 0}
                    >
                      &gt;
                    </button>
                    <button
                      className="btn btn-sm btn-outline-secondary"
                      onClick={() => handlePageChange(totalPages)}
                      disabled={currentPage === totalPages || totalPages === 0}
                    >
                      &gt;&gt;
                    </button>
                  </div>
                </div>
              </div>
            )}
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
                  <i className="fas fa-info-circle me-2"></i>
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
                        <strong><i className="fas fa-user me-1"></i> Información del Usuario</strong>
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
                        <strong><i className="fas fa-cog me-1"></i> Información de la Operación</strong>
                      </div>
                      <div className="card-body">
                        <p><strong>Tabla Afectada:</strong> 
                          <span className="badge bg-secondary ms-1">{selectedRecord.tablaAfectada}</span>
                        </p>
                        <p><strong>Operación:</strong> 
                          <span className={`badge ms-1 ${
                            selectedRecord.operacion === 'crear' ? 'bg-success' :
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
                    <strong><i className="fas fa-file-alt me-1"></i> Descripción Completa</strong>
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
                  <i className="fas fa-times me-1"></i> Cerrar
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