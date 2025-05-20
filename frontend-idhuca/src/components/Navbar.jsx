import React from 'react';
import { Link } from 'react-router-dom';
import logoIdhuca from '../assets/idhuca-logo-blue.png';
import { useAuth } from './AuthContext'

const Navbar = () => {
  const { logout, userRole } = useAuth();

  const handleLogout = () => {
    logout();
  };

  const renderMenuItems = () => {
  switch (userRole) {
    case 'ROL_1':
      return (
        <>
          <li className="nav-item">
            <Link className="nav-link mx-2" to="/registros">Registros</Link>
          </li>
          <li className="nav-item">
            <Link className="nav-link mx-2" to="/ficha-de-derechos">Ficha de derechos</Link>
          </li>
          <li className="nav-item">
            <Link className="nav-link mx-2" to="/configuraciones">Configuraciones</Link>
          </li>
          <li className="nav-item">
            <Link className="nav-link mx-2" to="/usuarios">Usuarios</Link>
          </li>
          <li className="nav-item">
            <Link className="nav-link mx-2" to="/mantenimiento">Mantenimiento</Link>
          </li>
          <li className="nav-item">
            <Link className="nav-link mx-2" to="/auditoria">Auditoría</Link>
          </li>
        </>
      );
    case 'ROL_2':
      return (
        <>
          <li className="nav-item">
            <Link className="nav-link mx-2" to="/registros">Registros</Link>
          </li>
          <li className="nav-item">
            <Link className="nav-link mx-2" to="/ficha-de-derechos">Ficha de derechos</Link>
          </li>
        </>
      );
    default:
      return null;
  }
};

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-white py-2 shadow-sm">
      <div className="container">
        <Link className="navbar-brand" to="/">
          <img src={logoIdhuca} alt="Instituto de Derechos Humanos de la UCA" height="60" />
        </Link>
        
        <button 
          className="navbar-toggler" 
          type="button" 
          data-bs-toggle="collapse" 
          data-bs-target="#navbarNavDropdown" 
          aria-controls="navbarNavDropdown" 
          aria-expanded="false" 
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>
        
        <div className="collapse navbar-collapse" id="navbarNavDropdown">
          <ul className="navbar-nav ms-auto">
            {renderMenuItems()}
          </ul>
          <button 
            className="btn btn-outline-secondary ms-3" 
            onClick={handleLogout}
          >
            <i className="bi bi-box-arrow-right me-1"></i>
            Cerrar sesión
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;