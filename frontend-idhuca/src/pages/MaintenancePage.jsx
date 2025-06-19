import { useEffect, useState, useCallback, useMemo } from 'react';
import Sidenav from '../components/Sidenav';
import { fetchCatalog, addCatalogItem, updateCatalogItem, deleteCatalogItem } from '../services/CatalogService';

const MaintenancePage = () => {
  const [catalogData, setCatalogData] = useState([]);
  const [selectedCatalog, setSelectedCatalog] = useState('departamentos');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Estados para el selector de departamentos (cuando se selecciona municipios)
  const [departamentos, setDepartamentos] = useState([]);
  const [selectedDepartamento, setSelectedDepartamento] = useState('');
  const [loadingDepartamentos, setLoadingDepartamentos] = useState(false);

  // Estados para el modal de nuevo registro
  const [showModal, setShowModal] = useState(false);
  const [newItemDescription, setNewItemDescription] = useState('');
  const [addingItem, setAddingItem] = useState(false);
  const [addResult, setAddResult] = useState(null);

  // Estados para el modal de edición
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [editDescription, setEditDescription] = useState('');
  const [updatingItem, setUpdatingItem] = useState(false);
  const [updateResult, setUpdateResult] = useState(null);

  // Estados para confirmación de eliminación
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deletingItem, setDeletingItem] = useState(null);
  const [itemToDelete, setItemToDelete] = useState(null);
  const [deleteResult, setDeleteResult] = useState(null);

  // Estado para paginación del servidor
  const [serverPagination, setServerPagination] = useState({
    paginaActual: 1, // UI usa base 1
    totalPaginas: 0,
    totalRegistros: 0,
    registrosPorPagina: 10
  });

  // Función para determinar si un catálogo requiere parentId específico
  const needsParentId = useCallback((catalogKey) => {
    return catalogKey === 'municipios';
  }, []);

  // Nombre del departamento seleccionado
  const selectedDepartamentoNombre = useMemo(() => {
    if (!Array.isArray(departamentos) || departamentos.length === 0) {
      return '';
    }
    return departamentos.find(d => d.codigo === selectedDepartamento)?.descripcion || '';
  }, [departamentos, selectedDepartamento]);

  // Función para cargar departamentos
  const loadDepartamentos = useCallback(async () => {
    setLoadingDepartamentos(true);
    try {
      const result = await fetchCatalog('departamentos', "1");

      console.log('Resultado de departamentos:', result);

      let departamentosData = [];

      if (result && result.entity && Array.isArray(result.entity)) {
        departamentosData = result.entity;
      } else if (Array.isArray(result)) {
        departamentosData = result;
      }

      console.log('Departamentos procesados:', departamentosData);
      setDepartamentos(departamentosData);

      if (departamentosData.length > 0) {
        setSelectedDepartamento((prev) => prev || departamentosData[0]?.codigo || '');
      }

    } catch (err) {
      console.error('Error al cargar departamentos:', err);
      setDepartamentos([]);
    } finally {
      setLoadingDepartamentos(false);
    }
  }, []);

  // Función principal para cargar catálogo
  const loadCatalog = useCallback(async () => {
    setLoading(true);
    setCatalogData([]);
    setError(null);

    try {
      let parentId = "1";
      if (needsParentId(selectedCatalog)) {
        parentId = selectedDepartamento || "DEP_1";
      }

      // Convertir a índice base 0 para el servidor (UI base 1 -> servidor base 0)
      const result = await fetchCatalog(
        selectedCatalog,
        parentId,
        serverPagination.paginaActual - 1, // UI página 1 = servidor página 0
        serverPagination.registrosPorPagina
      );

      console.log('Resultado completo de fetchCatalog:', result);

      if (result && typeof result === 'object') {
        if (result.entity && result.paginacionInfo) {
          setCatalogData(result.entity || []);
          setServerPagination(prev => ({
            ...prev,
            // Convertir respuesta del servidor (base 0) a UI (base 1)
            paginaActual: (result.paginacionInfo.paginaActual || 0) + 1, // servidor página 0 = UI página 1
            totalPaginas: result.paginacionInfo.totalPaginas || 1,
            totalRegistros: result.paginacionInfo.totalRegistros || 0,
            registrosPorPagina: result.paginacionInfo.registrosPorPagina || prev.registrosPorPagina
          }));
        } else if (Array.isArray(result)) {
          setCatalogData(result);
        } else if (result.data) {
          setCatalogData(result.data || []);
          if (result.paginacionInfo) {
            setServerPagination(prev => ({
              ...prev,
              paginaActual: (result.paginacionInfo.paginaActual || 0) + 1,
              totalPaginas: result.paginacionInfo.totalPaginas || 1,
              totalRegistros: result.paginacionInfo.totalRegistros || 0,
              registrosPorPagina: result.paginacionInfo.registrosPorPagina || prev.registrosPorPagina
            }));
          }
        }
      } else {
        setCatalogData([]);
      }

    } catch (err) {
      console.error('Error en loadCatalog:', err);
      setError('Error al cargar datos: ' + (err.message || 'Error desconocido'));
    } finally {
      setLoading(false);
    }
  }, [selectedCatalog, selectedDepartamento, needsParentId, serverPagination.paginaActual, serverPagination.registrosPorPagina]);

  // Cargar catálogo cuando cambia la selección
  useEffect(() => {
    if (needsParentId(selectedCatalog)) {
      loadDepartamentos();
    } else {
      loadCatalog();
    }
  }, [selectedCatalog, loadDepartamentos, loadCatalog, needsParentId]);

  // Cargar municipios cuando cambia el departamento seleccionado
  useEffect(() => {
    if (needsParentId(selectedCatalog) && selectedDepartamento) {
      loadCatalog();
    }
  }, [selectedDepartamento, selectedCatalog, loadCatalog, needsParentId]);

  // Función para agregar un nuevo elemento al catálogo
  const handleAddItem = async (e) => {
    e.preventDefault();

    if (!newItemDescription.trim()) {
      setAddResult({
        success: false,
        message: 'La descripción no puede estar vacía'
      });
      return;
    }

    setAddingItem(true);
    setAddResult(null);

    try {
      let parentId = "1";
      if (needsParentId(selectedCatalog)) {
        parentId = selectedDepartamento || "DEP_1";
      }

      const result = await addCatalogItem(selectedCatalog, newItemDescription, parentId);

      setAddResult(result);

      if (result.success) {
        setNewItemDescription('');
        setTimeout(() => {
          setShowModal(false);
          loadCatalog();
        }, 1500);
      }

    } catch (err) {
      setAddResult({
        success: false,
        message: err.message || 'Error al agregar el registro'
      });
    } finally {
      setAddingItem(false);
    }
  };

  // Función para abrir el modal de edición
  const handleEditClick = (item) => {
    setEditingItem(item);
    setEditDescription(item.descripcion);
    setShowEditModal(true);
    setUpdateResult(null);
  };

  // Función para actualizar un elemento del catálogo
  const handleUpdateItem = async (e) => {
    e.preventDefault();

    if (!editDescription.trim()) {
      setUpdateResult({
        success: false,
        message: 'La descripción no puede estar vacía'
      });
      return;
    }

    setUpdatingItem(true);
    setUpdateResult(null);

    try {
      const result = await updateCatalogItem(editingItem.codigo, editDescription);

      setUpdateResult(result);

      if (result.success) {
        setTimeout(() => {
          setShowEditModal(false);
          setEditingItem(null);
          setEditDescription('');
          loadCatalog();
        }, 1500);
      }

    } catch (err) {
      setUpdateResult({
        success: false,
        message: err.message || 'Error al actualizar el registro'
      });
    } finally {
      setUpdatingItem(false);
    }
  };

  // Función para abrir el modal de confirmación de eliminación
  const handleDeleteClick = (item) => {
    setItemToDelete(item);
    setShowDeleteModal(true);
    setDeleteResult(null);
  };

  // Función para eliminar un elemento del catálogo
  const handleDeleteItem = async () => {
    setDeletingItem(true);
    setDeleteResult(null);

    try {
      const result = await deleteCatalogItem(itemToDelete.codigo);

      setDeleteResult(result);

      if (result.success) {
        setTimeout(() => {
          setShowDeleteModal(false);
          setItemToDelete(null);
          loadCatalog();
        }, 1500);
      }

    } catch (err) {
      setDeleteResult({
        success: false,
        message: err.message || 'Error al eliminar el registro'
      });
    } finally {
      setDeletingItem(false);
    }
  };

  // Manejar cambio de página
  const handlePageChange = (pageNumber) => {
    if (pageNumber < 1 || pageNumber > serverPagination.totalPaginas) return;
    setServerPagination(prev => ({
      ...prev,
      paginaActual: pageNumber
    }));
  };

  // Manejar cambio de registros por página
  const handleItemsPerPageChange = (e) => {
    const newItemsPerPage = parseInt(e.target.value);
    setServerPagination(prev => ({
      ...prev,
      registrosPorPagina: newItemsPerPage,
      paginaActual: 1 // Resetear a la primera página
    }));
  };

  return (
    <div className="d-flex" style={{ height: 'calc(100vh - 160px)' }}>
      <Sidenav onSelectCatalog={setSelectedCatalog} />
      <div
        className="flex-grow-1 d-flex flex-column"
        style={{
          height: '100%',
          overflow: 'hidden'
        }}
      >
        {/* Header fijo */}
        <div className="px-4 py-3 border-bottom bg-white">
          <h1 className="mb-0 text-capitalize">{selectedCatalog === 'catalogos' ? 'Catálogos' : selectedCatalog}</h1>
        </div>

        {/* Contenido con scroll */}
        <div
          className="flex-grow-1 px-4 py-3"
          style={{
            overflowY: 'auto',
            height: '100%'
          }}
        >
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
            </div>
          )}

          {!loading && !error && selectedCatalog !== 'catalogos' && (
            <>
              {/* Selector de departamentos cuando se selecciona municipios */}
              {needsParentId(selectedCatalog) && (
                <div className="mb-3">
                  <label htmlFor="departamentoSelect" className="form-label">Filtrar por Departamento:</label>
                  <select
                    id="departamentoSelect"
                    className="form-select"
                    value={selectedDepartamento}
                    onChange={(e) => setSelectedDepartamento(e.target.value)}
                    disabled={loadingDepartamentos}
                  >
                    {loadingDepartamentos ? (
                      <option>Cargando departamentos...</option>
                    ) : (
                      Array.isArray(departamentos) && departamentos.length > 0 ? (
                        departamentos.map((dept) => (
                          <option key={dept.codigo} value={dept.codigo}>
                            {dept.descripcion}
                          </option>
                        ))
                      ) : (
                        <option value="">No hay departamentos disponibles</option>
                      )
                    )}
                  </select>
                </div>
              )}

              {/* Botón para agregar nuevo registro */}
               <div className="d-flex justify-content-between align-items-center mb-3">
                <button
                  className="btn btn-primary"
                  onClick={() => setShowModal(true)}
                >
                  <i className="bi bi-plus-circle me-2"></i>
                  Nuevo Registro
                </button>
              </div>

              {/* Información de registros */}
              {/* Información de registros */}
              <div className="mb-3 small text-muted">
                <i className="bi bi-info-circle me-1"></i>
                {catalogData && catalogData.length > 0 ?
                  `Total de registros: ${catalogData.length} de ${serverPagination.totalRegistros}` :
                  'No hay datos para mostrar'}
                {needsParentId(selectedCatalog) && selectedDepartamento && departamentos.length > 0 && (
                  <span className="ms-2">
                    - Departamento: {selectedDepartamentoNombre}
                  </span>
                )}
              </div>

              {/* Tabla de registros */}
              <div className="table-responsive">
                <table className="table table-bordered table-hover mb-0">
                  <thead className="table-dark">
                    <tr>
                      <th style={{ width: '120px', minWidth: '120px' }}>
                        <i className="bi bi-hash me-1"></i>Código
                      </th>
                      <th style={{ minWidth: '200px' }}>
                        <i className="bi bi-text-left me-1"></i>Descripción
                      </th>
                      <th style={{ width: '160px', minWidth: '160px' }}>
                        <i className="bi bi-gear me-1"></i>Acciones
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {catalogData && catalogData.length > 0 ? (
                      catalogData.map((item, index) => (
                        <tr key={item.codigo || index}>
                          <td>
                            <span className="badge bg-light text-dark">
                              {item.codigo}
                            </span>
                          </td>
                          <td>
                            <div className="d-flex align-items-center">
                              <i className="bi bi-tag me-2 text-muted"></i>
                              <span>{item.descripcion}</span>
                            </div>
                          </td>
                          <td>
                            <div className="btn-group" role="group">
                              <button
                                className="btn btn-sm btn-outline-primary"
                                title="Editar registro"
                                onClick={() => handleEditClick(item)}
                              >
                                <i className="bi bi-pencil-square"></i>
                              </button>
                              <button
                                className="btn btn-sm btn-outline-danger"
                                title="Eliminar registro"
                                onClick={() => handleDeleteClick(item)}
                              >
                                <i className="bi bi-trash"></i>
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan="3" className="text-center py-4 text-muted">
                          <i className="bi bi-folder-x fs-1 mb-3 d-block text-muted"></i>
                          No hay datos disponibles
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </>
          )}

          {/* Mostrar una página de inicio cuando se selecciona 'catalogos' */}
          {!loading && !error && selectedCatalog === 'catalogos' && (
            <div className="text-center my-5">
              <div className="display-4 mb-4">Sistema de Mantenimiento de Catálogos</div>
              <p className="lead">
                Seleccione un catálogo del menú lateral para comenzar a administrarlo.
              </p>
            </div>
          )}
        </div>

        {/* Paginación fija en la parte inferior */}
        {!loading && !error && selectedCatalog !== 'catalogos' && catalogData && catalogData.length > 0 && (
          <div className="px-4 py-3 border-top bg-white">
            <div className="d-flex justify-content-between align-items-center">
              <div>
                Mostrar
                <select
                  className="form-select form-select-sm d-inline-block mx-2"
                  style={{ width: "80px" }}
                  value={serverPagination.registrosPorPagina}
                  onChange={handleItemsPerPageChange}
                >
                  <option value="5">5</option>
                  <option value="10">10</option>
                  <option value="15">15</option>
                  <option value="20">20</option>
                  <option value="25">25</option>
                </select>
                registros por página
              </div>

              <div>
                Página {serverPagination.paginaActual} de {serverPagination.totalPaginas}
                <div className="btn-group ms-2">
                  <button
                    className="btn btn-sm btn-outline-secondary"
                    onClick={() => handlePageChange(serverPagination.paginaActual - 1)}
                    disabled={serverPagination.paginaActual === 1}
                  >
                    &lt;
                  </button>
                  <button
                    className="btn btn-sm btn-outline-secondary"
                    onClick={() => handlePageChange(serverPagination.paginaActual + 1)}
                    disabled={serverPagination.paginaActual === serverPagination.totalPaginas || serverPagination.totalPaginas === 0}
                  >
                    &gt;
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Modal para agregar nuevo registro */}
        {showModal && (
          <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
            <div className="modal-dialog">
              <div className="modal-content">
                <div className="modal-header">
                  <h5 className="modal-title">Agregar nuevo {selectedCatalog}</h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={() => {
                      setShowModal(false);
                      setNewItemDescription('');
                      setAddResult(null);
                    }}
                    disabled={addingItem}
                  ></button>
                </div>
                <div className="modal-body">
                  {addResult && (
                    <div className={`alert ${addResult.success ? 'alert-success' : 'alert-danger'}`}>
                      {addResult.message}
                    </div>
                  )}

                  <form onSubmit={handleAddItem}>
                    <div className="mb-3">
                      <label htmlFor="newItemDescription" className="form-label">Descripción</label>
                      <input
                        type="text"
                        className="form-control"
                        id="newItemDescription"
                        placeholder={`Ingrese descripción para el nuevo ${selectedCatalog}`}
                        value={newItemDescription}
                        onChange={(e) => setNewItemDescription(e.target.value)}
                        disabled={addingItem}
                        autoFocus
                      />
                    </div>
                  </form>
                </div>
                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => {
                      setShowModal(false);
                      setNewItemDescription('');
                      setAddResult(null);
                    }}
                    disabled={addingItem}
                  >
                    Cancelar
                  </button>
                  <button
                    type="button"
                    className="btn btn-primary"
                    onClick={handleAddItem}
                    disabled={addingItem || !newItemDescription.trim()}
                  >
                    {addingItem ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                        Guardando...
                      </>
                    ) : 'Guardar'}
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Modal para editar registro */}
        {showEditModal && (
          <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
            <div className="modal-dialog">
              <div className="modal-content">
                <div className="modal-header">
                  <h5 className="modal-title">Editar {selectedCatalog}</h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={() => {
                      setShowEditModal(false);
                      setEditingItem(null);
                      setEditDescription('');
                      setUpdateResult(null);
                    }}
                    disabled={updatingItem}
                  ></button>
                </div>
                <div className="modal-body">
                  {updateResult && (
                    <div className={`alert ${updateResult.success ? 'alert-success' : 'alert-danger'}`}>
                      {updateResult.message}
                    </div>
                  )}

                  <form onSubmit={handleUpdateItem}>
                    <div className="mb-3">
                      <label htmlFor="editCode" className="form-label">Código</label>
                      <input
                        type="text"
                        className="form-control"
                        id="editCode"
                        value={editingItem?.codigo || ''}
                        disabled
                      />
                    </div>
                    <div className="mb-3">
                      <label htmlFor="editDescription" className="form-label">Descripción</label>
                      <input
                        type="text"
                        className="form-control"
                        id="editDescription"
                        placeholder="Ingrese la nueva descripción"
                        value={editDescription}
                        onChange={(e) => setEditDescription(e.target.value)}
                        disabled={updatingItem}
                        autoFocus
                      />
                    </div>
                  </form>
                </div>
                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => {
                      setShowEditModal(false);
                      setEditingItem(null);
                      setEditDescription('');
                      setUpdateResult(null);
                    }}
                    disabled={updatingItem}
                  >
                    Cancelar
                  </button>
                  <button
                    type="button"
                    className="btn btn-primary"
                    onClick={handleUpdateItem}
                    disabled={updatingItem || !editDescription.trim()}
                  >
                    {updatingItem ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                        Actualizando...
                      </>
                    ) : 'Actualizar'}
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Modal para confirmación de eliminación */}
        {showDeleteModal && (
          <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
            <div className="modal-dialog">
              <div className="modal-content">
                <div className="modal-header">
                  <h5 className="modal-title">Confirmar Eliminación</h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={() => {
                      setShowDeleteModal(false);
                      setItemToDelete(null);
                      setDeleteResult(null);
                    }}
                    disabled={deletingItem}
                  ></button>
                </div>
                <div className="modal-body">
                  {deleteResult && (
                    <div className={`alert ${deleteResult.success ? 'alert-success' : 'alert-danger'}`}>
                      {deleteResult.message}
                    </div>
                  )}

                  <p>¿Está seguro que desea eliminar el siguiente registro?</p>
                  <div className="card">
                    <div className="card-body">
                      <strong>Código:</strong> {itemToDelete?.codigo}<br />
                      <strong>Descripción:</strong> {itemToDelete?.descripcion}
                    </div>
                  </div>
                  <p className="text-danger mt-2">
                    <i className="fas fa-exclamation-triangle"></i> Esta acción no se puede deshacer.
                  </p>
                </div>
                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => {
                      setShowDeleteModal(false);
                      setItemToDelete(null);
                      setDeleteResult(null);
                    }}
                    disabled={deletingItem}
                  >
                    Cancelar
                  </button>
                  <button
                    type="button"
                    className="btn btn-danger"
                    onClick={handleDeleteItem}
                    disabled={deletingItem}
                  >
                    {deletingItem ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                        Eliminando...
                      </>
                    ) : 'Eliminar'}
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MaintenancePage;