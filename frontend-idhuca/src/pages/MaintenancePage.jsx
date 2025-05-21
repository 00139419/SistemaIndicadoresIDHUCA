import { useEffect, useState } from 'react';
import Sidenav from '../components/Sidenav';
import { fetchCatalog } from '../services/CatalogService';

const MaintenancePage = () => {
  const [catalogData, setCatalogData] = useState([]);
  const [selectedCatalog, setSelectedCatalog] = useState('departamentos');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  // Versión simplificada que garantiza recibir los datos correctamente
  useEffect(() => {
    const loadCatalog = async () => {
      setLoading(true);
      setCatalogData([]); // Limpiar datos anteriores
      
      try {
        // Para municipios, necesitamos enviar el parentId del departamento
        const parentId = selectedCatalog === 'municipios' ? "DEP_1" : "1";
        
        // Obtener datos del servicio
        const result = await fetchCatalog(selectedCatalog, parentId);
        
        // Imprimir para depuración
        console.log(`Datos de ${selectedCatalog} recibidos:`, result);
        
        // Actualizar el estado con los datos recibidos
        setCatalogData(result || []);
      } catch (err) {
        setError('Error al cargar datos');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    
    // Ejecutar la carga de datos
    loadCatalog();
  }, [selectedCatalog]); // Solo se ejecuta cuando cambia selectedCatalog

  return (
    <div className="d-flex">
      <Sidenav onSelectCatalog={setSelectedCatalog} />
      <div className="flex-grow-1 p-4">
        <h1 className="mb-4 text-capitalize">{selectedCatalog}</h1>
        
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
        
        {!loading && !error && (
          <>
            {/* Debugging info */}
            <div className="mb-3 small text-muted">
              {catalogData && catalogData.length > 0 ? 
                `Mostrando ${catalogData.length} registros` : 
                'No hay datos para mostrar'}
            </div>
            
            <div className="table-responsive">
              <table className="table table-bordered">
                <thead className="table-dark">
                  <tr>
                    <th>Código</th>
                    <th>Descripción</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {catalogData && catalogData.length > 0 ? (
                    catalogData.map((item, index) => (
                      <tr key={item.codigo || index}>
                        <td>{item.codigo}</td>
                        <td>{item.descripcion}</td>
                        <td>
                          <div className="btn-group">
                            <button className="btn btn-sm btn-warning">
                              <i className="fas fa-edit"></i> Editar
                            </button>
                            <button className="btn btn-sm btn-danger ms-1">
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
            
            {catalogData && catalogData.length > 0 && (
              <div className="d-flex justify-content-between align-items-center mt-3">
                <div>
                  Mostrar
                  <select className="form-select form-select-sm d-inline-block mx-2" style={{ width: "60px" }}>
                    <option>1</option>
                    <option>5</option>
                    <option>10</option>
                    <option>15</option>
                  </select>
                </div>
                
                <div>
                  Página 1 de {Math.ceil(catalogData.length / 10)}
                  <div className="btn-group ms-2">
                    <button className="btn btn-sm btn-outline-secondary">&lt;</button>
                    <button className="btn btn-sm btn-outline-secondary">&gt;</button>
                  </div>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default MaintenancePage;