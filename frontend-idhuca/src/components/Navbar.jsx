import { Link } from 'react-router-dom';
import logoIdhuca from '../assets/idhuca-logo-blue.png';
import { useUser } from '../Contexts/UserContext';

const Navbar = () => {
  const { user } = useUser();

  const links = [
    { to: '/registros', label: 'Registros', roles: ['admin', 'user'] },
    { to: '/ficha-de-derechos', label: 'Ficha de derechos', roles: ['admin', 'user'] },
    { to: '/configuraciones', label: 'Configuraciones', roles: ['admin', 'user'] },
    { to: '/usuarios', label: 'Usuarios', roles: ['admin'] },
    { to: '/Mantenimiento', label: 'Mantenimiento', roles: ['admin'] },
    { to: '/auditoria', label: 'Auditoría', roles: ['admin'] },
  ];

  const visibleLinks = links.filter(link => link.roles.includes(user?.role));

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
            {visibleLinks.map(({ to, label }) => (
              <li className="nav-item" key={to}>
                <Link className="nav-link px-3 small" to={to}>
                  {label}
                </Link>
              </li>
            ))}
          </ul>
          <button className="btn btn-outline-secondary btn-sm">
            <i className="bi bi-box-arrow-right me-1"></i>
            Cerrar sesión
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
