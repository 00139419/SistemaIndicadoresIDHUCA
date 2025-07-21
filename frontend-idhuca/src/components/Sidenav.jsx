const Sidenav = ({ onSelectCatalog }) => {
  const catalogOptions = [
    {
      name: "Departamentos",
      label: "Departamentos",
      icon: "bi-building",
      key: "departamentos",
    },
    {
      name: "Municipios",
      label: "Municipios",
      icon: "bi-geo-alt-fill",
      key: "municipios",
    },
    {
      name: "Genero",
      label: "Género",
      icon: "bi-gender-ambiguous",
      key: "genero",
    },
    {
      name: "TipoProcesoJudicial",
      label: "Tipo Proceso Judicial",
      icon: "bi-hammer",
      key: "tipoProcesoJudicial",
    },
    {
      name: "TipoDenunciante",
      label: "Tipo Denunciante",
      icon: "bi-person-exclamation",
      key: "tipoDenunciante",
    },
    {
      name: "DuracionProceso",
      label: "Duración Proceso",
      icon: "bi-clock-history",
      key: "duracionProceso",
    },
    {
      name: "TipoRepresion",
      label: "Tipo Represión",
      icon: "bi-shield-exclamation",
      key: "tipoRepresion",
    },
    {
      name: "MedioExpresion",
      label: "Medio Expresión",
      icon: "bi-newspaper",
      key: "medioExpresion",
    },
    {
      name: "MotivoDetencion",
      label: "Motivo Detención",
      icon: "bi-person-x",
      key: "motivoDetencion",
    },
    {
      name: "TipoArma",
      label: "Tipo Arma",
      icon: "bi-exclamation-triangle",
      key: "tipoArma",
    },
    {
      name: "TipoDetencion",
      label: "Tipo Detención",
      icon: "bi-person-lock",
      key: "tipoDetencion",
    },
    {
      name: "TipoViolencia",
      label: "Tipo Violencia",
      icon: "bi-exclamation-octagon",
      key: "tipoViolencia",
    },
    {
      name: "EstadoSalud",
      label: "Estado Salud",
      icon: "bi-heart-pulse",
      key: "estadoSalud",
    },
    {
      name: "TipoPersona",
      label: "Tipo Persona",
      icon: "bi-person-badge",
      key: "tipoPersona",
    },
    {
      name: "Contexto",
      label: "Contexto",
      icon: "bi-info-circle",
      key: "contexto",
    },
    {
      name: "LugarExacto",
      label: "Lugar Exacto",
      icon: "bi-pin-map",
      key: "lugarExacto",
    },
    {
      name: "EstadoRegistro",
      label: "Estado Registro",
      icon: "bi-check-circle",
      key: "estadoRegistro",
    },
    { name: "Fuentes", label: "Fuentes", icon: "bi-book", key: "fuentes" },
    { name: "Paises", label: "Países", icon: "bi-globe", key: "paises" },
    {
      name: "Derechos",
      label: "Derechos",
      icon: "bi-shield-check",
      key: "derechos",
    },
    {
      name: "Roles",
      label: "Roles",
      icon: "bi-person-badge-fill",
      key: "roles",
    },
    {
      name: "SecurityQuestions",
      label: "Preguntas de Seguridad",
      icon: "bi-question-circle",
      key: "securityQuestions",
    },
  ];

  return (
    <div
      className="bg-light border-end position-sticky"
      style={{
        width: "250px",
        height: "calc(100vh - 160px)", // Ajusta según la altura de navbar + footer
        top: "0",
        overflowY: "auto",
        overflowX: "hidden",
      }}
    >
      <div className="py-3">
        {catalogOptions.map(({ key, label, icon }) => (
          <button
            key={key}
            onClick={() => onSelectCatalog(key)}
            className="btn w-100 d-flex align-items-center text-start text-secondary p-3 border-0"
            style={{
              borderRadius: "0",
            }}
          >
            <div
              className="me-3 text-secondary d-flex align-items-center justify-content-center"
              style={{ minWidth: "24px" }}
            >
              <i className={`bi ${icon}`} style={{ fontSize: "18px" }}></i>
            </div>
            <span className="text-truncate" title={label}>
              {label}
            </span>
          </button>
        ))}
      </div>
    </div>
  );
};

export default Sidenav;
