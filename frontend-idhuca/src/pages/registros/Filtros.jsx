import React, { useState } from 'react';

const FiltradoRegistros = () => {
  const [filters, setFilters] = useState({
    propiedadesEvento: {
      opcion1: true,
      opcion2: false,
      opcion3: true,
      opcion4: false
    },
    propiedadesPersona: {
      opcion1: false,
      opcion2: false,
      opcion3: false,
      opcion4: true
    },
    violencia: {
      opcion1: false,
      opcion2: true,
      opcion3: true,
      opcion4: false
    },
    filtro4: {
      opcion1: false,
      opcion2: true,
      opcion3: false,
      opcion4: true
    },
    filtro5: {
      opcion1: false,
      opcion2: false,
      opcion3: true,
      opcion4: true
    },
    filtro6: {
      opcion1: true,
      opcion2: false,
      opcion3: false,
      opcion4: false
    },
    filtro7: {
      opcion1: true,
      opcion2: false,
      opcion3: false,
      opcion4: false
    },
    filtro8: {
      opcion1: true,
      opcion2: false,
      opcion3: true,
      opcion4: false
    }
  });

  const handleCheckboxChange = (filterGroup, option) => {
    setFilters(prev => ({
      ...prev,
      [filterGroup]: {
        ...prev[filterGroup],
        [option]: !prev[filterGroup][option]
      }
    }));
  };

  const handleApplyFilters = () => {
    console.log('Aplicando filtros:', filters);
    // Aquí iría la lógica para aplicar los filtros
  };

  const handleClearFilters = () => {
    const clearedFilters = {};
    Object.keys(filters).forEach(filterGroup => {
      clearedFilters[filterGroup] = {};
      Object.keys(filters[filterGroup]).forEach(option => {
        clearedFilters[filterGroup][option] = false;
      });
    });
    setFilters(clearedFilters);
  };

  const handleCancelFilters = () => {
    console.log('Cancelando filtros');
    // Aquí iría la lógica para cancelar/resetear
  };

  const getCheckedCount = (filterGroup) => {
    return Object.values(filters[filterGroup]).filter(Boolean).length;
  };

  const FilterCard = ({ title, filterKey, options = ['opcion 1', 'opcion 1', 'opcion 1', 'opcion 1'] }) => (
    <div className="col-lg-3 col-md-6 col-12 mb-4">
      <div className="card h-100 shadow-sm">
        <div className="card-body">
          <h5 className="card-title text-center mb-3 fs-6">{title}</h5>
          <div className="mb-3">
            {options.map((option, index) => {
              const optionKey = `opcion${index + 1}`;
              return (
                <div key={optionKey} className="form-check mb-2">
                  <input
                    className="form-check-input"
                    type="checkbox"
                    id={`${filterKey}-${optionKey}`}
                    checked={filters[filterKey]?.[optionKey] || false}
                    onChange={() => handleCheckboxChange(filterKey, optionKey)}
                  />
                  <label className="form-check-label small" htmlFor={`${filterKey}-${optionKey}`}>
                    {option}
                  </label>
                </div>
              );
            })}
          </div>
          <div className="text-center">
            <button className="btn btn-link p-0 text-decoration-none small">
              Ver más ({getCheckedCount(filterKey)})
            </button>
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <>
      {/* Bootstrap CSS CDN */}
      <link 
        href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" 
        rel="stylesheet" 
      />
      <link 
        href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-icons/1.10.0/font/bootstrap-icons.min.css" 
        rel="stylesheet" 
      />
      
      <div className="bg-light min-vh-100" style={{ marginTop: '100px', marginBottom: '100px' }}>
        <div className="container py-5">
          {/* Header */}
          <div className="text-center mb-5">
            <h1 className="display-5 fw-bold text-dark mb-4">Filtrar</h1>
            
            {/* Action buttons */}
            <div className="d-flex justify-content-center gap-3 mb-5 flex-wrap">
              <button
                onClick={handleApplyFilters}
                className="btn btn-primary px-4"
              >
                Aplicar filtros
              </button>
              <button
                onClick={handleClearFilters}
                className="btn btn-primary px-4"
              >
                Borrar todos los filtros
              </button>
              <button
                onClick={handleCancelFilters}
                className="btn btn-danger px-4"
              >
                Cancelar
              </button>
            </div>
          </div>

          {/* Filter Grid */}
          <div className="row mb-5">
            <FilterCard 
              title="Propiedades del evento" 
              filterKey="propiedadesEvento"
            />
            <FilterCard 
              title="Propiedades de la persona afectada" 
              filterKey="propiedadesPersona"
            />
            <FilterCard 
              title="Violencia" 
              filterKey="violencia"
            />
            <FilterCard 
              title="Filtro 4" 
              filterKey="filtro4"
            />
            <FilterCard 
              title="Filtro 5" 
              filterKey="filtro5"
            />
            <FilterCard 
              title="Filtro 6" 
              filterKey="filtro6"
            />
            <FilterCard 
              title="Filtro 7" 
              filterKey="filtro7"
            />
            <FilterCard 
              title="Filtro 8" 
              filterKey="filtro8"
            />
          </div>

          {/* Bottom action button */}
          <div className="text-center">
            <button
              onClick={handleApplyFilters}
              className="btn btn-secondary px-5 py-2"
            >
              ver más filtros (+)
            </button>
          </div>
        </div>
      </div>

      {/* Bootstrap JS CDN */}
      <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
    </>
  );
};

export default FiltradoRegistros;