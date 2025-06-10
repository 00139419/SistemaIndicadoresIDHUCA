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
  const [deleting, setDeleting] = useState(null); // Para manejar el estado de eliminación
  
  // Estados para manejo de archivos
  const [selectedFiles, setSelectedFiles] = useState([]);
  const fileInputRef = useRef(null);

  const location = useLocation();
  const derechoId = location.state?.derechoId;
  const derechoTitle = location.state?.derechoTitle;

  // Función para obtener el icono basado en el ID del derecho
  const getDerechoIcon = (derechoId) => {
    const icons = {
      1: <img src={LibertadIcon} alt="Libertad Personal e Integridad" width="64" height="64" />,
      2: <img src={ExpresionIcon} alt="Libertad de Expresión" width="64" height="64" />,
      3: <img src={JusticiaIcon} alt="Acceso a la Justicia" width="64" height="64" />,
      4: <img src={VidaIcon} alt="Derecho a la Vida" width="64" height="64" />
    };
    return icons[derechoId] || icons[1];
  };

  // Cargar fichas al montar el componente
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

  // Función para manejar la eliminación de una ficha
  const handleDeleteFicha = async (fichaId, fichaTitle) => {
    // Mostrar confirmación
    const confirmDelete = window.confirm(
      `¿Estás seguro de que deseas eliminar la ficha "${fichaTitle}"?\n\nEsta acción no se puede deshacer.`
    );
    
    if (!confirmDelete) {
      return;
    }

    try {
      setDeleting(fichaId);
      
      const response = await deleteFicha(fichaId);
      
      if (response.success) {
        // Recargar las fichas después de eliminar
        await loadFichas();
        
        // Ajustar la página actual si es necesario
        const newTotalEntries = entries.length - 1;
        const newTotalPages = Math.ceil(newTotalEntries / showCount);
        if (currentPage > newTotalPages && newTotalPages > 0) {
          setCurrentPage(newTotalPages);
        }
        
        alert('Ficha eliminada exitosamente');
      } else {
        alert(response.message || 'Error al eliminar la ficha');
      }
    } catch (error) {
      console.error('Error al eliminar ficha:', error);
      alert(error.message || 'Error al eliminar la ficha');
    } finally {
      setDeleting(null);
    }
  };

  // Manejar selección de archivos
  const handleFileSelect = (event) => {
    const files = Array.from(event.target.files);
    const newFiles = files.map(file => ({
      id: Date.now() + Math.random(),
      file: file,
      name: file.name,
      size: file.size,
      tipo: 'documento' // Tipo por defecto
    }));
    
    setSelectedFiles(prev => [...prev, ...newFiles]);
    // Limpiar el input para permitir seleccionar el mismo archivo nuevamente
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  // Remover archivo seleccionado
  const removeFile = (fileId) => {
    setSelectedFiles(prev => prev.filter(file => file.id !== fileId));
  };

  // Cambiar tipo de archivo
  const changeFileType = (fileId, newType) => {
    setSelectedFiles(prev => 
      prev.map(file => 
        file.id === fileId ? { ...file, tipo: newType } : file
      )
    );
  };

  // Formatear tamaño de archivo
  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  // Filtrar entradas solo por fecha
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

  // Manejar creación de nueva ficha
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
      <div className="container-fluid" style={{ backgroundColor: '#f8f9fa', minHeight: 'calc(100vh - 200px)', paddingTop: '20px' }}>
        <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '50vh' }}>
          <div className="text-center">
            <Loader2 size={48} className="text-primary mb-3" style={{ animation: 'spin 1s linear infinite' }} />
            <p className="text-muted">Cargando fichas del derecho...</p>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container-fluid" style={{ backgroundColor: '#f8f9fa', minHeight: 'calc(100vh - 200px)', paddingTop: '20px' }}>
        <div className="d-flex justify-content-center align-items-center" style={{ minHeight: '50vh' }}>
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
      </div>
    );
  }

  return (
    <>
      <link
        href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.2/css/bootstrap.min.css"
        rel="stylesheet"
      />

      <div className="container-fluid" style={{ backgroundColor: '#f8f9fa', minHeight: 'calc(100vh - 200px)', paddingTop: '20px', paddingBottom: '20px' }}>
        {/* Header centrado */}
        <div className="row mb-4">
          <div className="col-12 text-center">
            <h1 className="h2 text-dark mb-0">Ficha del derecho</h1>
          </div>
        </div>

        {/* Contenido principal en dos columnas */}
        <div className="row h-100" style={{ minHeight: 'calc(100vh - 300px)' }}>
          {/* Columna izquierda - Información del derecho */}
          <div className="col-md-4 col-lg-3">
            <div className="card shadow-sm h-100">
              <div className="card-body d-flex flex-column">
                {/* Icono del derecho */}
                <div className="text-center mb-4">
                  <div className="d-inline-flex align-items-center justify-content-center mx-auto" 
                       style={{ width: '120px', height: '120px' }}>
                    {derechoId ? getDerechoIcon(derechoId) : getDerechoIcon(1)}
                  </div>
                </div>

                {/* Título del derecho */}
                <div className="text-center mb-4">
                  <h3 className="h5 text-primary fw-semibold mb-3">
                    {derechoTitle || 'Derecho a la Libertad Personal e Integridad personal'}
                  </h3>
                </div>

                {/* Descripción del derecho */}
                <div className="mb-4 flex-grow-1">
                  <p className="text-muted small lh-base text-justify">
                    Protege a toda persona contra detenciones o arrestos arbitrarios,
                    asegurando que cualquier privación de libertad se realice conforme a la
                    ley y con el debido proceso. Además, garantiza el derecho a no ser
                    sometido a torturas, tratos crueles, inhumanos o degradantes,
                    preservando la dignidad, seguridad y bienestar físico y psicológico de cada
                    individuo.
                  </p>
                </div>

                {/* Investigador actual */}
                <div className="text-center border-top pt-3">
                  <h6 className="fw-semibold text-dark mb-2">Investigador Actual:</h6>
                  <p className="text-primary fw-medium mb-0">Mauricio Erazo</p>
                </div>
              </div>
            </div>
          </div>

          {/* Columna derecha - Lista de fichas */}
          <div className="col-md-8 col-lg-9">
            <div className="card shadow-sm h-100 d-flex flex-column">
              {/* Filtros y controles */}
              <div className="card-header bg-white border-bottom">
                <div className="row g-3 align-items-center">
                  <div className="col-auto">
                    <label className="form-label small mb-0 text-muted">Filtrar por fecha:</label>
                  </div>
                  <div className="col-auto">
                    <input
                      type="date"
                      value={filterDate}
                      onChange={(e) => setFilterDate(e.target.value)}
                      className="form-control form-control-sm"
                      style={{ fontSize: '11px' }}
                    />
                  </div>
                  <div className="col-auto">
                    <span className="small text-muted">hasta</span>
                  </div>
                  <div className="col-auto">
                    <input
                      type="date"
                      value={filterDateEnd}
                      onChange={(e) => setFilterDateEnd(e.target.value)}
                      className="form-control form-control-sm"
                      style={{ fontSize: '11px' }}
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
                    >
                      <option value={10}>Mostrar 10</option>
                      <option value={25}>Mostrar 25</option>
                      <option value={50}>Mostrar 50</option>
                    </select>
                  </div>
                  <div className="col-auto">
                    <div className="d-flex align-items-center bg-light px-3 py-1 rounded">
                      <span className="fw-semibold text-primary me-1">{filteredEntries.length}</span>
                      <span className="small text-dark">entradas</span>
                    </div>
                  </div>
                  <div className="col-auto ms-auto">
                    <span className="small text-muted">
                      Página {currentPage} de {totalPages || 1}
                    </span>
                  </div>
                </div>
              </div>

              {/* Lista de entradas - área scrolleable */}
              <div className="card-body flex-grow-1" style={{ overflow: 'auto', maxHeight: 'calc(100vh - 450px)' }}>
                {currentEntries.length === 0 ? (
                  <div className="text-center py-5">
                    <FileText size={48} className="text-muted mb-3" />
                    <p className="text-muted">No se encontraron entradas para este derecho</p>
                    {filteredEntries.length !== totalEntries && (
                      <p className="small text-muted">Intenta ajustar los filtros de búsqueda</p>
                    )}
                  </div>
                ) : (
                  <div className="d-flex flex-column gap-3">
                    {currentEntries.map((entry) => (
                      <div key={entry.id} className="card border">
                        <div className="card-body" style={{ backgroundColor: '#f8f9fa' }}>
                          <div className="d-flex justify-content-between align-items-start mb-3">
                            <h6 className="card-title mb-0 fw-semibold text-dark">
                              {entry.title}
                            </h6>
                            <div className="d-flex gap-2">
                              <Edit3 
                                size={16} 
                                className="text-muted" 
                                style={{ cursor: 'pointer' }} 
                                title="Editar entrada" 
                              />
                              <button
                                type="button"
                                className="btn p-0 border-0 bg-transparent"
                                onClick={() => handleDeleteFicha(entry.id, entry.title)}
                                disabled={deleting === entry.id}
                                title="Eliminar entrada"
                              >
                                {deleting === entry.id ? (
                                  <Loader2 
                                    size={16} 
                                    className="text-danger" 
                                    style={{ animation: 'spin 1s linear infinite' }} 
                                  />
                                ) : (
                                  <Trash2 
                                    size={16} 
                                    className="text-danger" 
                                    style={{ cursor: 'pointer' }} 
                                  />
                                )}
                              </button>
                            </div>
                          </div>

                          <p className="small text-dark mb-3 lh-base">
                            {entry.content}
                          </p>

                          {entry.attachments && entry.attachments.length > 0 && (
                            <div className="mb-3">
                              <div className="d-flex align-items-center gap-2 flex-wrap">
                                <FileText size={14} className="text-muted" />
                                <span className="small text-muted">Archivos Adjuntos:</span>
                                {entry.attachments.map((attachment, index) => (
                                  <React.Fragment key={index}>
                                    <a href="#" className="small text-primary text-decoration-underline">
                                      {attachment}
                                    </a>
                                    {index < entry.attachments.length - 1 && <span className="small text-muted">,</span>}
                                  </React.Fragment>
                                ))}
                              </div>
                            </div>
                          )}

                          <div className="d-flex justify-content-between">
                            <div className="d-flex align-items-center gap-1">
                              <Calendar size={12} className="text-muted" />
                              <span className="small text-muted">Fecha creación: {entry.creationDate}</span>
                            </div>
                            <div className="d-flex align-items-center gap-2">
                              <div className="d-flex align-items-center gap-1">
                                <User size={12} className="text-muted" />
                                <span className="small text-muted">Creador: {entry.creator}</span>
                              </div>
                              <span className="text-muted">|</span>
                              <div className="d-flex align-items-center gap-1">
                                <Calendar size={12} className="text-muted" />
                                <span className="small text-muted">Última modificación: {entry.lastModified}</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              {/* Campo para nueva entrada */}
              <div className="card-footer bg-white border-top">
                <div className="mb-3">
                  <input
                    type="text"
                    value={newTitle}
                    onChange={(e) => setNewTitle(e.target.value)}
                    className="form-control mb-2"
                    placeholder="Título de la nueva entrada..."
                    disabled={saving}
                  />
                  <textarea
                    value={newPost}
                    onChange={(e) => setNewPost(e.target.value)}
                    className="form-control mb-2"
                    placeholder="Escribir un nuevo post..."
                    rows="3"
                    disabled={saving}
                  />
                  
                  {/* Archivos seleccionados */}
                  {selectedFiles.length > 0 && (
                    <div className="mb-3">
                      <h6 className="small text-muted mb-2">Archivos seleccionados:</h6>
                      <div className="border rounded p-2" style={{ maxHeight: '150px', overflow: 'auto' }}>
                        {selectedFiles.map((file) => (
                          <div key={file.id} className="d-flex align-items-center justify-content-between mb-2 p-2 bg-light rounded">
                            <div className="d-flex align-items-center gap-2 flex-grow-1">
                              <FileText size={16} className="text-primary" />
                              <div className="flex-grow-1">
                                <div className="small fw-medium text-truncate">{file.name}</div>
                                <div className="text-muted" style={{ fontSize: '10px' }}>{formatFileSize(file.size)}</div>
                              </div>
                              <select 
                                className="form-select form-select-sm" 
                                style={{ width: 'auto', fontSize: '11px' }}
                                value={file.tipo}
                                onChange={(e) => changeFileType(file.id, e.target.value)}
                                disabled={saving}
                              >
                                <option value="documento">Documento</option>
                                <option value="informe">Informe</option>
                                <option value="imagen">Imagen</option>
                                <option value="audio">Audio</option>
                                <option value="video">Video</option>
                                <option value="otro">Otro</option>
                              </select>
                            </div>
                            <button
                              type="button"
                              className="btn btn-sm btn-outline-danger ms-2"
                              onClick={() => removeFile(file.id)}
                              disabled={saving}
                            >
                              <X size={14} />
                            </button>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                  
                  <div className="d-flex gap-2 align-items-center">
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
                      <Upload size={16} />
                      Adjuntar archivos
                    </button>
                    <button 
                      className="btn btn-primary"
                      onClick={handleSaveNewPost}
                      disabled={saving || !newTitle.trim() || !newPost.trim()}
                    >
                      {saving ? (
                        <>
                          <Loader2 size={16} className="me-1" style={{ animation: 'spin 1s linear infinite' }} />
                          Guardando...
                        </>
                      ) : (
                        'Guardar'
                      )}
                    </button>
                  </div>
                </div>
              </div>

              {/* Paginación */}
              {totalPages > 1 && (
                <div className="card-footer bg-white border-top d-flex justify-content-between align-items-center">
                  <button
                    onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
                    disabled={currentPage === 1}
                    className="btn btn-link text-decoration-none p-0 d-flex align-items-center gap-1"
                    style={{ fontSize: '14px' }}
                  >
                    <ChevronLeft size={16} />
                    Anterior
                  </button>

                  <div className="d-flex align-items-center gap-2">
                    {[...Array(Math.min(5, totalPages))].map((_, i) => {
                      let pageNum;
                      if (totalPages <= 5) {
                        pageNum = i + 1;
                      } else if (currentPage <= 3) {
                        pageNum = i + 1;
                      } else if (currentPage >= totalPages - 2) {
                        pageNum = totalPages - 4 + i;
                      } else {
                        pageNum = currentPage - 2 + i;
                      }
                      
                      return (
                        <button
                          key={pageNum}
                          onClick={() => setCurrentPage(pageNum)}
                          className={`btn btn-sm ${currentPage === pageNum
                              ? 'btn-primary'
                              : 'btn-outline-secondary'
                            }`}
                          style={{ width: '32px', height: '32px' }}
                        >
                          {pageNum}
                        </button>
                      );
                    })}
                  </div>

                  <button
                    onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
                    disabled={currentPage === totalPages}
                    className="btn btn-link text-decoration-none p-0 d-flex align-items-center gap-1"
                    style={{ fontSize: '14px' }}
                  >
                    Siguiente
                    <ChevronRight size={16} />
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default FichaDerechoView;