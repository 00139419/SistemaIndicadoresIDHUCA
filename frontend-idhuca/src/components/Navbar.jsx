import React from 'react';
import { Link } from 'react-router-dom';
import logoIdhuca from '../assets/idhuca-logo-blue.png';
import { useAuth } from './AuthContext'

const Navbar = () => {
  const { logout, userRole } = useAuth();

  const handleLogout = () => {
    logout();
  };

  const links = [
    { to: '/registros', label: 'Registros', roles: ['ROL_1', 'ROL_2'] },
    { to: '/ficha-de-derechos', label: 'Ficha de derechos', roles: ['ROL_1', 'ROL_2'] },
    { to: '/configuraciones', label: 'Configuraciones', roles: ['ROL_1', 'ROL_2'] },
    { to: '/users', label: 'Usuarios', roles: ['ROL_1'] },
    { to: '/Mantenimiento', label: 'Mantenimiento', roles: ['ROL_1'] },
    { to: '/auditoria', label: 'Auditoría', roles: ['ROL_1'] },
    { to: '/parametros', label: 'Parámetros', roles: ['ROL_1'] },
    ];

  const visibleLinks = links.filter(link => link.roles.includes(userRole));


  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-white shadow-sm" style={{ paddingTop: '0.5rem', paddingBottom: '0.5rem' }}>
      <div className="container">
        <Link className="navbar-brand d-flex align-items-center me-4" to="/index">
          <img src={logoIdhuca} alt="Instituto de Derechos Humanos de la UCA" height="40" />
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
          <ul className="navbar-nav me-auto">
            {visibleLinks.map(({ to, label }) => {
              const isFichaOrRegistros = to === '/ficha-de-derechos' || to === '/registros';
              const target = isFichaOrRegistros ? '/seleccion-derecho' : to;
              const state = isFichaOrRegistros ? { from: to === '/ficha-de-derechos' ? 'ficha' : 'registros' } : undefined;

              return (
                <li className="nav-item" key={to}>
                  <Link className="nav-link px-3 small" to={target} state={state}>
                    {label}
                  </Link>
                </li>
              );
            })}

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
