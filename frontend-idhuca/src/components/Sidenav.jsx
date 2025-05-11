import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { 
  FaHome, 
  FaBuilding, 
  FaMapMarkerAlt, 
  FaTransgender, 
  FaUniversity, 
  FaComments, 
  FaPalette, 
  FaPlug, 
  FaUsers, 
  FaCog, 
  FaTools 
} from 'react-icons/fa';

const Sidenav = () => {
  const location = useLocation();
  
  const menuItems = [
    { name: 'Catalogos', icon: <FaHome size={20} />, path: '/catalogos' },
    { name: 'Departamentos', icon: <FaBuilding size={20} />, path: '/departamentos' },
    { name: 'Municipios', icon: <FaMapMarkerAlt size={20} />, path: '/municipios', active: true },
    { name: 'Sexo', icon: <FaTransgender size={20} />, path: '/sexo' },
    { name: 'Instituciones', icon: <FaUniversity size={20} />, path: '/instituciones' },
    { name: 'Comments', icon: <FaComments size={20} />, path: '/comments', badge: 1 },
    { name: 'Appearance', icon: <FaPalette size={20} />, path: '/appearance' },
    { name: 'Plugins', icon: <FaPlug size={20} />, path: '/plugins' },
    { name: 'Users', icon: <FaUsers size={20} />, path: '/users' },
    { name: 'Settings', icon: <FaCog size={20} />, path: '/settings' },
    { name: 'Tools', icon: <FaTools size={20} />, path: '/tools' }
  ];

  return (
    <div className="bg-light border-end" style={{ width: '250px', minHeight: 'calc(100vh - 136px)' }}>
      <div className="py-3">
        {menuItems.map((item, index) => (
          <Link 
            key={index} 
            to={item.path} 
            className={`d-flex align-items-center text-decoration-none text-secondary p-3 ${item.active ? 'bg-primary bg-opacity-10' : ''}`}
          >
            <div className="me-3 text-secondary">
              {item.icon}
            </div>
            <span>{item.name}</span>
            {item.badge && (
              <span className="ms-auto badge bg-primary rounded-pill">{item.badge}</span>
            )}
          </Link>
        ))}
      </div>
    </div>
  );
};

export default Sidenav;