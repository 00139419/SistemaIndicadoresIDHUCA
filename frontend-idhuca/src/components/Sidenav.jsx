import { 
  FaHome, 
  FaBuilding, 
  FaMapMarkerAlt, 
  FaTransgender, 
  FaUniversity,
  FaGavel,
  FaUserAlt,
  FaClock,
  FaHandPaper,
  FaNewspaper,
  FaProcedures,
  FaUserInjured,
  FaMapMarked,
  FaCheck,
  FaBook,
  FaGlobe,
  FaShieldAlt,
  FaUserTag,
  FaQuestion
} from 'react-icons/fa';

const Sidenav = ({ onSelectCatalog }) => {
  const catalogOptions = [
    { name: 'Catalogos', label: 'Catálogos', icon: <FaHome size={20} />, key: 'catalogos' },
    { name: 'Departamentos', label: 'Departamentos', icon: <FaBuilding size={20} />, key: 'departamentos' },
    { name: 'Municipios', label: 'Municipios', icon: <FaMapMarkerAlt size={20} />, key: 'municipios' },
    { name: 'Genero', label: 'Género', icon: <FaTransgender size={20} />, key: 'genero' },
    { name: 'Instituciones', label: 'Instituciones', icon: <FaUniversity size={20} />, key: 'instituciones' },
    { name: 'TipoProcesoJudicial', label: 'Tipo Proceso Judicial', icon: <FaGavel size={20} />, key: 'tipoProcesoJudicial' },
    { name: 'TipoDenunciante', label: 'Tipo Denunciante', icon: <FaUserAlt size={20} />, key: 'tipoDenunciante' },
    { name: 'DuracionProceso', label: 'Duración Proceso', icon: <FaClock size={20} />, key: 'duracionProceso' },
    { name: 'TipoRepresion', label: 'Tipo Represión', icon: <FaHandPaper size={20} />, key: 'tipoRepresion' },
    { name: 'MedioExpresion', label: 'Medio Expresión', icon: <FaNewspaper size={20} />, key: 'medioExpresion' },
    { name: 'MotivoDetencion', label: 'Motivo Detención', icon: <FaHandPaper size={20} />, key: 'motivoDetencion' },
    { name: 'TipoArma', label: 'Tipo Arma', icon: <FaHandPaper size={20} />, key: 'tipoArma' },
    { name: 'TipoDetencion', label: 'Tipo Detención', icon: <FaHandPaper size={20} />, key: 'tipoDetencion' },
    { name: 'TipoViolencia', label: 'Tipo Violencia', icon: <FaHandPaper size={20} />, key: 'tipoViolencia' },
    { name: 'EstadoSalud', label: 'Estado Salud', icon: <FaProcedures size={20} />, key: 'estadoSalud' },
    { name: 'TipoPersona', label: 'Tipo Persona', icon: <FaUserInjured size={20} />, key: 'tipoPersona' },
    { name: 'LugarExacto', label: 'Lugar Exacto', icon: <FaMapMarked size={20} />, key: 'lugarExacto' },
    { name: 'EstadoRegistro', label: 'Estado Registro', icon: <FaCheck size={20} />, key: 'estadoRegistro' },
    { name: 'Fuentes', label: 'Fuentes', icon: <FaBook size={20} />, key: 'fuentes' },
    { name: 'Paises', label: 'Países', icon: <FaGlobe size={20} />, key: 'paises' },
    { name: 'Derechos', label: 'Derechos', icon: <FaShieldAlt size={20} />, key: 'derechos' },
    { name: 'Roles', label: 'Roles', icon: <FaUserTag size={20} />, key: 'roles' },
    { name: 'SecurityQuestions', label: 'Preguntas de Seguridad', icon: <FaQuestion size={20} />, key: 'securityQuestions' },
  ];

  return (
    <div 
      className="bg-light border-end position-sticky" 
      style={{ 
        width: '250px', 
        height: 'calc(100vh - 160px)', // Ajusta según la altura de navbar + footer
        top: '0',
        overflowY: 'auto',
        overflowX: 'hidden'
      }}
    >
      <div className="py-3">
        {catalogOptions.map(({ key, label, icon }) => (
          <button
            key={key}
            onClick={() => onSelectCatalog(key)}
            className="btn w-100 d-flex align-items-center text-start text-secondary p-3 border-0 bg-transparent hover-bg-light"
            style={{
              transition: 'background-color 0.2s ease',
              borderRadius: '0'
            }}
            onMouseEnter={(e) => {
              e.target.style.backgroundColor = '#f8f9fa';
            }}
            onMouseLeave={(e) => {
              e.target.style.backgroundColor = 'transparent';
            }}
          >
            <div className="me-3 text-secondary d-flex align-items-center justify-content-center" style={{ minWidth: '24px' }}>
              {icon}
            </div>
            <span className="text-truncate" title={label}>{label}</span>
          </button>
        ))}
      </div>
    </div>
  );
};

export default Sidenav;