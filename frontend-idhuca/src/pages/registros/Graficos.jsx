import { useState, useEffect } from 'react';
import { useAuth } from '../../components/AuthContext';
import graphImage from '../../assets/image.png'; // Imagen de ejemplo para el gráfico

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
    ejeX: '',
    delimitador: '',
    tipoGrafico: 'Pastel',
    paletaColores: 'Paleta 1',
    titulo: 'Grafico de ejemplo'
  });

  const [chartData, setChartData] = useState([
    { label: 'Categoría A', value: 45, color: '#007bff' },
    { label: 'Categoría B', value: 30, color: '#ffc107' },
    { label: 'Categoría C', value: 25, color: '#28a745' }
  ]);

  const [filtrosEjeX, setFiltrosEjeX] = useState([
    'Filtro 1', 'Fecha', 'Departamento', 'Tipo'
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
      // En una implementación real, aquí copiarías la configuración del gráfico
      const configText = `Configuración del gráfico:
Eje X: ${chartConfig.ejeX}
Delimitador: ${chartConfig.delimitador}
Tipo: ${chartConfig.tipoGrafico}
Paleta: ${chartConfig.paletaColores}
Título: ${chartConfig.titulo}`;
      
      await navigator.clipboard.writeText(configText);
      alert('Configuración copiada al portapapeles');
    } catch (err) {
      console.error('Error al copiar:', err);
    }
  };

  const downloadAsPNG = () => {
    // En una implementación real, aquí generarías y descargarías el PNG del gráfico
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
      minHeight: '100%', 
      width: '100%', 
      maxWidth: '120%',
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

        <div className="row g-4">
          {/* Panel izquierdo - Configuración */}
          {hasPermission('modify') && (
            <div className="col-md-6">
              <div className="bg-white p-4 rounded-lg shadow-sm h-100" style={{
                borderRadius: '15px',
                border: '1px solid #e0e0e0'
              }}>
                {/* Sección Dimensiones */}
                <div className="mb-4">
                  <h5 className="fw-bold mb-3" style={{ color: '#1a237e' }}>
                    <i className="bi bi-sliders me-2"></i>Dimensiones
                  </h5>
                  
                  <div className="mb-4">
                    <label className="form-label"
                    style={{
                        
                        borderRadius: '8px',
                        padding: '0.75rem',
                        fontSize: '1.2rem'
                      }}>Eje X</label>
                    <select 
                      className="form-select"
                      style={{
                        backgroundColor: '#f8f9fa',
                        border: '1px solid #ced4da',
                        borderRadius: '8px',
                        padding: '0.75rem',
                        fontSize: '1rem'
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

                  <div className="mb-7">
                    <label className="form-label fw-semibold">Delimitador</label>
                    <select 
                      className="form-select"
                       style={{
                        backgroundColor: '#f8f9fa',
                        border: '1px solid #ced4da',
                        borderRadius: '8px',
                        padding: '0.75rem',
                        fontSize: '1rem'
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
                  <h5 className="fw-bold mb-3" style={{ color: '#1a237e' }}>
                    <i className="bi bi-gear me-2"></i>Propiedades
                  </h5>
                  
                  <div className="mb-3">
                    <label className="form-label"
                    style={{
                        
                        borderRadius: '8px',
                        padding: '0.75rem',
                        fontSize: '1.2rem'
                      }}>Tipo de gráfico</label>
                    <select 
                      className="form-select"
                       style={{
                        backgroundColor: '#f8f9fa',
                        border: '1px solid rgb(255, 255, 255)',
                        borderRadius: '8px',
                        padding: '0.75rem',
                        fontSize: '1rem'
                      }}
                      value={chartConfig.tipoGrafico}
                      onChange={(e) => handleConfigChange('tipoGrafico', e.target.value)}
                    >
                      {tiposGrafico.map((tipo, index) => (
                        <option key={index} value={tipo}>{tipo}</option>
                      ))}
                    </select>
                  </div>

                  <div className="mb-3">
                    <label className="form-label fw-semibold">Paleta de colores</label>
                    <select 
                      className="form-select"
                      style={{
                        backgroundColor: '#f8f9fa',
                        border: '1px solid rgb(255, 255, 255)',
                        borderRadius: '8px',
                        padding: '0.75rem',
                        fontSize: '1rem'
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
                    <label className="form-label fw-semibold">Título</label>
                    <input 
                      type="text"
                      className="form-control"
                      style={{
                        backgroundColor: '#f8f9fa',
                        border: '1px solid rgb(255, 255, 255)',
                        borderRadius: '8px',
                        padding: '0.75rem',
                        fontSize: '1rem'
                      }}
                      value={chartConfig.titulo}
                      onChange={(e) => handleConfigChange('titulo', e.target.value)}
                      placeholder="Ingrese el título del gráfico"
                    />
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Panel derecho - Visualización del gráfico */}
          <div className="col-md-6">
            <div className="bg-white p-5 rounded-lg shadow-sm h-100 d-flex flex-column" style={{
              borderRadius: '15px',
              border: '1px solid #e0e0e0'
            }}>
              <div className="flex-grow-1 d-flex align-items-center justify-content-center">
      <img 
        src={graphImage}
        alt="Gráfico de ejemplo"
        className="img-fluid"
        style={{ minHeight: '300px', minWidth: '100%', maxWidth: '120%', maxHeight: '120%' }}
      />
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