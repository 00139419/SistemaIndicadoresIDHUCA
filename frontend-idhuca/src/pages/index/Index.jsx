import logo from '../../assets/LogoMain.png';
const Index = () => {
  return (
    <div className="container-fluid px-4 py-5">
      {/* Título principal */}
      <div className="row mb-5">
        <div className="col-12 text-center">
          <h1 className="display-4 fw-bold">Bienvenido al sistema de indicadores<br />del IDHUCA</h1>
        </div>
      </div>

      {/* Contenido principal: Logo y descripción */}
      <div className="row align-items-center">
        {/* Columna del logo */}
        <div className="col-md-5 text-center mb-4 mb-md-0">
          <img
            src={logo}
            alt="Logo OUDH - Observatorio Universitario de Derechos Humanos"
            className="img-fluid"
            style={{ maxWidth: '350px' }}
          />
        </div>

        {/* Columna de texto */}
        <div className="col-md-7">
          <h2 className="fw-bold mb-3">Acerca del Proyecto</h2>

          <p className="mb-3">
            El Sistema de Indicadores para el IDHUCA es una plataforma diseñada
            para centralizar y analizar información clave sobre derechos humanos y
            justicia social. Su propósito es facilitar la recopilación y visualización de
            datos provenientes de diversas fuentes, permitiendo una interpretación
            clara y fundamentada.
          </p>

          <p>
            Mediante la centralización de datos y la generación de gráficos
            dinámicos, el sistema transforma la información en un recurso accesible
            que apoya la toma de decisiones y la incidencia en políticas públicas. Al
            unificar los datos en un solo espacio digital, mejora el seguimiento de
            tendencias y optimiza los procesos de análisis, facilitando el trabajo del
            IDHUCA en la defensa de los derechos humanos.
          </p>
        </div>
      </div>
    </div>
  );
}

export default Index;
