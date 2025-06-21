import { useState, useEffect } from 'react';
import { useAuth } from '../../components/AuthContext';
import graphImage from '../../assets/image.png'; 

const Graficos = () => {
  const { userRole } = useAuth();
  
  // Función para verificar permisos
  const hasPermission = (action) => {
    switch (action) {
      case 'generate':
      case 'modify':
      case 'export':
        return ['ROL_1', 'ROL_2', 'ROL_3'].includes(userRole);
      default:
        return false;
    }
  };

  const [chartConfig, setChartConfig] = useState({
    ejeX: 'Cantidad de personas afectadas de departamento de Ahuachapán',
    delimitador: '',
    tipoGrafico: 'Pastel',
    paletaColores: 'Paleta 1',
    titulo: 'Gráfico de ejemplo',
    subtitulo: 'Datos estadísticos del departamento',
    dimension: '2D' 
  });

  const [chartData, setChartData] = useState([
    { label: 'Categoría A', value: 45, color: '#007bff' },
    { label: 'Categoría B', value: 30, color: '#ffc107' },
    { label: 'Categoría C', value: 25, color: '#28a745' }
  ]);

  const [filtrosEjeX, setFiltrosEjeX] = useState([
    'Cantidad de personas afectadas de departamento de Ahuachapán', 
    'Filtro 1', 
    'Fecha', 
    'Departamento', 
    'Tipo'
  ]);

  const [filtrosDelimitador, setFiltrosDelimitador] = useState([
    'Filtro 3', 'Género', 'Edad', 'Estado'
  ]);

  const tiposGrafico = ['Pastel', 'Barras', 'Líneas', 'Área'];
  const paletasColores = ['Paleta 1', 'Paleta 2', 'Paleta 3', 'Paleta 4'];

  const handleConfigChange = (field, value) => {
    setChartConfig(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const copyToClipboard = async () => {
    try {
      // En ls implementación real copiar aqui la configuración del gráfico
      const configText = `Configuración del gráfico:
Eje X: ${chartConfig.ejeX}
Delimitador: ${chartConfig.delimitador}
Tipo: ${chartConfig.tipoGrafico}
Paleta: ${chartConfig.paletaColores}
Título: ${chartConfig.titulo}
Subtítulo: ${chartConfig.subtitulo}
Dimensión: ${chartConfig.dimension}`;
      
      await navigator.clipboard.writeText(configText);
      alert('Configuración copiada al portapapeles');
    } catch (err) {
      console.error('Error al copiar:', err);
    }
  };

  const downloadAsPNG = () => {
    // En la implementación real, aquí generar y descargar el PNG del gráfico
    alert('Descargando gráfico como PNG...');
  };

  const renderPieChart = () => {
    const total = chartData.reduce((sum, item) => sum + item.value, 0);
    let currentAngle = 0;
    
    return (
      <div className="d-flex flex-column align-items-center">
        <h5 className="mt-3 mb-4 fw-bold">{chartConfig.titulo}</h5>
      </div>
    );
  };

  return (
    <div className="container-fluid px-0" style={{ 
      minHeight: '100vh', 
      width: '100%',
      maxWidth: '100%',
    }}>
      <div className="container-fluid px-4 py-5" style={{ minHeight: '100%' }}> 
        {/* Título mejorado */}
        <div className="text-center mb-5">
          <h1 className="display-4 fw-bold" style={{ 
            fontSize: '2.8rem', 
            color: '#1a237e',
            marginBottom: '0.5rem'
          }}>
            Gráficos
          </h1>
          <p className="text-muted">Visualiza tus datos de forma interactiva</p>
        </div>

        <div className="row g-4" style={{ minHeight: '80vh' }}>
          {/* Panel izquierdo - Configuración - Reducido */}
          {hasPermission('modify') && (
            <div className="col-md-4 col-lg-3">
              <div className="bg-white p-3 rounded-lg shadow-sm h-100" style={{
                borderRadius: '15px',
                border: '1px solid #e0e0e0',
                maxHeight: '80vh',
                overflowY: 'auto'
              }}>
                {/* Sección Dimensiones */}
                <div className="mb-4">
                  <h6 className="fw-bold mb-3" style={{ color: '#1a237e', fontSize: '1rem' }}>
                    <i className="bi bi-sliders me-2"></i>Dimensiones
                  </h6>
                  
                  <div className="mb-3">
                    <label className="form-label fw-semibold" style={{ fontSize: '0.9rem' }}>Eje X</label>
                    <select 
                      className="form-select form-select-sm"
                      style={{
                        backgroundColor: '#f8f9fa',
                        border: '1px solid #ced4da',
                        borderRadius: '6px',
                        fontSize: '0.85rem'
                      }}
                      value={chartConfig.ejeX}
                      onChange={(e) => handleConfigChange('ejeX', e.target.value)}
                    >
                      <option value="">Seleccionar filtro...</option>
                      {filtrosEjeX.map((filtro, index) => (
                        <option key={index} value={filtro}>{filtro}</option>
                      ))}
                    </select>
                  </div>

                  <div className="mb-3">
                    <label className="form-label fw-semibold" style={{ fontSize: '0.9rem' }}>Delimitador</label>
                    <select 
                      className="form-select form-select-sm"
                      style={{
                        backgroundColor: '#f8f9fa',
                        border: '1px solid #ced4da',
                        borderRadius: '6px',
                        fontSize: '0.85rem'
                      }}
                      value={chartConfig.delimitador}
                      onChange={(e) => handleConfigChange('delimitador', e.target.value)}
                    >
                      <option value="">Seleccionar filtro...</option>
                      {filtrosDelimitador.map((filtro, index) => (
                        <option key={index} value={filtro}>{filtro}</option>
                      ))}
                    </select>
                  </div>
                </div>

                {/* Sección Propiedades */}
                <div className="mb-4">
                  <h6 className="fw-bold mb-3" style={{ color: '#1a237e', fontSize: '1rem' }}>
                    <i className="bi bi-gear me-2"></i>Propiedades
                  </h6>
                  
                  <div className="mb-3">
                    <label className="form-label fw-semibold" style={{ fontSize: '0.9rem' }}>Tipo de gráfico</label>
                    <select 
                      className="form-select form-select-sm"
                      style={{
                        backgroundColor: '#f8f9fa',
                        border: '1px solid #ced4da',
                        borderRadius: '6px',
                        fontSize: '0.85rem'
                      }}
                      value={chartConfig.tipoGrafico}
                      onChange={(e) => handleConfigChange('tipoGrafico', e.target.value)}
                    >
                      {tiposGrafico.map((tipo, index) => (
                        <option key={index} value={tipo}>{tipo}</option>
                      ))}
                    </select>
                  </div>

                  {/* Botones 2D/3D */}
                  <div className="mb-3">
                    <label className="form-label fw-semibold" style={{ fontSize: '0.9rem' }}>Dimensión</label>
                    <div className="btn-group w-100" role="group">
                      <button
                        type="button"
                        className={`btn btn-sm ${chartConfig.dimension === '2D' ? 'btn-primary' : 'btn-outline-primary'}`}
                        onClick={() => handleConfigChange('dimension', '2D')}
                        style={{
                          fontSize: '0.8rem',
                          backgroundColor: chartConfig.dimension === '2D' ? '#1a237e' : 'transparent',
                          borderColor: '#1a237e'
                        }}
                      >
                        2D
                      </button>
                      <button
                        type="button"
                        className={`btn btn-sm ${chartConfig.dimension === '3D' ? 'btn-primary' : 'btn-outline-primary'}`}
                        onClick={() => handleConfigChange('dimension', '3D')}
                        style={{
                          fontSize: '0.8rem',
                          backgroundColor: chartConfig.dimension === '3D' ? '#1a237e' : 'transparent',
                          borderColor: '#1a237e'
                        }}
                      >
                        3D
                      </button>
                    </div>
                  </div>

                  <div className="mb-3">
                    <label className="form-label fw-semibold" style={{ fontSize: '0.9rem' }}>Paleta de colores</label>
                    <select 
                      className="form-select form-select-sm"
                      style={{
                        backgroundColor: '#f8f9fa',
                        border: '1px solid #ced4da',
                        borderRadius: '6px',
                        fontSize: '0.85rem'
                      }}
                      value={chartConfig.paletaColores}
                      onChange={(e) => handleConfigChange('paletaColores', e.target.value)}
                    >
                      {paletasColores.map((paleta, index) => (
                        <option key={index} value={paleta}>{paleta}</option>
                      ))}
                    </select>
                  </div>

                  <div className="mb-3">
                    <label className="form-label fw-semibold" style={{ fontSize: '0.9rem' }}>Título</label>
                    <input 
                      type="text"
                      className="form-control form-control-sm"
                      style={{
                        backgroundColor: '#f8f9fa',
                        border: '1px solid #ced4da',
                        borderRadius: '6px',
                        fontSize: '0.85rem'
                      }}
                      value={chartConfig.titulo}
                      onChange={(e) => handleConfigChange('titulo', e.target.value)}
                      placeholder="Ingrese el título del gráfico"
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label fw-semibold" style={{ fontSize: '0.9rem' }}>Subtítulo</label>
                    <input 
                      type="text"
                      className="form-control form-control-sm"
                      style={{
                        backgroundColor: '#f8f9fa',
                        border: '1px solid #ced4da',
                        borderRadius: '6px',
                        fontSize: '0.85rem'
                      }}
                      value={chartConfig.subtitulo}
                      onChange={(e) => handleConfigChange('subtitulo', e.target.value)}
                      placeholder="Ingrese el subtítulo del gráfico"
                    />
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Panel derecho - Visualización del gráfico - Expandido */}
          <div className={hasPermission('modify') ? "col-md-8 col-lg-9" : "col-12"}>
            <div className="bg-white p-4 rounded-lg shadow-sm h-100 d-flex flex-column" style={{
              borderRadius: '15px',
              border: '1px solid #e0e0e0',
              minHeight: '80vh'
            }}>
              {/* Títulos del gráfico */}
              <div className="text-center mb-3">
                <h3 className="fw-bold mb-2" style={{ 
                  color: '#1a237e',
                  fontSize: '1.8rem'
                }}>
                  {chartConfig.titulo}
                </h3>
                {chartConfig.subtitulo && (
                  <h5 className="text-muted mb-3" style={{ 
                    fontSize: '1.2rem',
                    fontWeight: '400'
                  }}>
                    {chartConfig.subtitulo}
                  </h5>
                )}
                {/* Indicador de dimensión */}
                <div className="mb-3">
                  <span className={`badge ${chartConfig.dimension === '3D' ? 'bg-success' : 'bg-primary'}`} 
                        style={{ fontSize: '0.9rem' }}>
                    Modo {chartConfig.dimension}
                  </span>
                </div>
              </div>

              {/* Área del gráfico expandida */}
              <div className="flex-grow-1 d-flex align-items-center justify-content-center" 
                   style={{ minHeight: '500px' }}>
                <div className="position-relative w-100 h-100 d-flex align-items-center justify-content-center">
                  <img 
                    src={graphImage}
                    alt="Gráfico de ejemplo"
                    className="img-fluid"
                    style={{ 
                      maxWidth: '95%', 
                      maxHeight: '95%',
                      minHeight: '400px',
                      objectFit: 'contain',
                      borderRadius: '8px',
                      boxShadow: '0 4px 8px rgba(0,0,0,0.1)'
                    }}
                  />
                  
                  {/* Etiqueta del Eje X */}
                  <div className="position-absolute bottom-0 start-50 translate-middle-x mb-2">
                    <div className="bg-dark text-white px-3 py-2 rounded" 
                         style={{ 
                           fontSize: '0.9rem',
                           opacity: '0.8',
                           maxWidth: '400px',
                           textAlign: 'center'
                         }}>
                      <strong>Eje X:</strong> {chartConfig.ejeX || 'No seleccionado'}
                    </div>
                  </div>
                </div>
              </div>

              {/* Botones de exportación - Solo mostrar si tiene permiso */}
              {hasPermission('export') && (
                <div className="d-flex gap-3 justify-content-center mt-4">
                  <button 
                    className="btn btn-primary px-4 py-2"
                    onClick={copyToClipboard}
                    style={{
                      borderRadius: '8px',
                      backgroundColor: '#1a237e',
                      border: 'none',
                      boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                    }}
                  >
                    <i className="bi bi-clipboard me-2"></i>
                    Copiar
                  </button>
                  <button 
                    className="btn btn-primary px-4 py-2"
                    onClick={downloadAsPNG}
                    style={{
                      borderRadius: '8px',
                      backgroundColor: '#1a237e',
                      border: 'none',
                      boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                    }}
                  >
                    <i className="bi bi-download me-2"></i>
                    Descargar PNG
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Graficos;