import { 
  FaHome, 
  FaBuilding, 
  FaMapMarkerAlt, 
  FaTransgender, 
  FaUniversity
} from 'react-icons/fa';

const Sidenav = ({ onSelectCatalog }) => {
  const catalogOptions = [
    { name: 'Catalogos', label: 'catalogos', icon: <FaHome size={20} />, key: 'catalogos' },
    { name: 'Departamentos', label: 'departamentos', icon: <FaBuilding size={20} />, key: 'departamentos' },
    { name: 'Municipios', label: 'municipios', icon: <FaMapMarkerAlt size={20} />, key: 'municipios' },
    { name: 'Sexo', label: 'sexo', icon: <FaTransgender size={20} />, key: 'sexo' },
    { name: 'Instituciones', label: 'instituciones', icon: <FaUniversity size={20} />, key: 'instituciones' },
  ];

  return (
    <div className="bg-light border-end" style={{ width: '250px', minHeight: 'calc(100vh - 136px)' }}>
      <div className="py-3">
        {catalogOptions.map(({ key, label, icon }) => (
          <button
            key={key}
            onClick={() => onSelectCatalog(key)}
            className="btn w-100 d-flex align-items-center text-start text-secondary p-3 border-0 bg-transparent"
          >
            <div className="me-3 text-secondary">{icon}</div>
            <span>{label}</span>
          </button>
        ))}
      </div>
    </div>
  );
};

export default Sidenav;
