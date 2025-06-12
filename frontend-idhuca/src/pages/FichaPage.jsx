import React, { useState, useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import { Edit3, FileText, Calendar, User, ChevronLeft, ChevronRight, Loader2, AlertCircle, Trash2, Upload, X } from 'lucide-react';
import { fetchFichasByDerecho, createFicha, deleteFicha, formatDate, generateDerechoCodigo, getDerechoDescripcion } from '../services/FichaDerechoService';
import ExpresionIcon from '../assets/icons/expresion.png';
import LibertadIcon from '../assets/icons/libertad.png';
import JusticiaIcon from '../assets/icons/justicia.png';
import VidaIcon from '../assets/icons/vida.png';

const FichaDerechoView = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const [filterDate, setFilterDate] = useState('');
  const [filterDateEnd, setFilterDateEnd] = useState('');
  const [showCount, setShowCount] = useState(10);
  const [newPost, setNewPost] = useState('');
  const [newTitle, setNewTitle] = useState('');
  const [entries, setEntries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [totalEntries, setTotalEntries] = useState(0);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [itemToDelete, setItemToDelete] = useState(null);
  const [deleteResult, setDeleteResult] = useState(null);
  const [selectedFiles, setSelectedFiles] = useState([]);
  const fileInputRef = useRef(null);

  const location = useLocation();
  const derechoId = location.state?.derechoId;
  const derechoTitle = location.state?.derechoTitle;

  const getDerechoIcon = (derechoId) => {
    const icons = {
      1: <img src={LibertadIcon} alt="Libertad Personal e Integridad" width="48" height="48" />,
      2: <img src={ExpresionIcon} alt="Libertad de Expresión" width="48" height="48" />,
      3: <img src={JusticiaIcon} alt="Acceso a la Justicia" width="48" height="48" />,
      4: <img src={VidaIcon} alt="Derecho a la Vida" width="48" height="48" />
    };
    return icons[derechoId] || icons[1];
  };

  useEffect(() => {
    if (derechoId) {
      loadFichas();
    }
  }, [derechoId]);

  const loadFichas = async () => {
    try {
      setLoading(true);
      setError(null);

      const derechoCodigo = generateDerechoCodigo(derechoId);
      const response = await fetchFichasByDerecho(derechoCodigo);

      if (response.success) {
        const transformedEntries = response.data.map(ficha => ({
          id: ficha.id,
          title: ficha.titulo,
          creator: ficha.creadoPor?.nombre || 'Usuario desconocido',
          content: ficha.descripcion,
          attachments: ficha.archivos?.map(archivo => archivo.nombreOriginal) || [],
          creationDate: formatDate(ficha.creadoEn),
          lastModified: formatDate(ficha.modificadoEn),
          fecha: ficha.fecha,
          derechoCodigo: ficha.derechoCodigo,
          creadoPor: ficha.creadoPor,
          modificadoPor: ficha.modificadoPor,
          archivos: ficha.archivos
        }));

        setEntries(transformedEntries);
        setTotalEntries(transformedEntries.length);
      } else {
        setError(response.message || 'Error al cargar las fichas');
      }
    } catch (error) {
      console.error('Error al cargar fichas:', error);
      setError(error.message || 'Error al cargar las fichas del derecho');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteClick = (ficha) => {
    setItemToDelete(ficha);
    setShowDeleteModal(true);
    setDeleteResult(null);
  };

  const handleDeleteFicha = async () => {
    setDeleting(itemToDelete.id);
    setDeleteResult(null);

    try {
      const response = await deleteFicha(itemToDelete.id);

      if (response.success) {
        setDeleteResult({
          success: true,
          message: 'Ficha eliminada exitosamente'
        });

        setTimeout(async () => {
          setShowDeleteModal(false);
          setItemToDelete(null);
          await loadFichas();

          const newTotalEntries = entries.length - 1;
          const newTotalPages = Math.ceil(newTotalEntries / showCount);
          if (currentPage > newTotalPages && newTotalPages > 0) {
            setCurrentPage(newTotalPages);
          }
        }, 1500);
      } else {
        setDeleteResult({
          success: false,
          message: response.message || 'Error al eliminar la ficha'
        });
      }
    } catch (error) {
      console.error('Error al eliminar ficha:', error);
      setDeleteResult({
        success: false,
        message: error.message || 'Error al eliminar la ficha'
      });
    } finally {
      setDeleting(null);
    }
  };

  const handleFileSelect = (event) => {
    const files = Array.from(event.target.files);
    const newFiles = files.map(file => ({
      id: Date.now() + Math.random(),
      file: file,
      name: file.name,
      size: file.size,
      tipo: 'documento'
    }));

    setSelectedFiles(prev => [...prev, ...newFiles]);
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const removeFile = (fileId) => {
    setSelectedFiles(prev => prev.filter(file => file.id !== fileId));
  };

  const changeFileType = (fileId, newType) => {
    setSelectedFiles(prev =>
      prev.map(file =>
        file.id === fileId ? { ...file, tipo: newType } : file
      )
    );
  };

  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const getFilteredEntries = () => {
    let filtered = [...entries];

    if (filterDate) {
      filtered = filtered.filter(entry => {
        const entryDate = new Date(entry.fecha);
        const filterDateObj = new Date(filterDate);
        return entryDate >= filterDateObj;
      });
    }

    if (filterDateEnd) {
      filtered = filtered.filter(entry => {
        const entryDate = new Date(entry.fecha);
        const filterDateEndObj = new Date(filterDateEnd);
        return entryDate <= filterDateEndObj;
      });
    }

    filtered.sort((a, b) => new Date(b.fecha) - new Date(a.fecha));
    return filtered;
  };

  const filteredEntries = getFilteredEntries();
  const startIndex = (currentPage - 1) * showCount;
  const endIndex = startIndex + showCount;
  const currentEntries = filteredEntries.slice(startIndex, endIndex);
  const totalPages = Math.ceil(filteredEntries.length / showCount);

  const handleSaveNewPost = async () => {
    if (!newTitle.trim() || !newPost.trim()) {
      alert('Por favor ingresa tanto el título como el contenido para la nueva ficha');
      return;
    }

    try {
      setSaving(true);

      const fichaData = {
        derechoCodigo: generateDerechoCodigo(derechoId),
        derechoDescripcion: getDerechoDescripcion(derechoId),
        titulo: newTitle.trim(),
        descripcion: newPost.trim(),
        fecha: new Date().toISOString()
      };

      const response = await createFicha(fichaData, selectedFiles);

      if (response.success) {
        setNewTitle('');
        setNewPost('');
        setSelectedFiles([]);
        await loadFichas();
        alert('Ficha creada exitosamente');
      } else {
        alert(response.message || 'Error al crear la ficha');
      }
    } catch (error) {
      console.error('Error al crear ficha:', error);
      alert(error.message || 'Error al crear la ficha');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ height: 'calc(100vh - 200px)' }}>
        <div className="text-center">
          <Loader2 size={48} className="text-primary mb-3" style={{ animation: 'spin 1s linear infinite' }} />
          <p className="text-muted">Cargando fichas del derecho...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ height: 'calc(100vh - 200px)' }}>
        <div className="alert alert-danger d-flex align-items-center" role="alert">
          <AlertCircle size={24} className="me-2" />
          <div>
            <strong>Error:</strong> {error}
            <br />
            <button className="btn btn-sm btn-outline-danger mt-2" onClick={loadFichas}>
              Intentar nuevamente
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <>
      <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.2/css/bootstrap.min.css" rel="stylesheet" />

      {/* Contenedor principal con altura fija considerando navbar y footer */}
      <div className="container-fluid" style={{ height: 'calc(100vh - 200px)', overflow: 'hidden' }}>
        {/* Header compacto */}
        <div className="row py-2">
          <div className="col-12 text-center">
            <h2 className="h4 text-dark mb-0">Ficha del derecho</h2>
          </div>
        </div>

        {/* Contenido principal con scroll */}
        <div className="row" style={{ height: 'calc(100% - 60px)' }}>
          {/* Columna izquierda - Información del derecho */}
          <div className="col-md-3" style={{ height: '100%' }}>
            <div className="card h-100">
              <div className="card-body d-flex flex-column p-3">
                {/* Icono más pequeño */}
                <div className="text-center mb-2">
                  {derechoId ? getDerechoIcon(derechoId) : getDerechoIcon(1)}
                </div>

                {/* Título compacto */}
                <div className="text-center mb-2">
                  <h4 className="h6 text-primary fw-semibold mb-2">
                    {derechoTitle || 'Derecho a la Libertad Personal e Integridad personal'}
                  </h4>
                </div>

                {/* Descripción más compacta */}
                <div className="mb-2 flex-grow-1">
                  <p className="text-muted small lh-sm">
                    Protege contra detenciones arbitrarias y garantiza el debido proceso...
                    Protege contra detenciones arbitrarias y garantiza el debido proceso...
                    Protege contra detenciones arbitrarias y garantiza el debido proceso...
                    Protege contra detenciones arbitrarias y garantiza el debido proceso...
                    Protege contra detenciones arbitrarias y garantiza el debido proceso...
                  </p>
                </div>

                {/* Investigador */}
                <div className="text-center border-top pt-2">
                  <h6 className="small fw-semibold text-dark mb-1">Investigador:</h6>
                  <p className="text-primary small mb-0">Mauricio Erazo</p>
                </div>
              </div>
            </div>
          </div>

          {/* Columna derecha - Lista de fichas */}
          <div className="col-md-9" style={{ height: '100%' }}>
            <div className="card h-100 d-flex flex-column">
              {/* Filtros compactos */}
              <div className="card-header bg-white border-bottom py-2">
                <div className="row g-2 align-items-center">
                  <div className="col-auto">
                    <label className="form-label small mb-0 text-muted">Filtrar:</label>
                  </div>
                  <div className="col-auto">
                    <input
                      type="date"
                      value={filterDate}
                      onChange={(e) => setFilterDate(e.target.value)}
                      className="form-control form-control-sm"
                      style={{ fontSize: '10px', width: '130px' }}
                    />
                  </div>
                  <div className="col-auto">
                    <span className="small text-muted">a</span>
                  </div>
                  <div className="col-auto">
                    <input
                      type="date"
                      value={filterDateEnd}
                      onChange={(e) => setFilterDateEnd(e.target.value)}
                      className="form-control form-control-sm"
                      style={{ fontSize: '10px', width: '130px' }}
                    />
                  </div>
                  <div className="col-auto">
                    <select
                      className="form-select form-select-sm"
                      value={showCount}
                      onChange={(e) => {
                        setShowCount(Number(e.target.value));
                        setCurrentPage(1);
                      }}
                      style={{ width: '90px' }}
                    >
                      <option value={5}>5</option>
                      <option value={10}>10</option>
                      <option value={25}>25</option>
                    </select>
                  </div>
                  <div className="col-auto">
                    <div className="d-flex align-items-center bg-light px-2 py-1 rounded">
                      <span className="fw-semibold text-primary me-1 small">{filteredEntries.length}</span>
                      <span className="small text-dark">entradas</span>
                    </div>
                  </div>
                  <div className="col-auto ms-auto">
                    <span className="small text-muted">
                      Pág {currentPage}/{totalPages || 1}
                    </span>
                  </div>
                </div>
              </div>

              {/* Lista de entradas - área scrolleable principal */}
              <div className="card-body p-2" style={{ 
                flex: '1 1 auto', 
                overflow: 'auto',
                minHeight: '0'
              }}>
                {currentEntries.length === 0 ? (
                  <div className="text-center py-4">
                    <FileText size={32} className="text-muted mb-2" />
                    <p className="text-muted small">No se encontraron entradas</p>
                  </div>
                ) : (
                  <div className="d-flex flex-column gap-2">
                    {currentEntries.map((entry) => (
                      <div key={entry.id} className="card border-0 shadow-sm">
                        <div className="card-body p-2" style={{ backgroundColor: '#f8f9fa' }}>
                          <div className="d-flex justify-content-between align-items-start mb-1">
                            <h6 className="card-title mb-0 fw-semibold text-dark small">
                              {entry.title}
                            </h6>
                            <div className="d-flex align-items-center gap-1">
                              <button className="btn p-0 border-0 bg-transparent" title="Editar">
                                <Edit3 size={14} className="text-muted" />
                              </button>
                              <button
                                className="btn p-0 border-0 bg-transparent"
                                onClick={() => handleDeleteClick(entry)}
                                disabled={deleting === entry.id}
                                title="Eliminar"
                              >
                                {deleting === entry.id ? (
                                  <Loader2 size={14} className="text-danger" style={{ animation: 'spin 1s linear infinite' }} />
                                ) : (
                                  <Trash2 size={14} className="text-danger" />
                                )}
                              </button>
                            </div>
                          </div>

                          <p className="small text-dark mb-2 lh-sm">
                            {entry.content.length > 150 ? entry.content.substring(0, 150) + '...' : entry.content}
                          </p>

                          {entry.attachments && entry.attachments.length > 0 && (
                            <div className="mb-2">
                              <div className="d-flex align-items-center gap-1 flex-wrap">
                                <FileText size={12} className="text-muted" />
                                <span className="small text-muted">Archivos:</span>
                                {entry.attachments.slice(0, 2).map((attachment, index) => (
                                  <a key={index} href="#" className="small text-primary">
                                    {attachment.length > 15 ? attachment.substring(0, 15) + '...' : attachment}
                                  </a>
                                ))}
                                {entry.attachments.length > 2 && (
                                  <span className="small text-muted">+{entry.attachments.length - 2}</span>
                                )}
                              </div>
                            </div>
                          )}

                          <div className="d-flex justify-content-between">
                            <div className="d-flex align-items-center gap-1">
                              <Calendar size={10} className="text-muted" />
                              <span className="small text-muted">{entry.creationDate}</span>
                            </div>
                            <div className="d-flex align-items-center gap-1">
                              <User size={10} className="text-muted" />
                              <span className="small text-muted">{entry.creator}</span>
                            </div>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              {/* Área de nueva entrada - fija al final */}
              <div className="card-footer bg-white border-top p-2" style={{ flexShrink: 0 }}>
                <div className="mb-2">
                  <input
                    type="text"
                    value={newTitle}
                    onChange={(e) => setNewTitle(e.target.value)}
                    className="form-control form-control-sm mb-1"
                    placeholder="Título..."
                    disabled={saving}
                  />
                  <textarea
                    value={newPost}
                    onChange={(e) => setNewPost(e.target.value)}
                    className="form-control form-control-sm mb-1"
                    placeholder="Nuevo contenido..."
                    rows="2"
                    disabled={saving}
                  />

                  {/* Archivos seleccionados - compacto */}
                  {selectedFiles.length > 0 && (
                    <div className="mb-2">
                      <div className="border rounded p-1" style={{ maxHeight: '80px', overflow: 'auto' }}>
                        {selectedFiles.map((file) => (
                          <div key={file.id} className="d-flex align-items-center justify-content-between mb-1 p-1 bg-light rounded">
                            <div className="d-flex align-items-center gap-1 flex-grow-1">
                              <FileText size={12} className="text-primary" />
                              <div className="flex-grow-1">
                                <div className="small text-truncate" style={{ maxWidth: '100px' }}>{file.name}</div>
                              </div>
                              <select
                                className="form-select form-select-sm"
                                style={{ width: '80px', fontSize: '9px' }}
                                value={file.tipo}
                                onChange={(e) => changeFileType(file.id, e.target.value)}
                                disabled={saving}
                              >
                                <option value="documento">Doc</option>
                                <option value="informe">Inf</option>
                                <option value="imagen">Img</option>
                                <option value="otro">Otro</option>
                              </select>
                            </div>
                            <button
                              type="button"
                              className="btn btn-sm btn-outline-danger ms-1"
                              onClick={() => removeFile(file.id)}
                              disabled={saving}
                            >
                              <X size={10} />
                            </button>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  <div className="d-flex gap-1 align-items-center">
                    <input
                      type="file"
                      ref={fileInputRef}
                      onChange={handleFileSelect}
                      className="d-none"
                      multiple
                      disabled={saving}
                    />
                    <button
                      className="btn btn-outline-secondary btn-sm d-flex align-items-center gap-1"
                      onClick={() => fileInputRef.current?.click()}
                      disabled={saving}
                    >
                      <Upload size={12} />
                      Archivos
                    </button>
                    <button
                      className="btn btn-primary btn-sm"
                      onClick={handleSaveNewPost}
                      disabled={saving || !newTitle.trim() || !newPost.trim()}
                    >
                      {saving ? (
                        <>
                          <Loader2 size={12} className="me-1" style={{ animation: 'spin 1s linear infinite' }} />
                          Guardando...
                        </>
                      ) : (
                        'Guardar'
                      )}
                    </button>
                  </div>
                </div>
              </div>

              {/* Paginación fija al final */}
              {totalPages > 1 && (
                <div className="card-footer bg-white border-top p-2 d-flex justify-content-between align-items-center" style={{ flexShrink: 0 }}>
                  <button
                    onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
                    disabled={currentPage === 1}
                    className="btn btn-sm btn-outline-secondary d-flex align-items-center gap-1"
                  >
                    <ChevronLeft size={14} />
                    Ant
                  </button>

                  <div className="d-flex align-items-center gap-1">
                    {[...Array(Math.min(3, totalPages))].map((_, i) => {
                      let pageNum;
                      if (totalPages <= 3) {
                        pageNum = i + 1;
                      } else if (currentPage <= 2) {
                        pageNum = i + 1;
                      } else if (currentPage >= totalPages - 1) {
                        pageNum = totalPages - 2 + i;
                      } else {
                        pageNum = currentPage - 1 + i;
                      }

                      return (
                        <button
                          key={pageNum}
                          onClick={() => setCurrentPage(pageNum)}
                          className={`btn btn-sm ${currentPage === pageNum ? 'btn-primary' : 'btn-outline-secondary'}`}
                          style={{ width: '28px', height: '28px', fontSize: '11px' }}
                        >
                          {pageNum}
                        </button>
                      );
                    })}
                  </div>

                  <button
                    onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
                    disabled={currentPage === totalPages}
                    className="btn btn-sm btn-outline-secondary d-flex align-items-center gap-1"
                  >
                    Sig
                    <ChevronRight size={14} />
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Modal para confirmación de eliminación */}
      {showDeleteModal && (
        <div className="modal show d-block" tabIndex="-1" style={{ backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <div className="modal-dialog modal-sm">
            <div className="modal-content">
              <div className="modal-header py-2">
                <h6 className="modal-title">Confirmar Eliminación</h6>
                <button
                  type="button"
                  className="btn-close"
                  onClick={() => {
                    setShowDeleteModal(false);
                    setItemToDelete(null);
                    setDeleteResult(null);
                  }}
                  disabled={deleting}
                ></button>
              </div>
              <div className="modal-body py-2">
                {deleteResult && (
                  <div className={`alert alert-sm ${deleteResult.success ? 'alert-success' : 'alert-danger'} py-1`}>
                    {deleteResult.message}
                  </div>
                )}

                <p className="small">¿Eliminar esta ficha?</p>
                <div className="card">
                  <div className="card-body p-2">
                    <div className="small">
                      <strong>Título:</strong> {itemToDelete?.title}<br />
                      <strong>Creador:</strong> {itemToDelete?.creator}
                    </div>
                  </div>
                </div>
              </div>
              <div className="modal-footer py-2">
                <button
                  type="button"
                  className="btn btn-sm btn-secondary"
                  onClick={() => {
                    setShowDeleteModal(false);
                    setItemToDelete(null);
                    setDeleteResult(null);
                  }}
                  disabled={deleting}
                >
                  Cancelar
                </button>
                <button
                  type="button"
                  className="btn btn-sm btn-danger"
                  onClick={handleDeleteFicha}
                  disabled={deleting}
                >
                  {deleting ? 'Eliminando...' : 'Eliminar'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default FichaDerechoView;