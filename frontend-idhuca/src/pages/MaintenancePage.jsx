import { useEffect, useState, useCallback, useMemo } from 'react';
import Sidenav from '../components/Sidenav';
import { fetchCatalog, addCatalogItem, updateCatalogItem, deleteCatalogItem } from '../services/CatalogService';

const MaintenancePage = () => {
  const [catalogData, setCatalogData] = useState([]);
  const [selectedCatalog, setSelectedCatalog] = useState('departamentos');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);

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

  // Función para determinar si un catálogo requiere parentId específico
  const needsParentId = useCallback((catalogKey) => {
    return catalogKey === 'municipios';
  }, []);

  const selectedDepartamentoNombre = useMemo(() => {
    return departamentos.find(d => d.codigo === selectedDepartamento)?.descripcion || '';
  }, [departamentos, selectedDepartamento]);

  // Cargar departamentos para el selector
const loadDepartamentos = useCallback(async () => {
  setLoadingDepartamentos(true);
  try {
    const result = await fetchCatalog('departamentos', "1");
    setDepartamentos(result || []);

    // 🛠️ Solo establecer el primero por defecto si no hay ninguno seleccionado
    setSelectedDepartamento((prev) => prev || (result?.[0]?.codigo ?? ''));
  } catch (err) {
    console.error('Error al cargar departamentos:', err);
    setDepartamentos([]);
  } finally {
    setLoadingDepartamentos(false);
  }
}, []);

  // Cargar datos del catálogo seleccionado
  const loadCatalog = useCallback(async () => {
    setLoading(true);
    setCatalogData([]); // Limpiar datos anteriores
    setCurrentPage(1); // Resetear a la primera página
    setError(null); // Limpiar errores anteriores

    try {
      // Determinar el parentId adecuado según el catálogo
      let parentId = "1";
      if (needsParentId(selectedCatalog)) {
        parentId = selectedDepartamento || "DEP_1";
      }

      // Obtener datos del servicio
      const result = await fetchCatalog(selectedCatalog, parentId);

      // Imprimir para depuración
      console.log(`Datos de ${selectedCatalog} recibidos:`, result);

      // Actualizar el estado con los datos recibidos
      setCatalogData(result || []);
    } catch (err) {
      setError('Error al cargar datos: ' + (err.message || 'Error desconocido'));
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [selectedCatalog, selectedDepartamento, needsParentId]);

  // Cargar catálogo cuando cambia la selección
  useEffect(() => {
    if (needsParentId(selectedCatalog)) {
      // Si se selecciona un catálogo que necesita parentId, primero cargar departamentos
      loadDepartamentos();
    } else {
      // Para otros catálogos, cargar directamente
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

    // Validar entrada
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
      // Determinar el parentId adecuado según el catálogo
      let parentId = "1";
      if (needsParentId(selectedCatalog)) {
        parentId = selectedDepartamento || "DEP_1";
      }

      // Llamar al servicio para agregar el elemento
      const result = await addCatalogItem(selectedCatalog, newItemDescription, parentId);

      // Establecer resultado
      setAddResult(result);

      // Si fue exitoso, limpiar el campo y cerrar el modal después de un breve retraso
      if (result.success) {
        setNewItemDescription('');
        setTimeout(() => {
          setShowModal(false);
          loadCatalog(); // Recargar el catálogo para mostrar el nuevo elemento
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

    // Validar entrada
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
      // Llamar al servicio para actualizar el elemento
      const result = await updateCatalogItem(editingItem.codigo, editDescription);

      // Establecer resultado
      setUpdateResult(result);

      // Si fue exitoso, cerrar el modal después de un breve retraso
      if (result.success) {
        setTimeout(() => {
          setShowEditModal(false);
          setEditingItem(null);
          setEditDescription('');
          loadCatalog(); // Recargar el catálogo para mostrar los cambios
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
      // Llamar al servicio para eliminar el elemento
      const result = await deleteCatalogItem(itemToDelete.codigo);

      // Establecer resultado
      setDeleteResult(result);

      // Si fue exitoso, cerrar el modal después de un breve retraso
      if (result.success) {
        setTimeout(() => {
          setShowDeleteModal(false);
          setItemToDelete(null);
          loadCatalog(); // Recargar el catálogo para mostrar los cambios
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

  // Calcular datos paginados
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = catalogData.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(catalogData.length / itemsPerPage);

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
                      departamentos.map((dept) => (
                        <option key={dept.codigo} value={dept.codigo}>
                          {dept.descripcion}
                        </option>
                      ))
                    )}
                  </select>
                </div>
              )}

              {/* Botón para agregar nuevo registro */}
              <div className="mb-3">
                <button
                  className="btn btn-primary"
                  onClick={() => setShowModal(true)}
                >
                  <i className="fas fa-plus me-1"></i> Nuevo Registro
                </button>
              </div>

              {/* Información de registros */}
              <div className="mb-3 small text-muted">
                {catalogData && catalogData.length > 0 ?
                  `Mostrando ${Math.min(currentItems.length, itemsPerPage)} de ${catalogData.length} registros` :
                  'No hay datos para mostrar'}
                {needsParentId(selectedCatalog) && selectedDepartamento && departamentos.length > 0 && (
                  <span className="ms-2">
                    - Departamento: {selectedDepartamentoNombre}
                  </span>
                )}
              </div>

              {/* Tabla de registros */}
              <div className="table-responsive">
                <table className="table table-bordered table-hover">
                  <thead className="table-dark">
                    <tr>
                      <th>Código</th>
                      <th>Descripción</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {currentItems && currentItems.length > 0 ? (
                      currentItems.map((item, index) => (
                        <tr key={item.codigo || index}>
                          <td>{item.codigo}</td>
                          <td>{item.descripcion}</td>
                          <td>
                            <div className="btn-group">
                              <button
                                className="btn btn-sm btn-warning"
                                onClick={() => handleEditClick(item)}
                              >
                                <i className="fas fa-edit"></i> Editar
                              </button>
                              <button
                                className="btn btn-sm btn-danger ms-1"
                                onClick={() => handleDeleteClick(item)}
                              >
                                <i className="fas fa-trash"></i> Eliminar
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan="3" className="text-center">No hay datos disponibles</td>
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
                  value={itemsPerPage}
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
                Página {currentPage} de {totalPages}
                <div className="btn-group ms-2">
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