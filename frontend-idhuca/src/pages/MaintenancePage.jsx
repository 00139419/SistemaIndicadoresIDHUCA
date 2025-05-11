import React from 'react';
import Sidenav from '../components/Sidenav';

const MaintenancePage = () => {
  // Datos de ejemplo para la tabla
  const tableData = Array(15).fill().map((_, i) => ({
    col1: "Text",
    col2: "Text",
    col3: "Text",
    col4: "Text"
  }));

  return (
    <div className="d-flex">
      {/* Importamos el componente Sidenav */}
      <Sidenav />
      
      {/* Contenido principal de la página */}
      <div className="flex-grow-1 p-4">
        <h1 className="mb-4">Mantenimiento</h1>
        
        <div className="table-responsive">
          <table className="table table-bordered">
            <thead className="table-dark">
              <tr>
                <th>Text</th>
                <th>Text</th>
                <th>Text</th>
                <th>Text</th>
              </tr>
            </thead>
            <tbody>
              {tableData.map((row, index) => (
                <tr key={index}>
                  <td>{row.col1}</td>
                  <td>{row.col2}</td>
                  <td>{row.col3}</td>
                  <td>{row.col4}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        
        <div className="d-flex justify-content-between align-items-center mt-3">
          <div>
            Mostrar 
            <select className="form-select form-select-sm d-inline-block mx-2" style={{width: "60px"}}>
              <option>1</option>
              <option>5</option>
              <option>10</option>
              <option>15</option>
            </select>
          </div>
          
          <div>
            Página 1 de 15
            <div className="btn-group ms-2">
              <button className="btn btn-sm btn-outline-secondary">&lt;</button>
              <button className="btn btn-sm btn-outline-secondary">&gt;</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MaintenancePage;